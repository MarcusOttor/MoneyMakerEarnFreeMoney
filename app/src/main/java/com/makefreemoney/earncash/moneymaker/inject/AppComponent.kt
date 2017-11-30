package com.makefreemoney.earncash.moneymaker.inject

import com.makefreemoney.earncash.moneymaker.core.MyApplication
import com.makefreemoney.earncash.moneymaker.core.services.ClaimService
import com.makefreemoney.earncash.moneymaker.screens.BaseActivity
import com.makefreemoney.earncash.moneymaker.screens.dialogs.*
import dagger.Component

@Component(modules = arrayOf(AppModule::class, MainModule::class))
interface AppComponent {

    fun inject(screen: BaseActivity)
    fun inject(app: MyApplication)
    fun inject(dialog: LoginDialog)
    fun inject(dialog: SignupDialog)
    fun inject(dialog: PromocodeDialog)
    fun inject(dialog: RedeemDialog)
    fun inject(service: ClaimService)
    fun inject(dialog: HistoryDialog)
}
