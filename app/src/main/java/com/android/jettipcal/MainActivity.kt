package com.android.jettipcal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.jettipcal.components.InputField
import com.android.jettipcal.models.StateTypes
import com.android.jettipcal.models.States
import com.android.jettipcal.ui.theme.JetTipCalTheme
import com.android.jettipcal.utils.calculateTip
import com.android.jettipcal.utils.calculateTotalPerson
import com.android.jettipcal.widgets.RoundIconBt
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppEntryPoint {
                MainContent()
            }
        }
    }
    @Composable
    fun AppEntryPoint(content: @Composable () -> Unit) {
        JetTipCalTheme {
            Surface(
                color = MaterialTheme.colors.background
            ) {
                content()
            }
        }
    }

    /**
     * Top Header
     */
//    @Preview(name = "TopHeader")
    @Composable
    fun TopHeader(totalAmount: Double = 0.0) {
        Surface(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
//            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))) // same works!
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
            color = Color(0xFFE9D7F7)
        ) {
            Column(modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                val total = "%.2f".format(totalAmount)
                Text(text = "Total Per Person",
                style = MaterialTheme.typography.h5)
                Text(text = "$${total}", style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
    //Main Content
    @Preview(name = "Main Content")
    @Composable
    fun MainContent() {
        val sliderState = remember {
            mutableStateOf(0f)
        }
        val counterState = remember {
            mutableStateOf(1)
        }
        val validCounterState = remember(counterState.value) {
            counterState.value > 1
        }
        val billState = remember {
            mutableStateOf("")
        }
        val validState = remember(billState.value) {
            billState.value.trim().isNotEmpty()
        }
        val tipAmtState = remember {
            mutableStateOf(0.0)
        }
        val totalPerPersonState = remember {
            mutableStateOf(0.0)
        }
        val percentageState = remember {
            mutableStateOf(0)
        }
        val states = States(
            counterState,sliderState,billState,tipAmtState,totalPerPersonState,percentageState,validCounterState,validState
        )
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            BillForm(states = states) { billAmt ->
                Log.d("BILL", billAmt)
            }
        }
    }
@Composable
fun BillForm(modifier: Modifier = Modifier,states: States, onValueChanged: (String) -> Unit = {}, ) {
    val range = IntRange(start = 1, endInclusive = 100)
    val keyboardController = LocalSoftwareKeyboardController.current
    TopHeader(totalAmount = states.totalPerPersonState.value)
    Surface(modifier = modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(modifier = modifier.padding(6.dp), verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
            InputField(
                valueState = states.billState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onKeyboardAction = KeyboardActions{
                    if (!states.validState) return@KeyboardActions
                    onValueChanged(states.billState.value.trim())
                    keyboardController?.hide()
                })
            if (states.validState) {
                // Split row
                Row(
                    modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = "Split", modifier = modifier.align(
                        alignment = Alignment.CenterVertically
                    ))
                    Spacer(modifier = modifier.width(120.dp))
                    Row(modifier = modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {
                        RoundIconBt(imageVector = Icons.Default.Remove, onClick = {
                            if (states.validCounterState) states.counterState.value -= 1
                            states.totalPerPersonState.value = calculateTotalPerson(states.billState.value.toDouble(), states.counterState.value, states.percentageState.value)
                        })
                        Text(text = "${states.counterState.value}",
                            modifier = modifier
                                .align(
                                    alignment = Alignment.CenterVertically
                                )
                                .padding(start = 12.dp, end = 9.dp))
                        RoundIconBt(imageVector = Icons.Default.Add, onClick = {
                            states.counterState.value += 1
                            states.totalPerPersonState.value = calculateTotalPerson(states.billState.value.toDouble(), states.counterState.value, states.percentageState.value)
                        })
                    }
                }
                // TIP ROW
                Row(modifier = modifier.padding(3.dp)) {
                    Text(text = "Tip", modifier = modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = modifier.width(200.dp))
                    Text(text = "$${states.tipAmtState.value}", modifier = modifier.align(alignment = Alignment.CenterVertically))
                }
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    val percentage = (states.sliderState.value * 100).roundToInt()
                    states.percentageState.value = percentage
                    Text(text = "${percentage}%")
                    Spacer(modifier = modifier.height(10.dp))
                    Slider(value = states.sliderState.value, onValueChange = {
                        states.sliderState.value = it
                        states.tipAmtState.value = calculateTip(states.billState.value.toDouble(), percentage)
                        states.totalPerPersonState.value = calculateTotalPerson(states.billState.value.toDouble(), states.counterState.value, percentage)
                    }, modifier = modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 5)
                }
            } else {
                states.totalPerPersonState.value = 0.0
                Box{}
            }
        }
    }
}

    //    @Preview(showSystemUi = true, showBackground = true, name = "Preview")
    @Composable
    fun Preview() {
        AppEntryPoint {
            Text(text = "Hey!")
        }
    }
}