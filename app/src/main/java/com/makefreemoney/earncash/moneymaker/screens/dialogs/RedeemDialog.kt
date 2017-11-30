package com.makefreemoney.earncash.moneymaker.screens.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import butterknife.ButterKnife
import butterknife.OnClick
import com.makefreemoney.earncash.moneymaker.AppTools
import com.makefreemoney.earncash.moneymaker.R
import com.makefreemoney.earncash.moneymaker.core.MyApplication
import com.makefreemoney.earncash.moneymaker.core.analytics.Analytics
import com.makefreemoney.earncash.moneymaker.core.managers.CoinsManager
import com.makefreemoney.earncash.moneymaker.core.managers.DialogsManager
import com.makefreemoney.earncash.moneymaker.core.managers.PreferencesManager
import com.makefreemoney.earncash.moneymaker.db.History
import com.makefreemoney.earncash.moneymaker.db.HistoryDatabase
import com.makefreemoney.earncash.moneymaker.inject.AppModule
import com.makefreemoney.earncash.moneymaker.inject.DaggerAppComponent
import com.makefreemoney.earncash.moneymaker.screens.BaseActivity
import com.makefreemoney.earncash.moneymaker.screens.MainActivity
import kotlinx.android.synthetic.main.dialog_redeem.view.*
import java.text.SimpleDateFormat
import javax.inject.Inject
import kotlin.concurrent.thread

class RedeemDialog : DialogFragment() {

    @Inject lateinit var preferencesManager: PreferencesManager
    @Inject lateinit var coinsManager: CoinsManager
    @Inject lateinit var dialogsManager: DialogsManager
    @Inject lateinit var database: HistoryDatabase

    private var currentTab: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        DaggerAppComponent.builder()
                .appModule(AppModule(context))
                .mainModule((activity.application as MyApplication).mainModule)
                .build().inject(this)

        var view = inflater?.inflate(R.layout.dialog_redeem, container, false)

        view?.redeemMoneyText?.text = "Withdraw $${BaseActivity.format(coinsManager.getCoins())}"

        repeat(2) {
            view?.switcher?.addTab(view.switcher.newTab())
        }
        view?.switcher?.getTabAt(0)?.text = "PayPal"
        view?.switcher?.getTabAt(1)?.text = "Visa"
        view?.switcher?.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                view.emailText?.hint = when (tab?.position) {
                    0 -> "Paypal email address"
                    1 -> "Email address"
                    else -> { "Email address" }
                }
            }
        })

        ButterKnife.bind(this, view !!)

        return view
    }

    @OnClick(R.id.redeemBtn)
    fun redeem() {
        if (AppTools.isNetworkAvaliable(activity)) {
            if (coinsManager.getCoins() >= 100f) {
                if (AppTools.isEmailAdressCorrect(view?.rootView?.emailText?.text.toString())) {
                    var dismisser = dialogsManager.showProgressDialog(activity.supportFragmentManager)
                    thread {
                        Thread.sleep(3000)
                        Analytics.report(Analytics.WITHDRAW, Analytics.AMOUNT, coinsManager.getCoins().toString())
                        activity.runOnUiThread {
                            dismisser.dismiss()
                            database.historyDao().insert(History(
                                    SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()), coinsManager.getCoins()))
                            coinsManager.subtractCoins(coinsManager.getCoins())
                            view?.rootView?.redeemMoneyText?.text = "Withdraw $${BaseActivity.format(coinsManager.getCoins())}"
                            (activity as MainActivity).updateCoins()
                            dialogsManager.showAlertDialog(activity.supportFragmentManager,
                                    "You will receive your money in 3 - 7 days!", {
                                dismiss()
                            })
                        }
                    }
                } else {
                    dialogsManager.showAlertDialog(activity.supportFragmentManager,
                            "Email is not valid!", {
                        (activity as MainActivity).admobInterstitial?.show {  }
                    })
                }
            } else {
                dialogsManager.showAlertDialog(activity.supportFragmentManager,
                        "Not enough money! You need at least $100", {
                    (activity as MainActivity).admobInterstitial?.show {  }
                })
            }
        } else {
            dialogsManager.showAlertDialog(activity.supportFragmentManager,
                    "No internet connection!", {
                (activity as MainActivity).admobInterstitial?.show {  }
            })
        }

    }
}
