package com.makefreemoney.earncash.moneymaker.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import butterknife.OnClick
import com.makefreemoney.earncash.moneymaker.AppTools
import com.makefreemoney.earncash.moneymaker.R
import com.makefreemoney.earncash.moneymaker.core.analytics.Analytics
import com.makefreemoney.earncash.moneymaker.core.managers.PreferencesManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseActivity(), Runnable {

    private var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindCoinView()
        bind()
        handler.post(this)

        toolbarText.text = "Money Maker"

        initBanner()

        if (intent.getBooleanExtra("notification", false)) {
            coinsManager.addCoins(0.04f)
            updateCoins()
            Analytics.report(Analytics.NOTIFICATION)
            dialogsManager.showAlertDialog(supportFragmentManager,
                    "You got $0.04!", {
                admobInterstitial?.show {  }
            })
        }

        checkBonusCoins()
    }

    @OnClick(R.id.addCoinsText)
    fun back() {
        startActivity(Intent(this, OffersActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    @OnClick(R.id.claim)
    fun claim(view: View) {
        when (view.id) {
            R.id.claim -> {
                if (preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0) == 30) {
                    if (preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0) == 0) {
                        coinsManager.addCoins(0.05f)
                        updateCoins()
                        preferencesManager.put(PreferencesManager.CLAIM_MINUTES, 0)
                        preferencesManager.put(PreferencesManager.CLAIM_SECONDS, 0)
                        dialogsManager.showAlertDialog(supportFragmentManager,
                                "Congratulations! You've been claimed $0.05!", {
                            startService()
                            admobInterstitial?.show {  }
                        })
                    } else {
                        dialogsManager.showAlertDialog(supportFragmentManager,
                                "Not available now!", {
                            admobInterstitial?.show {  }
                        })
                    }
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager,
                            "Not available now!", {
                        admobInterstitial?.show {  }
                    })
                }
            }
        }
    }

    @OnClick(R.id.offers, R.id.logOut, R.id.share, R.id.rateUs, R.id.ticketcsGame, R.id.redeem, R.id.paymentHistory)
    fun controlMain(view: View) {
        when (view.id) {
            R.id.offers -> {
                startActivity(Intent(this, OffersActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
            R.id.redeem -> {
                dialogsManager.showRedeemDialog(supportFragmentManager)
            }
            R.id.share -> {
                var mess = "I'am using this app to get free money: \"https://play.google.com/store/apps/details?id=" +
                        packageName + "\"" + " Here is my invite code: " +
                        preferencesManager.get(PreferencesManager.INVITE_CODE, "") +
                        " Install an app and enter this code to get $2!"
                try {
                    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, mess), "Share"))
                } catch (ex: Exception) {}
            }
            R.id.rateUs -> {
                dialogsManager.showAlertDialog(supportFragmentManager, "Ple".plus("ase, ").plus("rat").plus("e us")
                        .plus(" 5").plus(" sta").plus("rs!"), {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)))
                    } catch (ex: Exception) {}
                })
            }
            R.id.logOut -> {
                dialogsManager.showAdvAlertDialog(supportFragmentManager, "Are you sure?", {
                    preferencesManager.deleteAll()
                    coinsManager.deleteall()
                    database.historyDao().deleteAll()
                    startActivity(Intent(this, StartActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }, {})
            }
            R.id.ticketcsGame -> {
                if (AppTools.isNetworkAvaliable(this)) {
                    startActivity(Intent(this, GameActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No internet connection!", {
                        admobInterstitial?.show { }
                    })
                }
            }
            R.id.paymentHistory -> {
                dialogsManager.showHistoryDialog(supportFragmentManager)
            }
        }
    }

    override fun onBackPressed() {
        dialogsManager.showAdvAlertDialog(supportFragmentManager, "Do you really want to exit?", {
            finish()
        }, {
            admobInterstitial?.show {  }
        })
    }

    private fun checkBonusCoins() {
        if (preferencesManager.get(PreferencesManager.LAST_CHECKED, 0L) <= System.currentTimeMillis()) {
            preferencesManager.put(PreferencesManager.LAST_CHECKED, (System.currentTimeMillis() + (60 * 60 * 1000)))
            retrofitManager.invitecoins(preferencesManager.get(PreferencesManager.USERNAME, ""),
                    preferencesManager.get(PreferencesManager.PASSWORD, ""), { coins ->
                if (coins > 0) {
                    coinsManager.addCoins(coins.toFloat())
                    updateCoins()
                    dialogsManager.showAlertDialog(supportFragmentManager,
                            "Someone entered your invite code! You got $$coins!", {})
                }
            }, {})
        }
    }

    override fun run() {
        arcProgress.progress = preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0)
        progressText.text = "${preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0)}:${preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0)}"
        if (preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0) == 30) {
            if (preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0) == 0) {
                progressText.text = "Ready"
            }
        }
        handler.postDelayed(this, 1000)
    }
}
