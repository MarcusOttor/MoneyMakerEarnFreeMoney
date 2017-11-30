package com.makefreemoney.earncash.moneymaker.core.advertisements

import android.content.Context
import android.widget.TextView
import com.makefreemoney.earncash.moneymaker.core.analytics.Analytics
import com.makefreemoney.earncash.moneymaker.core.managers.CoinsManager
import com.makefreemoney.earncash.moneymaker.screens.BaseActivity
import mo.offers.lib.core.managers.OffersManager

class HouseOffers(private var context: Context,
                  private var currency: String,
                  private var coinManager: CoinsManager) {

    private var coinsText: TextView? = null

    private var manager: OffersManager = OffersManager(context, {}, context.packageName.toString(), currency)

    fun init() {
        manager.attachRewardListener { rw ->
            coinManager.addCoins(rw.toFloat() * 0.01f)
            coinsText?.text = BaseActivity.format(coinManager.getCoins())
            Analytics.report(Analytics.OFFER, Analytics.MOOFFERS, Analytics.REWARD)
        }
    }

    fun show(coinsText: TextView) {
        this.coinsText = coinsText
        manager.show()
        Analytics.report(Analytics.OFFER, Analytics.MOOFFERS, Analytics.OPEN)
    }
 }
