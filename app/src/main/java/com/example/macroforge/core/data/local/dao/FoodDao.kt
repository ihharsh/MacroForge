package com.example.macroforge.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.macroforge.core.data.local.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

// dao/FoodDao.kt
@Dao
interface FoodDao {
    @Query("SELECT COUNT(*) FROM foods")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<FoodEntity>)

    @Insert
    suspend fun insert(food: FoodEntity)

    @Query("SELECT * FROM foods ORDER BY foodName ASC")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE foodId = :foodId")
    suspend fun getFoodById(foodId: String): FoodEntity?

    @Query("SELECT * FROM foods WHERE foodName LIKE '%' || :query || '%'")
    fun searchFoods(query: String): Flow<List<FoodEntity>>

    // Paged variant for the Food Database browse screen — an empty query
    // matches every row, so this also serves the unfiltered "browse all" case.
    @Query("SELECT * FROM foods WHERE (:query = '' OR foodName LIKE '%' || :query || '%') ORDER BY foodName ASC")
    fun getFoodsPaged(query: String): PagingSource<Int, FoodEntity>
}