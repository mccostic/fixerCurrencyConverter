package com.michaeldovoh.fixercurrencyconverter.presentation_converter


data class CurrencyListModel(
    val items: List<CurrencyListItemModel> = listOf(),
)

data class CurrencyListItemModel(
    val iso: String,
    val name: String
)