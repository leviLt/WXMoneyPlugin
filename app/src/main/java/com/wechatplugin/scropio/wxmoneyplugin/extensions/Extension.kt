package com.wechatplugin.scropio.wxmoneyplugin.extensions

import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils
import android.widget.Toast

/**
 * @author lt
 * @package com.wechatplugin.scropio.wxmoneyplugin.extensions
 * @date 2019/1/14 5:27 PM
 * @describe TODO
 * @project
 */
fun Context.toast(
    text: String,
    context: Context = applicationContext,
    time: Int = Toast.LENGTH_SHORT
) {
    if (TextUtils.isEmpty(text)) {
        return
    }
    Toast.makeText(context, text, time).show()
}


/**
 * 关闭指定App
 * @param packageName 包名
 */
fun Context.killAppByPackageName(packageName: String) {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.killBackgroundProcesses(packageName)
}

/**
 * 打开指定包名的App
 * @param packageName 包名
 */
fun Context.openAppByPackage(packageName: String) {
    packageManager?.getLaunchIntentForPackage(packageName)?.also {
        startActivity(it)
    }

}