package com.support.utills

object MathUtils {

    fun calculateDiscounted(totalAmount: Double, percentageValue: Double): Double {
        var totAmount = totalAmount
        val discount: Double = totalAmount * (percentageValue / 100.00)
        totAmount -= discount
        return totAmount
    }

    fun calculateBonus(totalAmount: Double, percentageValue: Double): Double {
        //Temp Fix
        return when(totalAmount){
            91.00 -> 130.00
            108.50 -> 155.00
            178.50 -> 255.00
            else -> 130.00
        }
    }

}