package com.wechatplugin.scropio.wxmoneyplugin

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport

/**
 * @author lt
 * @package com.wechatplugin.scropio.wxmoneyplugin
 * @date 2019/1/16 10:36 AM
 * @describe TODO
 * @project
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化bugly
        CrashReport.initCrashReport(applicationContext, ConstValues.BuglyAppId, false)
    }
}