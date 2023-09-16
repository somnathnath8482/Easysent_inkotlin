package com.easy.kotlintest.Helper.PrefFile

import android.content.Context
import android.content.SharedPreferences
import com.easy.kotlintest.Helper.PrefFile.PrefKey.Companion.KEY_IS_LOGGED
import com.easy.kotlintest.Helper.PrefFile.PrefKey.Companion.KEY_USER
import com.easy.kotlintest.R
import com.easy.kotlintest.Response.Login.LoginResponse
import com.google.gson.Gson


class PrefUtill {


    constructor(context: Context) {
        preferences = context.getSharedPreferences(context.getString(R.string.app_name), 0)
            .also {
                preferences = it
                it.edit().also {
                    editor = it
                }
            }


    }

    companion object {
        private var preferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null

        fun clearSessionManager() {
            editor?.clear()?.apply()
        }

        fun setIsLogged(islogged: Boolean) {
            editor?.putBoolean(KEY_IS_LOGGED, islogged)

        }
 fun getIsLogged(): Boolean {
            return  preferences?.getBoolean(KEY_IS_LOGGED, false)?:false;

        }

        fun setUser(value: LoginResponse?) {
            val gson = Gson()
            val json = gson.toJson(value)
            editor?.putString(KEY_USER, json.toString())?.apply()
        }

        fun getUser(): LoginResponse? {
            var res: LoginResponse? = null
            try {
                val gson = Gson()
                val json: String = preferences?.getString(KEY_USER, "").toString()
                res = gson.fromJson(json, LoginResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return res
        }

    }
}