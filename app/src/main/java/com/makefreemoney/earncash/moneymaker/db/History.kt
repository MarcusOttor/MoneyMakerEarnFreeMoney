package com.makefreemoney.earncash.moneymaker.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class History {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "date")
    var date: String = ""
    @ColumnInfo(name = "amount")
    var amount: Float = 0f

    constructor(date: String, amount: Float) {
        this.date = date
        this.amount = amount
    }
}
