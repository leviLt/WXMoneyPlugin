package com.wechatplugin.scropio.wxmoneyplugin.utils

import android.content.Context
import android.content.pm.PackageInfo
import com.wechatplugin.scropio.wxmoneyplugin.service.RedPacketService

/**
 * @author lt
 * @package com.wechatplugin.scropio.wxmoneyplugin.utils
 * @date 2019/1/16 4:02 PM
 * @describe TODO
 * @project
 */
private val WX_PackageName = "com.tencent.mm"

class WXUtils {
    companion object {
        /**
         * 微信版本号
         */
        fun getWXVersion(context: Context): Int {
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = context.applicationContext.packageManager.getPackageInfo(WX_PackageName, 0)
            } catch (e: Exception) {
                e.printStackTrace()
                packageInfo = null
            }
            if (packageInfo == null) {
                return 0
            }
            return packageInfo.versionCode
        }

        /**
         * 是否在微信列表
         */
        fun isWXChatList(context: Context): Boolean {
            var isNotList = true
            if (context is RedPacketService) {
                context.rootInActiveWindow?.apply {
                    this.findAccessibilityNodeInfosByText("微信")?.apply {
                        if (this.isEmpty()) {
                            isNotList = false
                        }
                    }
                    this.findAccessibilityNodeInfosByText("发现")?.apply {
                        if (this.isEmpty()) {
                            isNotList = false
                        }
                    }
                    this.findAccessibilityNodeInfosByText("通讯录")?.apply {
                        if (this.isEmpty()) {
                            isNotList = false
                        }
                    }
                    this.findAccessibilityNodeInfosByText("我")?.apply {
                        if (this.isEmpty()) {
                            isNotList = false
                        }
                    }
                }
            }
            return isNotList
        }
    }
}