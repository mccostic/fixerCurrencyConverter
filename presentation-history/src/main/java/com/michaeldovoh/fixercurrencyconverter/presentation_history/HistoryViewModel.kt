package com.michaeldovoh.fixercurrencyconverter.presentation_history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaeldovoh.fixercurrencyconverter.domain.usecase.GetHistoryRateUseCase
import com.michaeldovoh.fixercurrencyconverter.presentation_common.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val useCase: GetHistoryRateUseCase,
                        private val historyRateListConverter: HistoryRateListConverter,)
    :ViewModel(){

    private val _historyRatesFlow =
        MutableStateFlow<UiState<HistoryRateListModel>>(UiState.Initial)
    val historyRatesFlow: StateFlow<UiState<HistoryRateListModel>> = _historyRatesFlow

    fun getDate(minusDays:Long =0): String {
        val zoneId = ZoneId.systemDefault()
        return LocalDate.now(zoneId).minusDays(minusDays).toString()
    }

    fun getTargetCurrencies(target:String):String{
        val popularCurrencies = mutableListOf("EUR","JPY","GBP","AUD","CAD","CHF","HKD","SGD","USD","CNY","SEK")
        val topMostPopularCurrencies = popularCurrencies.filter { s->s !=target }.toMutableList()
        topMostPopularCurrencies.add(target)
        return topMostPopularCurrencies.joinToString()
    }
    fun getHistoryRates(base:String,target:String, endDate:String,startDate:String) {
        viewModelScope.launch {
            useCase.execute(GetHistoryRateUseCase.Request(base = base, target = target, startDate = startDate, endDate = endDate))
                .map {

                    historyRateListConverter.convert(it)
                }
                .collect {
                    if(it is UiState.Error){
                        val its = it.errorMessage
                        Log.d("HISORYRATES", its)
                    }
                    _historyRatesFlow.value = it
                }
        }
    }
}