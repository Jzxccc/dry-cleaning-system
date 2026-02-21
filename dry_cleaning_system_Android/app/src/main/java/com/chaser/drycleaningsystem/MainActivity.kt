package com.chaser.drycleaningsystem

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.io.File
import com.chaser.drycleaningsystem.ui.components.navEnterTransition
import com.chaser.drycleaningsystem.ui.components.navExitTransition
import com.chaser.drycleaningsystem.ui.components.navPopEnterTransition
import com.chaser.drycleaningsystem.ui.components.navPopExitTransition
import com.chaser.drycleaningsystem.ui.dashboard.DashboardViewModel
import com.chaser.drycleaningsystem.data.DataInjection
import com.chaser.drycleaningsystem.data.entity.Customer
import com.chaser.drycleaningsystem.data.repository.CustomerRepository
import com.chaser.drycleaningsystem.ui.customer.CustomerEditDialog
import com.chaser.drycleaningsystem.ui.customer.CustomerListScreen
import com.chaser.drycleaningsystem.ui.customer.CustomerViewModel
import com.chaser.drycleaningsystem.ui.customer.CustomerDetailScreen
import com.chaser.drycleaningsystem.ui.dashboard.DashboardScreen
import com.chaser.drycleaningsystem.ui.navigation.HomeScreen
import com.chaser.drycleaningsystem.ui.order.CustomerSelectorDialog
import com.chaser.drycleaningsystem.ui.order.NewOrderScreen
import com.chaser.drycleaningsystem.ui.order.OrderDetailScreen
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
    val customerRepository = CustomerRepository(DataInjection.getDatabase(context).customerDao())

    NavHost(
        navController = navController,
        startDestination = "dashboard",
        enterTransition = { navEnterTransition() },
        exitTransition = { navExitTransition() },
        popEnterTransition = { navPopEnterTransition() },
        popExitTransition = { navPopExitTransition() }
    ) {
        composable("dashboard") {
            val viewModel: DashboardViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return DashboardViewModel(
                            orderRepository = DataInjection.getOrderRepository(context),
                            customerRepository = DataInjection.getCustomerRepository(context),
                            rechargeRecordRepository = DataInjection.getRechargeRecordRepository(context)
                        ) as T
                    }
                }
            )
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToCustomers = { navController.navigate("customers") },
                onNavigateToOrders = { navController.navigate("orders") },
                onNavigateToRecharge = { 
                    navController.navigate("recharge")
                    // 从充值页面返回时刷新数据
                    viewModel.refresh()
                },
                onNavigateToStatistics = { navController.navigate("statistics") }
            )
        }
        composable("customers") {
            val viewModel: CustomerViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return CustomerViewModel(
                            repository = DataInjection.getCustomerRepository(context),
                            orderRepository = DataInjection.getOrderRepository(context),
                            rechargeRecordRepository = DataInjection.getRechargeRecordRepository(context)
                        ) as T
                    }
                }
            )
            var showAddCustomerDialog by remember { mutableStateOf(false) }
            var customerToEdit by remember { mutableStateOf<Customer?>(null) }
            var selectedCustomerDetail by remember { mutableStateOf<Customer?>(null) }

            CustomerListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onAddCustomer = { showAddCustomerDialog = true },
                onEditCustomer = { customer -> customerToEdit = customer },
                onCustomerClick = { customer ->
                    Log.d("DRY CLEAN SYSTEM LOG", "导航到客户详情：${customer.id}")
                    navController.navigate("customer-detail/${customer.id}")
                }
            )

            // 添加客户对话框
            if (showAddCustomerDialog) {
                CustomerEditDialog(
                    onDismiss = { showAddCustomerDialog = false },
                    onConfirm = { name, phone, wechat, balance, note ->
                        viewModel.addCustomer(name, phone, wechat, balance, note)
                        showAddCustomerDialog = false
                    }
                )
            }

            // 编辑客户对话框
            if (customerToEdit != null) {
                CustomerEditDialog(
                    onDismiss = { customerToEdit = null },
                    onConfirm = { name, phone, wechat, balance, note ->
                        viewModel.updateCustomer(customerToEdit!!.id, name, phone, wechat, balance, note)
                        customerToEdit = null
                    },
                    customer = customerToEdit
                )
            }
        }
        composable("customer-detail/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")?.toLongOrNull()
            Log.d("DRY CLEAN SYSTEM LOG", "进入客户详情路由，customerId: $customerId")
            
            val viewModel: CustomerViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return CustomerViewModel(
                            repository = DataInjection.getCustomerRepository(context),
                            orderRepository = DataInjection.getOrderRepository(context),
                            rechargeRecordRepository = DataInjection.getRechargeRecordRepository(context)
                        ) as T
                    }
                }
            )
            
            // 使用 runBlocking 调用 suspend 函数
            val customerDetail = if (customerId != null) {
                Log.d("DRY CLEAN SYSTEM LOG", "开始获取客户详情数据，customerId: $customerId")
                kotlinx.coroutines.runBlocking {
                    val detail = viewModel.getCustomerDetail(customerId)
                    Log.d("DRY CLEAN SYSTEM LOG", "获取客户详情结果：${detail != null}, 订单数：${detail?.orders?.size}, 充值记录数：${detail?.rechargeRecords?.size}")
                    detail
                }
            } else {
                Log.d("DRY CLEAN SYSTEM LOG", "customerId 为 null")
                null
            }
            
            if (customerDetail != null) {
                Log.d("DRY CLEAN SYSTEM LOG", "显示客户详情页面：${customerDetail.customer.name}")
                CustomerDetailScreen(
                    customer = customerDetail.customer,
                    orders = customerDetail.orders,
                    rechargeRecords = customerDetail.rechargeRecords,
                    onNavigateBack = { 
                        Log.d("DRY CLEAN SYSTEM LOG", "从客户详情返回")
                        navController.popBackStack() 
                    },
                    onEditCustomer = { 
                        Log.d("DRY CLEAN SYSTEM LOG", "点击编辑客户（从详情页）")
                        /* TODO: 打开编辑对话框 */ 
                    }
                )
            } else {
                Log.d("DRY CLEAN SYSTEM LOG", "客户详情数据为空，无法显示页面")
            }
        }
        composable("orders") {
            val viewModel: OrderViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return OrderViewModel(
                            orderRepository = DataInjection.getOrderRepository(context),
                            customerRepository = DataInjection.getCustomerRepository(context),
                            clothesRepository = DataInjection.getClothesRepository(context),
                            context = context
                        ) as T
                    }
                }
            )
            val customerViewModel: CustomerViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return CustomerViewModel(
                            repository = DataInjection.getCustomerRepository(context),
                            orderRepository = DataInjection.getOrderRepository(context),
                            rechargeRecordRepository = DataInjection.getRechargeRecordRepository(context)
                        ) as T
                    }
                }
            )
            OrderListScreen(
                viewModel = viewModel,
                customerViewModel = customerViewModel,
                onNavigateBack = { navController.popBackStack() },
                onCreateOrder = { navController.navigate("new-order") },
                onOrderClick = { orderId -> navController.navigate("order-detail/$orderId") }
            )
        }
        composable("order-detail/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")?.toLongOrNull()
            val viewModel: OrderViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return OrderViewModel(
                            orderRepository = DataInjection.getOrderRepository(context),
                            customerRepository = DataInjection.getCustomerRepository(context),
                            clothesRepository = DataInjection.getClothesRepository(context),
                            context = context
                        ) as T
                    }
                }
            )
            
            if (orderId != null) {
                // 获取订单详情
                val orderDetail = viewModel.getOrderDetail(orderId)
                val order = orderDetail?.order
                
                // 获取客户信息
                val customer = order?.let {
                    kotlinx.coroutines.runBlocking {
                        try {
                            customerRepository.getCustomerById(it.customerId)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                
                if (order != null) {
                    OrderDetailScreen(
                        viewModel = viewModel,
                        orderId = orderId,
                        customerName = customer?.name ?: "未知客户",
                        customerPhone = customer?.phone,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
        composable("new-order") {
            val viewModel: OrderViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return OrderViewModel(
                            orderRepository = DataInjection.getOrderRepository(context),
                            customerRepository = DataInjection.getCustomerRepository(context),
                            clothesRepository = DataInjection.getClothesRepository(context),
                            context = context
                        ) as T
                    }
                }
            )
            NewOrderScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onCreateOrder = { customerId, payType, urgent, clothesList, photoPath ->
                    val orderId = viewModel.createOrder(customerId, payType, urgent, clothesList)
                    
                    // 添加调试日志
                    Log.d("DRY CLEAN SYSTEM LOG", "========== 创建订单后调试信息 ==========")
                    Log.d("DRY CLEAN SYSTEM LOG", "订单 ID: $orderId")
                    Log.d("DRY CLEAN SYSTEM LOG", "照片路径：${photoPath ?: "null"}")
                    
                    // 保存照片路径到数据库，并移动照片目录
                    if (orderId > 0 && photoPath != null) {
                        // 从照片路径中提取临时订单 ID
                        val tempOrderId = photoPath.substringAfter("order_").substringBefore("/")
                        Log.d("DRY CLEAN SYSTEM LOG", "临时订单 ID: $tempOrderId")
                        
                        // 移动照片目录从临时 ID 到实际订单 ID
                        val tempDir = File(context.filesDir, "photos/order_$tempOrderId")
                        val actualDir = File(context.filesDir, "photos/order_$orderId")
                        
                        Log.d("DRY CLEAN SYSTEM LOG", "临时目录：${tempDir.absolutePath}")
                        Log.d("DRY CLEAN SYSTEM LOG", "实际目录：${actualDir.absolutePath}")
                        Log.d("DRY CLEAN SYSTEM LOG", "临时目录存在：${tempDir.exists()}")
                        
                        if (tempDir.exists() && tempDir != actualDir) {
                            tempDir.copyRecursively(actualDir, overwrite = true)
                            tempDir.deleteRecursively()
                            Log.d("DRY CLEAN SYSTEM LOG", "照片目录已移动")
                        }
                        
                        // 更新数据库，保存新的照片路径
                        val newPhotoPath = photoPath.replace("order_$tempOrderId", "order_$orderId")
                        Log.d("DRY CLEAN SYSTEM LOG", "新照片路径：$newPhotoPath")
                        viewModel.savePhotoPath(orderId, newPhotoPath)
                        Log.d("DRY CLEAN SYSTEM LOG", "照片路径已保存到数据库")
                    } else {
                        Log.d("DRY CLEAN SYSTEM LOG", "跳过照片保存：orderId=$orderId, photoPath=${photoPath ?: "null"}")
                    }
                    Log.d("DRY CLEAN SYSTEM LOG", "==========================================")
                    
                    navController.popBackStack()
                }
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
