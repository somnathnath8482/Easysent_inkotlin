package com.easy.kotlintest.Helper

import android.app.Application
import android.util.Log
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.socket.LiveMessage

class Application : Application() {
   var  application:Application = this;
    override fun onCreate() {
        super.onCreate()
        application = this
        PrefUtill(this)
        if (PrefUtill.getIsLogged()){
            Log.e("TAG", "onCreate: Loggedin" )
            LiveMessage(this)
            LiveMessage.listen(PrefUtill.getUser()?.user?.id)
        }

    }

    fun getapplication():Application{
        return application?:this;
    }

}
