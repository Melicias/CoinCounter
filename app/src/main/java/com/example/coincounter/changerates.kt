package com.example.coincounter

import com.beust.klaxon.*
import java.io.Serializable

private val klaxon = Klaxon()

data class changerates (
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String,
    val rates: Map<String, Double>
) : Serializable {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<changerates>(json)
    }

    public fun getRate(currency: String): Double? {
        return rates[currency]
    }

    fun rateKeysToList(): List<String> {
        if(rates==null){
            return listOf("test")
        }
        return rates.keys.toList()
    }

    fun changeRate(baseCurrency: String, toCurrency: String, value: Double): Double {
        if (baseCurrency == base)
            return rates[toCurrency]!! * value
        return (value / rates[baseCurrency]!!) * rates[toCurrency]!!
    }
}