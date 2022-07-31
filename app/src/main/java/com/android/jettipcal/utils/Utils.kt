package com.android.jettipcal.utils

import androidx.compose.runtime.MutableState


fun calculateTip(totalValue: Double, percentage: Int): Double {
    return if (totalValue.toString().isNotEmpty() && totalValue > 1) {
        (totalValue * percentage) / 100
    } else {
        0.0
    }
}
fun calculateTotalPerson(totalBil: Double, splitBy: Int, tipPercentage: Int): Double {
    val bill = calculateTip(totalBil, tipPercentage) + totalBil
    return bill / splitBy
}