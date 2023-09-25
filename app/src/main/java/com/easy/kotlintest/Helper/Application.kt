package com.easy.kotlintest.Helper

import android.app.Application
import com.easy.kotlintest.Helper.PrefFile.PrefUtill

class Application : Application() {
   var  application:Application = this;
    override fun onCreate() {
        super.onCreate()
        application = this
        PrefUtill(this)
    }

    fun getapplication():Application{
        return application?:this;
    }

}
