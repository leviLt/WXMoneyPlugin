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
            var bool = false
            if (context is RedPacketService) {
                val accessibilityNodeInfo = context.rootInActiveWindow
                if (accessibilityNodeInfo != null) {
                    bool = if (accessibilityNodeInfo.childCount > 0) {
                        accessibilityNodeInfo.getChild(0).childCount > 1
                    } else {
                        false
                    }
                }
            }
            return bool
        }
    }
}