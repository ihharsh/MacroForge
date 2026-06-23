package com.example.macroforge.core.navigation

sealed class NavRoute(val route: String) {
    object Splash : NavRoute("splash")
    object SignIn : NavRoute("sign_in")
    object Home : NavRoute("home")
    object FoodDatabase : NavRoute("food_database")
    object AddFood : NavRoute("add_food")
    object CreateMeal : NavRoute("create_meal")
    object SearchFood : NavRoute("search_food")
    object SaveMeal : NavRoute("save_meal")
    object SavedMeals : NavRoute("saved_meals")
    object MealDetail : NavRoute("meal_detail/{mealId}") {
        fun createRoute(mealId: String) = "meal_detail/$mealId"
    }
}