package com.makefreemoney.earncash.moneymaker.core.receiver

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.NotificationCompat
import com.makefreemoney.earncash.moneymaker.R
import com.makefreemoney.earncash.moneymaker.core.managers.PreferencesManager
import com.makefreemoney.earncash.moneymaker.screens.MainActivity

class Receiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        var prefs = PreferencesManager(context!!)
        if (prefs.get(PreferencesManager.FIRST_LAUNCH, false)) {

            var nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            var ni = Intent(context, MainActivity::class.java)
            ni.putExtra("notification", true)
            var content = PendingIntent.getActivity(context, 0, ni, 0)

            nm.notify(0, NotificationCompat.Builder(context).setContentTitle(context.getString(R.string.app_name))
                    .setContentText("Yor reward is ready! Claim your money!").setSmallIcon(R.drawable.logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo))
                    .setContentIntent(content).setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND).build())

            scheduleEveryTime(context)
        }
    }

    private fun scheduleEveryTime(ctx: Context) {
        var intent = Intent(ctx, Receiver::class.java)
        var pi = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + 2 * 60 * 60 * 1000), pi)
        } else {
            am.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + 2 * 60 * 60 * 1000), pi)
        }
    }
}
