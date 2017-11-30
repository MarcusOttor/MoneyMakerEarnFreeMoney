package com.makefreemoney.earncash.moneymaker.core.advertisements

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.makefreemoney.earncash.moneymaker.core.analytics.Analytics
import com.fyber.Fyber
import com.fyber.ads.AdFormat
import com.fyber.currency.VirtualCurrencyErrorResponse
import com.fyber.currency.VirtualCurrencyResponse
import com.fyber.requesters.*
import com.makefreemoney.earncash.moneymaker.core.managers.CoinsManager
import com.makefreemoney.earncash.moneymaker.core.managers.PreferencesManager
import com.makefreemoney.earncash.moneymaker.screens.BaseActivity
import com.makefreemoney.earncash.moneymaker.screens.OffersActivity

class FyberManager(
        private var coinsManager: CoinsManager,
        private var preferencesManager: PreferencesManager) {

    private lateinit var activity: AppCompatActivity
    private lateinit var coinView: TextView
    private var isAvailable = false
    private var offerWallIntent: Intent? = null

    fun init(activity: AppCompatActivity) {
        this.activity = activity
        Fyber.with(appId, activity)
                .withSecurityToken(SecurityToken)
                .start()
        OfferWallRequester.create(object : RequestCallback {

            override fun onAdNotAvailable(p0: AdFormat?) {
                isAvailable = false
            }

            override fun onRequestError(p0: RequestError?) {
                isAvailable = false
            }

            override fun onAdAvailable(offerWallIntent: Intent?) {
                this@FyberManager.offerWallIntent = offerWallIntent
                isAvailable = true
            }
        }).request(activity)

        VirtualCurrencyRequester.create(callback).request(activity)
    }

    private var callback = object : VirtualCurrencyCallback {
        override fun onSuccess(p0: VirtualCurrencyResponse?) {
            if (p0?.deltaOfCoins?.toInt() ?: 0 > 0) {
                coinsManager.addCoins((p0?.deltaOfCoins?.toFloat()!! * 0.01f))
                try {
                    coinView.text = BaseActivity.format(coinsManager.getCoins())
                } catch (ex: Exception) {
                    try {
                        (activity as OffersActivity).coinsView.text = BaseActivity.format(coinsManager.getCoins())
                    } catch (ex: Exception) {}
                }
                Analytics.report(Analytics.OFFER, Analytics.FYBER, Analytics.REWARD)
            }
        }

        override fun onRequestError(p0: RequestError?) {}
        override fun onError(p0: VirtualCurrencyErrorResponse?) {}

    }

    fun onResume(activity: AppCompatActivity) {
        this.activity = activity
        VirtualCurrencyRequester.create(callback).request(activity)
    }

    fun show(activity: AppCompatActivity, coinsView: TextView): Boolean {
        this.activity = activity
        this.coinView = coinsView
        if (isAvailable) activity.startActivity(offerWallIntent)
        Analytics.report(Analytics.OFFER, Analytics.FYBER, Analytics.OPEN)
        return isAvailable

    }

    companion object {
        val appId = "110366"
        val SecurityToken = "d0f17cf92dd7c632b5e8a4cba0013ff1"
    }
}
