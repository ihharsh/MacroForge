package com.example.macroforge

import com.example.macroforge.core.data.local.entity.FoodEntity
import com.example.macroforge.feature_meals.domain.usecase.CalculateMealMacrosUseCase
import junit.framework.TestCase.assertEquals
import org.junit.Test

// app/src/test/java/.../CalculateMealMacrosUseCaseTest.kt
class CalculateMealMacrosUseCaseTest {

    private val useCase = CalculateMealMacrosUseCase()

    @Test
    fun `calculates macros correctly for scaled quantity`() {
        val egg = FoodEntity("f1", "Egg", 1f, "egg", 77f, 0.6f, 6.5f, 5.2f)
        val result = useCase(listOf(egg), mapOf("f1" to 2f)) // 2 eggs

        assertEquals(154f, result.calories, 0.01f)
        assertEquals(13f, result.protein, 0.01f)
    }
}