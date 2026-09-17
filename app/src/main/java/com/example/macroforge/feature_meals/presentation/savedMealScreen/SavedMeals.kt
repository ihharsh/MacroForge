package com.example.macroforge.feature_meals.presentation.savedMealScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.macroforge.core.domain.UiState
import com.example.macroforge.core.ui.theme.*
import java.time.Instant
import java.time.ZoneId

// ─────────────────────────────────────────────────────────────────────────────
// UI Models  (move to model/ package in real project)
// ─────────────────────────────────────────────────────────────────────────────

enum class MealTagUi(val label: String) {
    ALL("All"),
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snack"),
    PRE_WORKOUT("Pre Workout"),
    POST_WORKOUT("Post Workout")
}

enum class SyncStatus { SYNCED, PENDING }

data class SavedMealUiItem(
    val id: String,
    val name: String,
    val tag: MealTagUi,
    val time: String,           // e.g. "7:30 AM"
    val createdAt: Long,        // epoch millis — used to group by day
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val syncStatus: SyncStatus,
    val calorieGoal: Int        // daily calorie goal, from user preferences
)

// ─────────────────────────────────────────────────────────────────────────────
// Recency grouping — Today / Yesterday / Older, by local calendar day.
// Exact per-day breakdown for older history belongs to the upcoming Log
// screen (date navigation); this list only needs three buckets.
// ─────────────────────────────────────────────────────────────────────────────

private fun groupByRecency(meals: List<SavedMealUiItem>): List<Pair<String, List<SavedMealUiItem>>> {
    val zone = ZoneId.systemDefault()
    val today = Instant.now().atZone(zone).toLocalDate()
    val yesterday = today.minusDays(1)

    val buckets = linkedMapOf<String, MutableList<SavedMealUiItem>>()
    meals.forEach { meal ->
        val mealDate = Instant.ofEpochMilli(meal.createdAt).atZone(zone).toLocalDate()
        val label = when (mealDate) {
            today -> "TODAY"
            yesterday -> "YESTERDAY"
            else -> "OLDER"
        }
        buckets.getOrPut(label) { mutableListOf() }.add(meal)
    }

    // Fixed section order, regardless of which bucket was populated first.
    return listOf("TODAY", "YESTERDAY", "OLDER").mapNotNull { label ->
        buckets[label]?.let { label to it }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tag color helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun tagColor(tag: MealTagUi): Color = when (tag) {
    MealTagUi.BREAKFAST   -> Color(0xFFF0A500)   // amber
    MealTagUi.LUNCH       -> Color(0xFF2F7EFF)   // blue
    MealTagUi.DINNER      -> Color(0xFF7C3AED)   // purple
    MealTagUi.SNACK       -> Color(0xFF12B76A)   // green
    MealTagUi.PRE_WORKOUT -> Color(0xFFFF5E2C)   // orange
    MealTagUi.POST_WORKOUT -> Color(0xFF0EA5E9)  // sky blue
    MealTagUi.ALL         -> Orange
}

private fun tagBg(tag: MealTagUi): Color = when (tag) {
    MealTagUi.BREAKFAST   -> Color(0xFFFFF8E6)
    MealTagUi.LUNCH       -> Color(0xFFEBF3FF)
    MealTagUi.DINNER      -> Color(0xFFF5F3FF)
    MealTagUi.SNACK       -> Color(0xFFE8FAF2)
    MealTagUi.PRE_WORKOUT -> Color(0xFFFFF1EC)
    MealTagUi.POST_WORKOUT -> Color(0xFFE0F7FA)
    MealTagUi.ALL         -> OrangeBg
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SavedMealsScreen(
    mealsState: UiState<List<SavedMealUiItem>>,
    selectedTag: MealTagUi,
    searchQuery: String,
    isSearchActive: Boolean,
    totalKcalToday: Int,
    onTagSelected: (MealTagUi) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchToggled: () -> Unit,
    onCancelSearch: () -> Unit,
    onMealClicked: (SavedMealUiItem) -> Unit,
    onCreateMeal: () -> Unit,
    onFilterClicked: () -> Unit,
    onHomeClicked: () -> Unit,
    onFoodsClicked: () -> Unit,
    onLogClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onDeleteMeal: (SavedMealUiItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val mealCount = (mealsState as? UiState.Success)?.data?.size ?: 0

    Scaffold(
        containerColor = Background,
        bottomBar = {
            SavedMealsBottomNav(
                onHomeClicked    = onHomeClicked,
                onFoodsClicked   = onFoodsClicked,
                onLogClicked     = onLogClicked,
                onProfileClicked = onProfileClicked
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateMeal,
                containerColor = Orange,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create meal",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Top header + search ─────────────────────────────────────────
            Surface(color = Surface) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    // Title row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 8.dp
                            ),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Title + subtitle
                        Column {
                            Text(
                                text = "My Meals",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$mealCount meals · ",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "$totalKcalToday",
                                    fontSize = 13.sp,
                                    color = Orange,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " kcal today",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Search + Filter icon buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButtonPill(
                                icon = Icons.Default.Search,
                                onClick = onSearchToggled,
                                tint = Orange,
                                bg = OrangeBg
                            )
                            IconButtonPill(
                                icon = Icons.Default.Tune,
                                onClick = onFilterClicked,
                                tint = TextSecondary,
                                bg = SurfaceAlt
                            )
                        }
                    }

                    // Search bar (visible when active)
                    if (isSearchActive) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.5.dp, Orange, RoundedCornerShape(16.dp))
                                .background(Surface)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Orange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChanged,
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                ),
                                decorationBox = { inner ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            "Search meals...",
                                            fontSize = 15.sp,
                                            color = TextGhost,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    inner()
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Cancel",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                                modifier = Modifier.clickable(onClick = onCancelSearch)
                            )
                        }
                    }

                    // Tag filter chips — horizontal scroll
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 14.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MealTagUi.values().forEach { tag ->
                            TagChip(
                                label = tag.label,
                                isSelected = selectedTag == tag,
                                onClick = { onTagSelected(tag) }
                            )
                        }
                    }
                }
            }

            // ── Meal list — loading / error / empty / content ──────────────
            Box(modifier = Modifier.fillMaxSize()) {
                when (mealsState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Orange
                        )
                    }
                    is UiState.Error -> {
                        Text(
                            text = mealsState.message,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 32.dp),
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                    is UiState.Success -> {
                        val meals = mealsState.data
                        if (meals.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No meals yet",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Tap + to create your first meal",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            val groups = remember(meals) { groupByRecency(meals) }
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 16.dp,
                                    bottom = 24.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                groups.forEach { (sectionLabel, mealsInSection) ->
                                    item(key = "header_$sectionLabel") {
                                        Text(
                                            text = sectionLabel,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextGhost,
                                            letterSpacing = 0.9.sp,
                                            modifier = Modifier.padding(
                                                horizontal = 4.dp,
                                                vertical = 4.dp
                                            )
                                        )
                                    }

                                    items(mealsInSection, key = { it.id }) { meal ->
                                        SavedMealCard(
                                            meal = meal,
                                            onClick = { onMealClicked(meal) },
                                            onDelete = { onDeleteMeal(meal) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SavedMealCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SavedMealCard(
    meal: SavedMealUiItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSolid, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Row 1: Meal name  |  sync icon + chevron ──────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = meal.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.3).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Sync status icon
                    Icon(
                        imageVector = if (meal.syncStatus == SyncStatus.SYNCED)
                            Icons.Default.CheckCircle else Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = if (meal.syncStatus == SyncStatus.SYNCED) Green else TextGhost,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open meal",
                        tint = TextGhost,
                        modifier = Modifier.size(18.dp)
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete ${meal.name}",
                            tint = TextGhost,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // ── Row 2: Tag chip + time  |  calorie count ──────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tag pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(tagBg(meal.tag))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = meal.tag.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = tagColor(meal.tag)
                        )
                    }
                    // Time
                    Text(
                        text = meal.time,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Calorie count
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${meal.calories}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 28.sp
                    )
                    Text(
                        text = "KCAL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGhost,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Row 3: Macro chips + calorie bar ──────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // P chip
                MacroMiniChip(label = "P", value = "${meal.protein.toInt()}g", color = Blue, bg = BlueBg)
                // C chip
                MacroMiniChip(label = "C", value = "${meal.carbs.toInt()}g", color = Amber, bg = AmberBg)
                // F chip
                MacroMiniChip(label = "F", value = "${meal.fats.toInt()}g", color = Green, bg = GreenBg)

                Spacer(Modifier.width(4.dp))

                // Calorie progress bar
                val fraction = (meal.calories.toFloat() / meal.calorieGoal.toFloat()).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(BorderSolid)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .background(Orange)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MacroMiniChip — "P 63g" style compact chip
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MacroMiniChip(
    label: String,
    value: String,
    color: Color,
    bg: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TagChip — filter chip in horizontal scroll row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TagChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Orange else Color.Transparent)
            .border(
                width = 1.5.dp,
                color = if (isSelected) Orange else BorderSolid,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// IconButtonPill — search + filter buttons in top right
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun IconButtonPill(
    icon: ImageVector,
    onClick: () -> Unit,
    tint: Color,
    bg: Color
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Navigation
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SavedMealsBottomNav(
    onHomeClicked: () -> Unit,
    onFoodsClicked: () -> Unit,
    onLogClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    Surface(
        color = Surface,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = BorderSolid,
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem(
                icon = Icons.Outlined.Home,
                label = "Home",
                isActive = false,
                onClick = onHomeClicked
            )
            BottomNavItem(
                icon = Icons.Outlined.HourglassBottom,
                label = "Foods",
                isActive = false,
                onClick = onFoodsClicked
            )
            // Meals — active tab
            BottomNavItem(
                icon = Icons.Default.Restaurant,
                label = "Meals",
                isActive = true,
                onClick = {}
            )
            BottomNavItem(
                icon = Icons.Outlined.Book,
                label = "Log",
                isActive = false,
                onClick = onLogClicked
            )
            BottomNavItem(
                icon = Icons.Outlined.Person,
                label = "Profile",
                isActive = false,
                onClick = onProfileClicked
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Orange else TextGhost,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) Orange else TextGhost
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview sample data
// ─────────────────────────────────────────────────────────────────────────────

private val previewMeals = listOf(
    SavedMealUiItem(
        id = "1", name = "High Protein Breakfast",
        tag = MealTagUi.BREAKFAST, time = "7:30 AM", createdAt = System.currentTimeMillis(),
        calories = 594, protein = 63f, carbs = 43f, fats = 17f,
        syncStatus = SyncStatus.SYNCED, calorieGoal = 2000
    ),
    SavedMealUiItem(
        id = "2", name = "Pre Workout Fuel",
        tag = MealTagUi.PRE_WORKOUT, time = "11:00 AM", createdAt = System.currentTimeMillis(),
        calories = 412, protein = 38f, carbs = 52f, fats = 8f,
        syncStatus = SyncStatus.SYNCED, calorieGoal = 2000
    ),
    SavedMealUiItem(
        id = "3", name = "Chicken Rice Bowl",
        tag = MealTagUi.LUNCH, time = "1:15 PM",
        createdAt = System.currentTimeMillis() - 86_400_000L, // yesterday
        calories = 680, protein = 58f, carbs = 71f, fats = 14f,
        syncStatus = SyncStatus.PENDING, calorieGoal = 2000
    ),
    SavedMealUiItem(
        id = "4", name = "Evening Snack",
        tag = MealTagUi.SNACK, time = "4:00 PM",
        createdAt = System.currentTimeMillis() - 3 * 86_400_000L, // older
        calories = 220, protein = 12f, carbs = 28f, fats = 7f,
        syncStatus = SyncStatus.SYNCED, calorieGoal = 2000
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "Default")
@Composable
private fun SavedMealsScreenPreview() {
    MacroForgeTheme {
        SavedMealsScreen(
            mealsState = UiState.Success(previewMeals),
            selectedTag = MealTagUi.ALL,
            searchQuery = "",
            isSearchActive = false,
            totalKcalToday = 2351,
            onTagSelected = {},
            onSearchQueryChanged = {},
            onSearchToggled = {},
            onCancelSearch = {},
            onMealClicked = {},
            onCreateMeal = {},
            onFilterClicked = {},
            onHomeClicked = {},
            onFoodsClicked = {},
            onLogClicked = {},
            onProfileClicked = {},
            onDeleteMeal = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "Search active")
@Composable
private fun SavedMealsScreenSearchPreview() {
    MacroForgeTheme {
        SavedMealsScreen(
            mealsState = UiState.Success(previewMeals),
            selectedTag = MealTagUi.ALL,
            searchQuery = "chicken",
            isSearchActive = true,
            totalKcalToday = 2351,
            onTagSelected = {},
            onSearchQueryChanged = {},
            onSearchToggled = {},
            onCancelSearch = {},
            onMealClicked = {},
            onCreateMeal = {},
            onFilterClicked = {},
            onHomeClicked = {},
            onFoodsClicked = {},
            onLogClicked = {},
            onProfileClicked = {},
            onDeleteMeal = {}
        )
    }
}