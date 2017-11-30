package com.makefreemoney.earncash.moneymaker.core.managers

import android.content.Context
import android.content.SharedPreferences

class CoinsManager(context: Context) {

    companion object {
        var COINS_CURRENT = "COINS_CURRENT"
    }

    private var prefs: SharedPreferences = context.getSharedPreferences(null, Context.MODE_PRIVATE)

    fun deleteall() {
        prefs.edit().clear().apply()
    }

    fun subtractCoins(coins: Float) {
        prefs.edit().putFloat(COINS_CURRENT, getCoins() - coins).apply()
    }

    fun getCoins() : Float {
        return prefs.getFloat(COINS_CURRENT, 0f)
    }

    fun addCoins(coins: Float) {
        prefs.edit().putFloat(COINS_CURRENT, getCoins() + coins).apply()
    }

    fun setCoins(coins: Float) {
        prefs.edit().putFloat(COINS_CURRENT, coins).apply()
    }
}
