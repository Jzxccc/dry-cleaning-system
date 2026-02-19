package com.chaser.drycleaningsystem

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

/**
 * 充值功能单元测试
 * 测试充值计算逻辑
 */
class RechargeCalculationTest {

    @Test
    fun calculateGiftAmount_returnsCorrectAmount() {
        // 充 100 送 20%
        assertEquals(20.0, calculateGiftAmount(100.0), 0.01)
        assertEquals(40.0, calculateGiftAmount(200.0), 0.01)
        assertEquals(100.0, calculateGiftAmount(500.0), 0.01)
    }

    @Test
    fun calculateGiftAmount_returnsZeroForInvalidAmount() {
        assertEquals(0.0, calculateGiftAmount(0.0), 0.01)
        assertEquals(0.0, calculateGiftAmount(-100.0), 0.01)
    }

    @Test
    fun calculateTotalAmount_returnsCorrectTotal() {
        // 充值金额 + 赠送金额
        assertEquals(120.0, calculateTotalAmount(100.0), 0.01)
        assertEquals(240.0, calculateTotalAmount(200.0), 0.01)
        assertEquals(600.0, calculateTotalAmount(500.0), 0.01)
    }

    @Test
    fun rechargeAmountValidation_mustBeMultipleOf100() {
        // 测试充值金额验证逻辑
        assertTrue(isValidRechargeAmount("100"))
        assertTrue(isValidRechargeAmount("200"))
        assertTrue(isValidRechargeAmount("500"))
        assertTrue(!isValidRechargeAmount("50"))
        assertTrue(!isValidRechargeAmount("150"))
        assertTrue(!isValidRechargeAmount("abc"))
        assertTrue(!isValidRechargeAmount(""))
    }

    private fun calculateGiftAmount(amount: Double): Double {
        if (amount <= 0) return 0.0
        return amount * 0.2
    }

    private fun calculateTotalAmount(amount: Double): Double {
        if (amount <= 0) return 0.0
        return amount + calculateGiftAmount(amount)
    }

    private fun isValidRechargeAmount(amount: String): Boolean {
        if (amount.isBlank()) return false
        val value = amount.toDoubleOrNull() ?: return false
        if (value <= 0) return false
        return value % 100 == 0.0
    }
}
