package com.chaser.drycleaningsystem.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ============ Material Design 3 形状系统 ============
// 定义组件的圆角和形状令牌

val Shapes = Shapes(
    // ============ 小形状 (4dp) ============
    // 用于：复选框、小图标、徽章
    extraSmall = RoundedCornerShape(4.dp),
    
    // ============ 中形状 (8dp) ============
    // 用于：按钮、输入框、芯片
    small = RoundedCornerShape(8.dp),
    
    // ============ 中大形状 (12dp) ============
    // 用于：卡片、对话框
    medium = RoundedCornerShape(12.dp),
    
    // ============ 大形状 (16dp) ============
    // 用于：大型卡片、底部 sheet
    large = RoundedCornerShape(16.dp),
    
    // ============ 超大形状 (28dp) ============
    // 用于：模态对话框、大型容器
    extraLarge = RoundedCornerShape(28.dp)
)

// ============ 组件特定形状 ============

// 按钮形状
val ButtonShape = RoundedCornerShape(8.dp)
val FilledButtonShape = RoundedCornerShape(8.dp)
val OutlinedButtonShape = RoundedCornerShape(8.dp)
val TextButtonShape = RoundedCornerShape(8.dp)

// 卡片形状
val CardShape = RoundedCornerShape(12.dp)
val ElevatedCardShape = RoundedCornerShape(12.dp)
val FilledCardShape = RoundedCornerShape(12.dp)
val OutlinedCardShape = RoundedCornerShape(12.dp)

// 输入框形状
val TextFieldShape = RoundedCornerShape(8.dp)
val OutlinedTextFieldShape = RoundedCornerShape(8.dp)

// 对话框形状
val DialogShape = RoundedCornerShape(28.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomEnd = 0.dp, bottomStart = 0.dp)

// 芯片形状
val ChipShape = RoundedCornerShape(8.dp)
val FilterChipShape = RoundedCornerShape(8.dp)

// 导航栏形状
val NavigationBarShape = RoundedCornerShape(0.dp)
val NavigationRailShape = RoundedCornerShape(0.dp)

// 搜索栏形状
val SearchBarShape = RoundedCornerShape(28.dp)

// FAB 形状
val FabShape = RoundedCornerShape(16.dp)
val SmallFabShape = RoundedCornerShape(12.dp)
