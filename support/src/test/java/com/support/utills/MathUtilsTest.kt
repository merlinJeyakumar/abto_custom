package com.support.utills

import org.junit.Test

class MathUtilsTest {

    @Test
    fun calculateDiscounted() {
    }

    @Test
    fun calculateBonus() {
        val totalAmount = 108.50
        val percentageValue = 30.00
        var totAmount = totalAmount
        val discount: Double = totalAmount * (percentageValue / 100.00)
        totAmount += discount
        System.out.println("Bonus Amount : "+totAmount)
    }
}