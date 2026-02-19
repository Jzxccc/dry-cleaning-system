package com.chaser.drycleaningsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaser.drycleaningsystem.data.DataInjection
import com.chaser.drycleaningsystem.ui.customer.CustomerListScreen
import com.chaser.drycleaningsystem.ui.customer.CustomerViewModel
import com.chaser.drycleaningsystem.ui.navigation.HomeScreen
import com.chaser.drycleaningsystem.ui.order.OrderViewModel
import com.chaser.drycleaningsystem.ui.order.OrderListScreen
import com.chaser.drycleaningsystem.ui.recharge.RechargeScreen
import com.chaser.drycleaningsystem.ui.recharge.RechargeViewModel
import com.chaser.drycleaningsystem.ui.statistics.StatisticsScreen
import com.chaser.drycleaningsystem.ui.statistics.StatisticsViewModel
import com.chaser.drycleaningsystem.ui.theme.DryCleaningSystemAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DryCleaningSystemAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToCustomers = { navController.navigate("customers") },
                onNavigateToOrders = { navController.navigate("orders") },
                onNavigateToRecharge = { navController.navigate("recharge") },
                onNavigateToStatistics = { navController.navigate("statistics") }
            )
        }
        composable("customers") {
            val viewModel: CustomerViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return CustomerViewModel(
                            DataInjection.getCustomerRepository(context)
                        ) as T
                    }
                }
            )
            CustomerListScreen(
                viewModel = viewModel,
                onAddCustomer = { },
                onEditCustomer = { }
            )
        }
        composable("orders") {
            val viewModel: OrderViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return OrderViewModel(
                            orderRepository = DataInjection.getOrderRepository(context),
                            customerRepository = DataInjection.getCustomerRepository(context),
                            clothesRepository = DataInjection.getClothesRepository(context)
                        ) as T
                    }
                }
            )
            OrderListScreen(
                viewModel = viewModel,
                onCreateOrder = { navController.navigate("new-order") },
                onOrderClick = { }
            )
        }
        composable("recharge") {
            val viewModel: RechargeViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return RechargeViewModel(
                            customerRepository = DataInjection.getCustomerRepository(context),
                            rechargeRecordRepository = DataInjection.getRechargeRecordRepository(context)
                        ) as T
                    }
                }
            )
            RechargeScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("statistics") {
            val viewModel: StatisticsViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return StatisticsViewModel(
                            orderRepository = DataInjection.getOrderRepository(context),
                            rechargeRecordRepository = DataInjection.getRechargeRecordRepository(context)
                        ) as T
                    }
                }
            )
            StatisticsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
