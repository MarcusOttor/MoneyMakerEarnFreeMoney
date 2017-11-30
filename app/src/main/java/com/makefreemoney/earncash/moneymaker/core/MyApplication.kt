package com.makefreemoney.earncash.moneymaker.core

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.makefreemoney.earncash.moneymaker.core.advertisements.AdmobInterstitial
import com.makefreemoney.earncash.moneymaker.core.advertisements.AdvertisementManager
import com.makefreemoney.earncash.moneymaker.core.managers.CoinsManager
import com.makefreemoney.earncash.moneymaker.core.managers.PreferencesManager
import com.makefreemoney.earncash.moneymaker.core.managers.RetrofitManager
import com.makefreemoney.earncash.moneymaker.core.receiver.Receiver
import com.makefreemoney.earncash.moneymaker.inject.AppModule
import com.makefreemoney.earncash.moneymaker.inject.DaggerAppComponent
import com.makefreemoney.earncash.moneymaker.inject.MainModule
import com.google.android.gms.ads.MobileAds
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Inject

class MyApplication : MultiDexApplication() {

    @Inject lateinit var calligraphy: CalligraphyConfig
    @Inject lateinit var coinsManager: CoinsManager
    @Inject lateinit var preferencesManager: PreferencesManager
    @Inject lateinit var metrica: YandexMetricaConfig.Builder

    @Inject lateinit var retrofit: RetrofitManager

    lateinit var mainModule: MainModule

    var advertisement: AdvertisementManager? = null
    var interstitialAdmob: AdmobInterstitial? = null

    override fun onCreate() {
        super.onCreate()

        mainModule = MainModule(this)

        DaggerAppComponent.builder()
                .appModule(AppModule(applicationContext))
                .mainModule(mainModule)
                .build().inject(this)

        CalligraphyConfig.initDefault(calligraphy)

        initializeAdmobAds()

        advertisement = AdvertisementManager(preferencesManager, coinsManager, applicationContext)
        interstitialAdmob = AdmobInterstitial(preferencesManager, applicationContext)

        if (!preferencesManager.get(PreferencesManager.FIRST_LAUNCH, true)) {
            metrica.handleFirstActivationAsUpdate(true)
            preferencesManager.put(PreferencesManager.FIRST_LAUNCH, false)
        }
        var extended = metrica.build()
        YandexMetrica.activate(applicationContext, extended)
        YandexMetrica.enableActivityAutoTracking(this)

        scheduleEveryTime()
    }

    fun scheduleEveryTime() {
        var intent = Intent(this, Receiver::class.java)
        var pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + 2 * 60 * 60 * 1000), pi)
        } else {
            am.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + 2 * 60 * 60 * 1000), pi)
        }
    }

    private fun initializeAdmobAds() {
        MobileAds.initialize(applicationContext, "ca-app-pub-7065666432812754~1461511941")
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
