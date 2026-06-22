# MacroForge — Technical Blueprint
**Offline-First Nutrition & Meal Tracking App | Phase 1 Implementation Plan**

Timeline assumption: 1-3 hours/day, intermediate Android skills, 3-4 months total.

---

## 1. Tech Stack (with rationale)

| Layer | Choice | Why | Alternative | Why this wins |
|---|---|---|---|---|
| Language | Kotlin | Standard, coroutines/Flow native | Java | No contest in 2026 |
| UI | Jetpack Compose | Modern declarative UI, resume-relevant | XML Views | Views are legacy; every SDE-1 JD now expects Compose |
| Architecture | MVVM + Clean Architecture (light) | Testable, standard, interview-friendly | MVI | MVI adds ceremony you don't need for Phase 1; revisit if state gets complex |
| DI | Hilt | Official, less boilerplate, Compose-friendly | Koin | Koin is runtime DI (slower failure detection); Hilt fails at compile time |
| Local DB | Room | Officially recommended, SQLite under the hood, Flow support | Realm | Realm is heavier, less idiomatic with Compose/Flow, declining community support |
| Networking | Retrofit + OkHttp (only if you add non-Firebase APIs later) | Industry standard | Ktor client | Retrofit has more docs/examples for your skill level right now |
| Remote DB | Firebase Firestore | You specified Firebase; pairs naturally with offline Room cache pattern | Supabase | Firebase has the most mature Android SDK + free tier |
| Auth | Firebase Auth (Google Sign-In + Email) | Native pairing with Firestore rules | Custom backend | Massive overkill for Phase 1 |
| Serialization | kotlinx.serialization | Kotlin-first, no reflection overhead | Gson/Moshi | Gson uses reflection (slower, less safe); kotlinx is modern default |
| Background work | WorkManager | Handles sync, survives process death, battery-aware | AlarmManager / Services | WorkManager is built for exactly this (deferred, guaranteed background work) |
| Image loading | Coil | Compose-first, Kotlin-native, lightweight | Glide | Glide is View-based legacy; Coil is the Compose-era standard |
| Local secure storage | Jetpack Security (EncryptedSharedPreferences) / DataStore + Keystore | Needed for auth tokens | Plain SharedPreferences | Plaintext token storage is a real vulnerability, easy interview gotcha |
| Testing | JUnit5 + Turbine (Flow testing) + MockK | Kotlin-first mocking, coroutine/Flow test support | Mockito | MockK handles Kotlin `final` classes and coroutines far better |
| Crash reporting | Firebase Crashlytics | Free, integrates directly with your existing Firebase project | Sentry | No reason to add a second vendor for Phase 1 |
| Analytics | Firebase Analytics | Same reasoning — already in your stack | Mixpanel | Unnecessary cost/complexity for Phase 1 |

---

## 2. Architecture Decisions

### Clean Architecture — Yes, but lightweight
Three layers per feature:
- **data** — Room DAOs/entities, Firestore data sources, repository implementations
- **domain** — Use cases (plain Kotlin classes, no Android dependencies), domain models
- **presentation** — ViewModels, Compose UI, UI state

Don't over-engineer Phase 1 with excessive use-case classes for trivial CRUD (e.g. "GetFoodByIdUseCase" wrapping a one-line DAO call adds no value). Reserve use cases for logic with actual business rules:
- `CalculateMealMacrosUseCase`
- `SyncMealsUseCase`
- `SearchFoodsUseCase` (with custom-food merging logic)

Trivial reads (e.g., "get all foods") can go ViewModel → Repository directly. This is a defensible, senior-level call you can explain in interviews: "I used use cases only where there was real domain logic, not as a blanket pattern."

### MVVM, not MVI
MVI is the more "advanced" pattern, but for Phase 1 scope (no complex multi-event state machines), MVVM with a single `UiState` sealed class per screen gives you 90% of MVI's benefits with much less boilerplate. Revisit MVI only if Phase 2 AI features introduce genuinely complex async state (streaming AI responses, multi-step flows).

### Repository Pattern
One repository per domain entity (`FoodRepository`, `MealRepository`, `AuthRepository`). Each repository:
- Exposes Room as the single read source (`Flow<List<T>>`)
- Writes go to Room first, then a sync queue pushes to Firestore
- Never expose Firestore objects directly into ViewModels — always map to domain models

### Offline-First / Sync Architecture
**Single source of truth = Room.** Firestore is sync transport, not the source of truth for reads.

Flow:
1. UI reads only from Room (via Flow) — always fast, always available offline
2. Writes go to Room immediately (optimistic), marked with a `syncStatus` field (`PENDING`, `SYNCED`, `FAILED`)
3. WorkManager periodic + immediate-trigger worker pushes `PENDING` rows to Firestore
4. On success, update `syncStatus = SYNCED`
5. Master food DB: one-time/periodic pull from Firestore → Room (read-only for users)

**Conflict resolution (Phase 1, keep simple):** Last-write-wins using a `updatedAt` timestamp. Document this explicitly as a known simplification — a true CRDT/merge strategy is Phase 2+ territory and overkill for a single-user meal tracker (no real-time collaboration).

```
Sequence: Save Meal
User taps Save
   -> ViewModel calls MealRepository.saveMeal(meal)
   -> Repository inserts into Room (status=PENDING) [returns immediately, UI updates]
   -> Repository enqueues OneTimeWorkRequest(SyncMealWorker)
   -> Worker (when network available) pushes to Firestore
   -> On success: Room update syncStatus=SYNCED
   -> On failure: WorkManager retries with backoff; status stays PENDING
```

```
Sequence: First Launch (Master Food DB Sync)
App launch
   -> Check Room: food_items count == 0?
   -> Yes -> Show "Setting up..." loading state
   -> Fetch master_food_database collection from Firestore
   -> Bulk insert into Room
   -> Mark local flag "master_db_synced = true" in DataStore
   -> Future launches: skip fetch unless a version flag in Firestore changes (incremental update check)
```

### Error Handling Strategy
- Domain layer returns sealed `Result<T>` (success/failure) — never throw across layers
- Network/Firestore failures map to typed errors (`NetworkError`, `AuthError`, `SyncError`) so the UI can show specific messages, not generic "Something went wrong"
- Room operations wrapped in try/catch at repository boundary only — don't scatter try/catch through ViewModels

### Security Strategy (what's actually necessary for Phase 1)
- **Firebase Security Rules**: users can only read/write their own `users/{uid}/...` subcollections; `master_food_database` is read-only for all authenticated users, writable only via admin SDK/console
- **No API keys to hide** — Firebase config (`google-services.json`) is not a secret; security comes entirely from Firestore Rules, not obscuring the config file
- **Auth tokens**: handled automatically by Firebase Auth SDK (don't roll your own token storage)
- **Encrypted DataStore**: only needed if you cache anything sensitive beyond what Firebase SDK already manages — for Phase 1, default Firebase Auth session handling is sufficient
- **Rooted device considerations**: not a priority for Phase 1 (no payment/health-critical data) — skip SafetyNet/Play Integrity for now, revisit if you add premium features later
- **Backup**: exclude Room DB from Android auto-backup if it ever contains anything sensitive (`android:fullBackupContent` in manifest) — for Phase 1, Firestore is your real backup, so this is a minor hardening step, not a blocker

---

## 3. Database Design

### Room Entities

```kotlin
@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey val id: String,        // matches Firestore doc id
    val name: String,
    val baseQty: Float,
    val unit: String,                  // "g", "ml", "pc", "slice", "egg"
    val calories: Float,
    val carbs: Float,
    val protein: Float,
    val fats: Float,
    val isCustom: Boolean,             // false = master DB, true = user-created
    val ownerId: String?,              // null for master DB items
    val updatedAt: Long
)

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val name: String,
    val tag: String,                   // Breakfast/Lunch/Dinner/Snack/PreWorkout/PostWorkout
    val createdAt: Long,
    val updatedAt: Long,
    val syncStatus: String             // PENDING / SYNCED / FAILED
)

@Entity(
    tableName = "meal_food_cross_ref",
    primaryKeys = ["mealId", "foodItemId"],
    foreignKeys = [
        ForeignKey(entity = MealEntity::class, parentColumns = ["id"], childColumns = ["mealId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = FoodItemEntity::class, parentColumns = ["id"], childColumns = ["foodItemId"])
    ],
    indices = [Index("foodItemId")]
)
data class MealFoodCrossRef(
    val mealId: String,
    val foodItemId: String,
    val quantityEntered: Float
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val displayName: String,
    val email: String?,
    val onboardingComplete: Boolean
)
```

### Relations (Room `@Relation`)

```kotlin
data class MealWithFoods(
    @Embedded val meal: MealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MealFoodCrossRef::class,
            parentColumn = "mealId",
            entityColumn = "foodItemId"
        )
    )
    val foods: List<FoodItemEntity>
)
```

### Text Schema Diagram

```
users
 └── uid (PK)

food_items
 └── id (PK)
     ownerId (nullable FK -> users.uid, null = master DB)
     isCustom

meals
 └── id (PK)
     ownerId (FK -> users.uid)
     tag, syncStatus

meal_food_cross_ref  (junction table)
 └── mealId (FK -> meals.id)      [composite PK]
     foodItemId (FK -> food_items.id)
     quantityEntered

Relationships:
users 1 ---- * meals
users 1 ---- * food_items (custom only)
meals * ---- * food_items   (via meal_food_cross_ref)
```

### Indexes
- `meal_food_cross_ref.foodItemId` — speeds up "which meals use this food" lookups
- `meals(ownerId, tag)` — composite index for tag-filtered search
- `food_items(name)` — for fast search-as-you-type

---

## 4. Firebase Firestore Design

```
master_food_database/
  {foodId}/
    name, calories, carbs, protein, fats, baseQty, unit, updatedAt
    (read-only for clients, written only via console/admin SDK)

users/
  {uid}/
    profile: { displayName, email, onboardingComplete }

    custom_foods/
      {foodId}/
        name, calories, carbs, protein, fats, baseQty, unit, updatedAt

    meals/
      {mealId}/
        name, tag, createdAt, updatedAt
        foodEntries: [
          { foodId, quantityEntered }
        ]
```

**Why nest `foodEntries` as an array inside the meal document instead of a subcollection:** meal-food relationships are small (rarely >15 items per meal) and always read/written together with the meal — no need for the extra read cost of a subcollection. This is a deliberate denormalization, and a good thing to be able to explain in interviews: "I chose embedding over subcollections because the access pattern was always read-the-whole-meal-at-once."

**Why `master_food_database` is top-level, not per-user:** it's shared, read-only reference data — duplicating it per-user would be wasteful reads/writes and an unnecessary sync complexity.

### Firestore Security Rules (concept)

```
match /users/{userId}/{document=**} {
  allow read, write: if request.auth.uid == userId;
}
match /master_food_database/{foodId} {
  allow read: if request.auth != null;
  allow write: if false; // admin-only via console/admin SDK
}
```

---

## 5. Project Structure

**Decision: Single module for Phase 1.** Multi-module is the "impressive-sounding" choice, but for a solo dev building one feature set in 3-4 months, multi-module adds real build-config overhead (Gradle setup, module boundaries, navigation between modules) without a real payoff — you don't have a team needing parallel build isolation. Package-by-feature inside a single module gives you the same organizational clarity. **This is a defensible, senior decision to explain**: "I evaluated multi-module but the team size and project scope didn't justify the build overhead — package-by-feature gave the same separation of concerns."

Revisit multi-module only if Phase 2 AI features grow large enough to warrant a `:feature_ai` module with its own heavy dependencies (MediaPipe, model files) that you want excluded from debug build times.

```
app/
 └── MacroForgeApp.kt, MainActivity.kt

core/
 ├── data/            # Room DB instance, Firestore instance, DataStore
 ├── domain/          # Result wrapper, common domain models
 ├── di/              # Hilt modules (DatabaseModule, FirebaseModule, NetworkModule)
 ├── sync/            # WorkManager workers, SyncStatus enum
 └── ui/              # Theme, shared composables, design tokens

feature_auth/
 ├── data/            # AuthRepositoryImpl
 ├── domain/          # SignInUseCase, AuthRepository interface
 └── presentation/    # OnboardingScreen, LoginScreen, AuthViewModel

feature_home/
 └── presentation/    # HomeScreen, HomeViewModel

feature_foods/
 ├── data/            # FoodDao, FoodRepositoryImpl, FirestoreFoodSyncSource
 ├── domain/          # SearchFoodsUseCase, CreateCustomFoodUseCase
 └── presentation/    # FoodDatabaseScreen, AddFoodScreen, FoodViewModel

feature_meals/
 ├── data/            # MealDao, MealFoodCrossRefDao, MealRepositoryImpl
 ├── domain/          # CalculateMealMacrosUseCase, SaveMealUseCase, SearchMealsUseCase
 └── presentation/    # CreateMealScreen, SavedMealsScreen, MealViewModel
```

---

## 6. Implementation Roadmap (Weekly, 1-3 hrs/day)

| Week | Focus | Hours | Outcome |
|---|---|---|---|
| 1 | Project setup: Gradle, Hilt, Compose theme, navigation skeleton | 10-15 | Empty app runs, navigates between blank screens |
| 2 | Room schema: all entities, DAOs, database class, migrations setup | 10-15 | Room compiles, can insert/query test data |
| 3 | Firebase setup: project, Auth (Google + Email), Firestore rules | 10-15 | User can sign in, profile doc created |
| 4 | Onboarding flow + Home screen empty state | 8-12 | "Hey [Name]" + empty state UI working |
| 5 | Master food DB sync: Firestore → Room on first launch, loading state | 10-15 | Food DB populates from Firebase on fresh install |
| 6 | Food Database screen: list, search, view food details | 10-15 | User can browse/search master food DB |
| 7 | Custom Food creation flow (form + save to Room + sync queue) | 10-15 | User can add "Grandma's Paneer" type items |
| 8 | Create Meal screen: search food, add to meal, quantity input | 12-18 | Can build a meal from food items |
| 9 | Live macro calculation logic + UI binding | 8-12 | Macros update instantly as quantities change |
| 10 | Save Meal flow: name, tag picker, persist to Room | 8-12 | Meal saves locally with syncStatus=PENDING |
| 11 | WorkManager sync engine: push pending meals/foods to Firestore | 10-15 | Background sync works, retries on failure |
| 12 | Saved Meals screen + Search by name/tag | 8-12 | Fast local search across saved meals |
| 13 | "Add food during meal creation" flow (inline food creation) | 8-12 | Seamless food creation without losing meal context |
| 14 | Cloud restore flow: reinstall → pulls user's meals/custom foods | 8-12 | Verified reinstall recovers all user data |
| 15 | Polish pass: empty states, loading states, error states, dark mode check | 10-15 | App feels finished, no jank |
| 16 | Testing: unit tests for use cases, repository tests, manual QA pass | 10-15 | Core logic covered, confidence to ship |
| 17 | Play Store prep: app icon, screenshots, privacy policy, listing copy | 6-10 | Store listing ready |
| 18 | Internal testing track, bug fixes from real device testing | 8-12 | Stable release candidate |
| 19 | Public release + buffer for review feedback / rejection fixes | 5-8 | **Live on Play Store** |

**MVP-only path (if you want something installable sooner):** Weeks 1-10 give you a working, single-user, no-sync version (skip WorkManager/Firestore sync initially, add it after). This is a reasonable checkpoint if motivation dips — you'd have a usable local app by ~week 10.

---

## 7. Full TODO Checklist (in build order)

```
[ ] Initialize project (Compose, min/target SDK decisions)
[ ] Set up Git repo + .gitignore (exclude google-services.json from public repo, or use a template)
[ ] Add Hilt, set up Application class with @HiltAndroidApp
[ ] Set up Navigation (Compose Navigation, NavHost skeleton)
[ ] Define core theme: colors, typography, shapes (Material 3)
[ ] Create Room entities: FoodItemEntity, MealEntity, MealFoodCrossRef, UserEntity
[ ] Create DAOs: FoodDao, MealDao, MealFoodCrossRefDao
[ ] Create AppDatabase class, set up Hilt DatabaseModule
[ ] Write basic Room unit tests (insert/query) to validate schema early
[ ] Set up Firebase project (Console): Auth, Firestore, Crashlytics
[ ] Add Firebase SDK, google-services.json
[ ] Implement Google Sign-In flow
[ ] Implement Email Sign-In (optional, can defer)
[ ] Write Firestore security rules, test with Firebase emulator or console
[ ] Build Onboarding screens (welcome, sign-in, name capture)
[ ] Build Home screen: greeting + empty state
[ ] Implement master food DB seed: Firestore fetch -> Room bulk insert
[ ] Add DataStore flag: master_db_synced
[ ] Build Food Database screen: list + search (Room Flow + search query)
[ ] Build Add/Edit Custom Food form
[ ] Implement CreateCustomFoodUseCase (validation + Room insert + sync flag)
[ ] Build Create Meal screen: food search + selection
[ ] Implement quantity input + live macro calculation (CalculateMealMacrosUseCase)
[ ] Build "Add food inline" flow from within Create Meal (returns to meal screen after save)
[ ] Build Save Meal UI: name input, tag picker (chips)
[ ] Implement SaveMealUseCase: Room insert (meal + cross refs), syncStatus=PENDING
[ ] Set up WorkManager: SyncMealWorker, SyncFoodWorker
[ ] Implement sync push logic: Room PENDING rows -> Firestore
[ ] Implement retry/backoff handling for failed syncs
[ ] Build Saved Meals screen: list, filter by tag
[ ] Implement SearchMealsUseCase: name + tag search (Room query)
[ ] Implement reinstall restore flow: pull user meals/custom foods from Firestore -> Room
[ ] Add loading/empty/error states across all screens
[ ] Verify dark mode across all screens
[ ] Write unit tests: use cases, repository logic
[ ] Write a few UI tests for critical flows (create meal, save meal)
[ ] Manual QA pass on 2+ physical devices/emulators
[ ] Set up Crashlytics, verify crash reporting works
[ ] Create app icon, feature graphic, screenshots
[ ] Write privacy policy (required for Play Store — covers Firebase data usage)
[ ] Write Play Store listing copy (short/long description)
[ ] Create signed release build, set up Play App Signing
[ ] Submit to internal testing track
[ ] Fix issues found in internal testing
[ ] Submit for production review
[ ] Monitor Crashlytics + Play Console after launch
```

---

## 8. Estimation

- **MVP (local-only, no sync, single user)**: ~6-8 weeks at 1-3 hrs/day
- **Production-ready (full scope as specified, synced, polished, published)**: ~16-19 weeks at 1-3 hrs/day — matches your stated 3-4 month timeline well

### Most difficult technical challenges
1. **Sync engine correctness** — getting the PENDING/SYNCED/FAILED state machine right without race conditions (e.g., user edits a meal while it's mid-sync) is the trickiest part of this whole project
2. **Macro calculation correctness across units** — "1 egg" vs "100g" base units means your scaling math must branch correctly per unit type; a bug here silently produces wrong nutrition data, which is your core value prop
3. **First-launch master DB sync UX** — making the "downloading food database" wait feel acceptable rather than janky

### Biggest risks
- **Scope creep** — it's tempting to keep adding fields/features before anything ships. Lock Phase 1 scope now (this document) and resist expanding until v1 is live.
- **Firestore cost surprises** — if you don't batch reads (e.g., re-fetching the whole master DB every launch instead of caching), you can rack up unexpected read counts. Cache aggressively, sync incrementally.
- **Demotivation around week 10-12** — this is typically where side projects die, right after the "fun" UI work and before the "boring" sync/polish work. Knowing this in advance helps you push through it.

### Common mistakes to avoid
- Putting business logic in Composables instead of ViewModels/UseCases (hurts testability)
- Treating Firestore as the source of truth for reads (breaks offline-first — always read from Room)
- Not handling the "no internet during first launch" case for master DB sync (show a clear retry state, don't crash)
- Skipping the `updatedAt` timestamp early, then needing it later for conflict resolution and migration being painful

---

## 9. Phase 2 — Future AI Features (designed for now, built later)

To avoid a major refactor later, bake these seams into Phase 1:

1. **`FoodItemEntity` and `MealEntity` already model macros per-100g/per-unit** — this is exactly the structured data an AI recipe generator needs as input context. No schema change required.
2. **Add a `source` field to `MealEntity` now** (`MANUAL`, `AI_GENERATED`) even if unused in Phase 1 — costs nothing today, saves a migration later.
3. **Keep `CalculateMealMacrosUseCase` as the single source of macro math** — Phase 2 AI features should call this same use case to verify AI-suggested quantities, rather than trusting AI-generated numbers (a strong, accurate interview point: "the LLM never does arithmetic — it selects ingredients and language, deterministic code calculates macros").
4. **Repository layer already abstracts data source** — adding an `AiRecipeRepository` later (wrapping Gemini Nano/AICore or a MediaPipe `.task` model) plugs into the same Clean Architecture layering without touching `feature_meals` or `feature_foods` internals.
5. **Potential Phase 2 features once this foundation exists:**
   - AI meal generator from selected pantry items (the original "MacroForge" pitch)
   - AI recipe builder with macro constraints ("30g+ protein")
   - Personalized macro recommendations based on logged history
   - Image-based food recognition (ML Kit object detection → food lookup → quantity estimation)

Each of these is additive on top of this architecture — no rewrite required if Phase 1 is built as specified above.
