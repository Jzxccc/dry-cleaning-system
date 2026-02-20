package com.chaser.drycleaningsystem.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 订单状态徽章
 */
@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        "UNWASHED" -> Color(0xFFFF9800) to "未洗"
        "WASHED" -> Color(0xFF2196F3) to "已洗"
        "FINISHED" -> Color(0xFF4CAF50) to "已取"
        else -> Color.Gray to status
    }

    Box(
        modifier = Modifier
            .background(color, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

/**
 * 支付方式文本
 */
@Composable
fun OrderPayTypeText(payType: String): String {
    return when (payType) {
        "CASH" -> "现金"
        "PREPAID" -> "储值"
        "UNPAID" -> "未支付"
        else -> payType
    }
}
