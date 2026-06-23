package com.example.macroforge.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.macroforge.core.ui.components.PlaceholderScreen

@Composable
fun MacroForgeNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.Splash.route
    ) {
        composable(NavRoute.Splash.route) {
            PlaceholderScreen("Splash") { navController.navigate(NavRoute.SignIn.route) }
        }
        composable(NavRoute.SignIn.route) {
            PlaceholderScreen("Sign In") { navController.navigate(NavRoute.Home.route) }
        }
        composable(NavRoute.Home.route) {
            PlaceholderScreen("Home") { navController.navigate(NavRoute.FoodDatabase.route) }
        }
        composable(NavRoute.FoodDatabase.route) {
            PlaceholderScreen("Food Database") {}
        }
        composable(NavRoute.AddFood.route) {
            PlaceholderScreen("Add Food") {}
        }
        composable(NavRoute.CreateMeal.route) {
            PlaceholderScreen("Create Meal") {}
        }
        composable(NavRoute.SearchFood.route) {
            PlaceholderScreen("Search Food") {}
        }
        composable(NavRoute.SaveMeal.route) {
            PlaceholderScreen("Save Meal") {}
        }
        composable(NavRoute.SavedMeals.route) {
            PlaceholderScreen("Saved Meals") {}
        }
        composable(
            route = NavRoute.MealDetail.route,
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
            PlaceholderScreen("Meal Detail: $mealId") {}
        }
    }
}