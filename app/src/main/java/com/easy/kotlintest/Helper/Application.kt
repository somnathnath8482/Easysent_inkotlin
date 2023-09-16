package com.easy.kotlintest.Helper

import android.app.Application
import com.easy.kotlintest.Helper.PrefFile.PrefUtill

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        PrefUtill(this)
    }
}
