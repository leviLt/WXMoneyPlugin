package com.wechatplugin.scropio.wxmoneyplugin.extensions

import android.app.Activity
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
fun Activity.toast(text: String, context: Context = applicationContext, time: Int = Toast.LENGTH_SHORT) {
    if (TextUtils.isEmpty(text)) {
        return
    }
    Toast.makeText(context, text, time).show()
}