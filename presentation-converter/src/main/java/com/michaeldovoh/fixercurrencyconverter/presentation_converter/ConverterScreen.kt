package com.michaeldovoh.fixercurrencyconverter.presentation_converter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.michaeldovoh.fixercurrencyconverter.presentation_common.navigation.HistoryInput
import com.michaeldovoh.fixercurrencyconverter.presentation_common.navigation.NavRoutes
import com.michaeldovoh.fixercurrencyconverter.presentation_common.state.CommonScreen
import com.michaeldovoh.fixercurrencyconverter.presentation_common.state.UiState

@Composable
fun ConverterScreen(
    viewModel: CurrencyConverterViewModel,
    modifier: Modifier,
    navController:NavController
) {
    var targetCurrencySymbol by rememberSaveable { mutableStateOf("GHS") }
    var baseCurrencySymbol by rememberSaveable { mutableStateOf("USD") }
    var baseAmount by rememberSaveable { mutableStateOf("1") }
    var swapCurrency by rememberSaveable { mutableStateOf(false) }

    var rate by rememberSaveable {
        mutableStateOf("")
    }

    viewModel.convertedRateFlow.collectAsState().value.let {
        when (it) {
            is UiState.Initial -> {
                rate = ""
            }
            is UiState.Loading -> {
                rate =""
            }
            is UiState.Success -> {
                val toDoubleAmount = baseAmount.toDoubleOrNull()
                val baseRate = it.data.firstOrNull()?.rate
                rate = if(baseAmount.isNotEmpty() && toDoubleAmount!=null && baseRate!=null)
                    String.format("%.3f",
                        baseRate.times(baseAmount.toDouble())
                    )
                else "error converting..."
            }
            is UiState.Error->{
                rate = it.errorMessage
            }
        }
    }
    Column(modifier = Modifier.padding(24.dp, 0.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        viewModel.currencyListFlow.collectAsState().value.let { state ->
            CommonScreen(state = state) {
                    currencyListModel->
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    CurrencyPicker(
                        modifier=Modifier.padding(2.dp),
                        readOnly = false,
                        enabled = true,
                        defaultSymbol = baseCurrencySymbol,
                        currencyListModel.items,
                        onSymbolSelected = {
                            baseCurrencySymbol = it
                            viewModel.onCurrencyChanged(
                                it,
                                baseAmount,
                                targetCurrencySymbol
                            )

                        }
                    )

                    IconButton(onClick = {
                        val temp= baseCurrencySymbol
                        baseCurrencySymbol = targetCurrencySymbol
                        targetCurrencySymbol = temp
                        swapCurrency = !swapCurrency



                        if(baseAmount.isNotEmpty() && targetCurrencySymbol.isNotEmpty() && baseCurrencySymbol.isNotEmpty()){


                            viewModel.onCurrencyChanged(baseCurrencySymbol=baseCurrencySymbol,
                                targetCurrencySymbol=targetCurrencySymbol, baseAmount = baseAmount)
                        }


                    }) {
                        Icon(
                            imageVector = Icons.Filled.CompareArrows,
                            contentDescription = stringResource(R.string.compare_arrow_description),
                            modifier = Modifier.padding(8.dp),
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }

                    CurrencyPicker(
                        modifier=Modifier.padding(2.dp),
                        readOnly = false,
                        enabled = true,
                        defaultSymbol = targetCurrencySymbol,
                        currencyListModel.items,
                        onSymbolSelected = {
                            targetCurrencySymbol = it
                            viewModel.onCurrencyChanged(
                                baseCurrencySymbol,
                                baseAmount,
                                it
                            )


                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CurrencyRateTextField(
                baseCurrencySymbol,
                modifier.weight(1.0f),
                readOnly = false,
                value = baseAmount,
                enabled = true,
                onAmountChanged = { newBaseAmount ->
                    baseAmount = newBaseAmount

                    if (newBaseAmount.isNotEmpty() && newBaseAmount.toDoubleOrNull()!=null) {
                        viewModel.onCurrencyChanged(baseCurrencySymbol = baseCurrencySymbol,
                            targetCurrencySymbol= targetCurrencySymbol, baseAmount = newBaseAmount)


                    }
                }
            )
            Spacer(modifier = Modifier
                .height(16.dp)
                .weight(0.1f))
            //base currency picker
            CurrencyRateTextField(
                targetCurrencySymbol,
                modifier.weight(1.0f),
                readOnly = false,
                value = rate,
                /*value = data.convertedAmount,
*/
                enabled = false,

                onAmountChanged = {

                }
            )




        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            elevation = null,
            onClick = {

                navController.navigate(NavRoutes.History.routeForHistory(HistoryInput(baseCurrencySymbol,targetCurrencySymbol)))



            },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = Color.White
            )
        ) {
            //convert text
            Text(
                text = stringResource(R.string.detail_text),
                fontWeight = FontWeight.Bold, fontSize = 16.sp
            )
        }

    }
    }