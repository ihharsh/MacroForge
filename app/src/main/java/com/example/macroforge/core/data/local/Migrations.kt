package com.example.macroforge.core.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// core/data/local/Migrations.kt

// Adds the idempotency-tracking table used by the AppFunctions createMeal
// integration. Existing foods/meals tables are untouched.
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `app_function_meal_requests` (" +
                "`requestKey` TEXT NOT NULL, " +
                "`mealId` TEXT NOT NULL, " +
                "`createdAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`requestKey`))"
        )
    }
}
