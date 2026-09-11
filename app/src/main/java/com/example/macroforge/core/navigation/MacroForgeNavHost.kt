package com.example.macroforge.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.macroforge.core.ui.components.PlaceholderScreen
import com.example.macroforge.feature_meals.presentation.createMealScreen.CreateMealRoute
import com.example.macroforge.feature_meals.presentation.mealDetailScreen.MealDetailRoute
import com.example.macroforge.feature_meals.presentation.savedMealScreen.SavedMealsRoute

@Composable
fun MacroForgeNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.SavedMeals.route
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
            CreateMealRoute(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSaveMeal = { navController.popBackStack() }
            )
        }
        composable(NavRoute.SearchFood.route) {
            PlaceholderScreen("Search Food") {}
        }
        composable(NavRoute.SaveMeal.route) {
            PlaceholderScreen("Save Meal") {}
        }
        composable(NavRoute.SavedMeals.route) {
            SavedMealsRoute(
                onNavigateToCreateMeal = { navController.navigate(NavRoute.CreateMeal.route) },
                onNavigateToMealDetail = { mealId ->
                    navController.navigate(NavRoute.MealDetail.createRoute(mealId))
                },
                onNavigateToHome = {},
                onNavigateToFoods = {},
                onNavigateToLog = {},
                onNavigateToProfile = {},
            )
        }
        composable(
            route = NavRoute.MealDetail.route,
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) {
            MealDetailRoute(onNavigateBack = { navController.popBackStack() })
        }
    }
}
