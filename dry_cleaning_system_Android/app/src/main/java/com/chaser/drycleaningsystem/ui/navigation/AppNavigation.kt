package com.chaser.drycleaningsystem.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaser.drycleaningsystem.ui.customer.CustomerEditDialog
import com.chaser.drycleaningsystem.ui.customer.CustomerListScreen

/**
 * App 导航
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "customers"
    ) {
        composable("customers") {
            CustomerListScreen(
                onAddCustomer = { /* Navigate to add */ },
                onEditCustomer = { /* Navigate to edit */ }
            )
        }
    }
}
