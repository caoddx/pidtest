package com.caodd.pid

import android.app.Application
import android.util.Log
import com.orhanobut.logger.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initLog()
    }

    private fun initLog() {
        val tag ="CAODD"

        val fs = CsvFormatStrategy.newBuilder()
                .tag(tag)
                .dateFormat(SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA))
                .build()

        Logger.addLogAdapter(object : DiskLogAdapter(fs) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return priority > Log.DEBUG
            }
        })

        if (BuildConfig.DEBUG) {

            val fs2 = PrettyFormatStrategy.newBuilder()
                    .tag(tag)
                    .methodOffset(5)
                    .methodCount(2)
                    .logStrategy(object : LogcatLogStrategy() {

                        var turn = true
                        override fun log(priority: Int, tag: String?, message: String) {
                            val p = if (turn) "+" else "-"
                            turn = turn.not()
                            super.log(priority, "$p$tag", message)
                        }
                    })
                    .build()

            Logger.addLogAdapter(AndroidLogAdapter(fs2))

            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    Logger.log(priority, tag, message, t)
                }
            })

        } else {

            Timber.plant(object : Timber.Tree() {
                override fun isLoggable(tag: String?, priority: Int): Boolean {
                    return priority > Log.DEBUG
                }

                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    Logger.log(priority, tag, message, t)
                }
            })
        }

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            try {
                Timber.e(e, "uncaught exception in thread ${t.name}")
            } catch (ex: Exception) {
                Log.wtf(tag, "UncaughtException Timber log failure", ex)
            }

            defaultHandler.uncaughtException(t, e)
        }

        Timber.i("App start")
    }
}