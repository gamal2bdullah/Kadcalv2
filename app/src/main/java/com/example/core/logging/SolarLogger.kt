package com.example.core.logging

import android.util.Log

interface Logger {
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String, throwable: Throwable? = null)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

object SolarLogger : Logger {
    private const val GLOBAL_PREFIX = "KADcal_"

    override fun d(tag: String, message: String) {
        Log.d("$GLOBAL_PREFIX$tag", message)
    }

    override fun i(tag: String, message: String) {
        Log.i("$GLOBAL_PREFIX$tag", message)
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        Log.w("$GLOBAL_PREFIX$tag", message, throwable)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e("$GLOBAL_PREFIX$tag", message, throwable)
    }
}
