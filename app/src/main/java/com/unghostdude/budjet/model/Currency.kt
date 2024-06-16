package com.unghostdude.budjet.model

import java.util.Currency

val supportedCurrencies = listOf(
    "USD", "EUR", "JPY", "GBP", "CHF", "CAD", "AUD", "NZD", "CNY", "HKD",
    "SGD", "TWD", "KRW", "INR", "BRL", "RUB", "ZAR", "MXN", "TRY", "SEK",
    "NOK", "DKK", "PLN", "SAR", "AED", "QAR", "KWD", "OMR", "BHD", "MAD",
    "NGN"
).map {
    Currency.getInstance(it)
}