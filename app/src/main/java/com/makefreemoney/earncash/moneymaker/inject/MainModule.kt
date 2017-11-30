package com.makefreemoney.earncash.moneymaker.inject

import android.arch.persistence.room.Room
import android.content.Context
import com.makefreemoney.earncash.moneymaker.R
import com.makefreemoney.earncash.moneymaker.db.HistoryDatabase
import com.yandex.metrica.YandexMetricaConfig
import dagger.Module
import dagger.Provides
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Singleton

@Singleton
@Module
class MainModule(var context: Context) {

    @Provides
    fun provideAppMetrica() : YandexMetricaConfig.Builder {
        return YandexMetricaConfig
                .newConfigBuilder(context.resources.getString(R.string.metrica))
    }

    @Provides
    fun provideCalligraphy() : CalligraphyConfig {
        return CalligraphyConfig
                .Builder()
                .setDefaultFontPath("fonts/ARLRDBD.TTF")
                .setFontAttrId(R.attr.fontPath)
                .build()
    }

    @Provides
    fun provideContext() : Context {
        return context
    }

    @Provides
    fun provideDb() : HistoryDatabase {
        return Room.databaseBuilder(context, HistoryDatabase::class.java, "history_database")
                .allowMainThreadQueries().build()
    }
}
