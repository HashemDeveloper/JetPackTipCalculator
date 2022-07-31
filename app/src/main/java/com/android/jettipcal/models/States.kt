package com.android.jettipcal.models

import androidx.compose.runtime.MutableState

data class States(
    val counterState: MutableState<Int>,
    val sliderState: MutableState<Float>,
    val billState: MutableState<String>,
    val tipAmtState: MutableState<Double>,
    val totalPerPersonState: MutableState<Double>,
    val percentageState: MutableState<Int>,
    val validCounterState: Boolean,
    val validState: Boolean
)
