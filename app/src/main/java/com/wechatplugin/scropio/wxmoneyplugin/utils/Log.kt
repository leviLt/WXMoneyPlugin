package com.wechatplugin.scropio.wxmoneyplugin.utils

import android.util.Log
import com.wechatplugin.scropio.wxmoneyplugin.BuildConfig
import java.lang.StringBuilder


/**
 * @author lt
 * @package com.wechatplugin.scropio.wxmoneyplugin.utils
 * @date 2019/1/16 1:23 PM
 * @describe TODO
 * @project
 */
class Log {
    companion object {
        fun log(vararg log: String) {
            if (BuildConfig.DEBUG) {
                val stackTrace = Thread.currentThread().stackTrace
                val index = 3
                val className = stackTrace[index].className
                val methodName = stackTrace[index].methodName
                val lineNumber = stackTrace[index].lineNumber

                val stringBuilder = StringBuilder()
                stringBuilder.append("[($className : $lineNumber )# $methodName]")
                if (log.isNotEmpty()) {
                    log.forEach {
                        stringBuilder.append(it)
                    }
                }
                val threadName = Thread.currentThread().name
                Log.e("Thread = $threadName", stringBuilder.toString())
            }
        }

    }
}