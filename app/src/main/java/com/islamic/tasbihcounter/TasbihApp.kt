package com.islamic.tasbihcounter

import android.app.Application
import com.islamic.tasbihcounter.util.createReminderChannel

class TasbihApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createReminderChannel(this)
    }
}
