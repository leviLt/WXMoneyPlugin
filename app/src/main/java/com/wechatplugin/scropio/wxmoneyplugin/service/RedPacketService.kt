package com.wechatplugin.scropio.wxmoneyplugin.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Notification
import android.opengl.Visibility
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import com.wechatplugin.scropio.wxmoneyplugin.utils.Log
import com.wechatplugin.scropio.wxmoneyplugin.utils.WXUtils

/**
 * 微信的包名
 */
private val WX_Chat_Launch_UI = "com.tencent.mm.ui.LauncherUI"
private val WX_Red_Packet_UI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI"
private val WX_Red_Packet_UI_7 = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI"         //微信7.0.0
private val WX_Red_Packet_Detail_UI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI"
private val WECHAT_VIEWPAGER_LAYOUT = "com.tencent.mm.ui.mogic.WxViewPager"
private val WX_List_View_Name = "android.widget.ListView"
private val WX_Text_View_Name = "android.widget.TextView"
private val LINEARLAYOUT_NAME = "android.widget.LinearLayout"
private val WECHAT_DETAILS_CH = "红包详情"
private val WECHAT_BETTER_LUCK_CH = "手慢了"
private val WECHAT_EXPIRES_CH = "已超过24小时"
private val WECHAT_VIEW_SELF_CH = "查看红包"
private val WECHAT_VIEW_OTHERS_CH = "领取红包"
private val WX_FriendSend_RedPacket_ForList_Tip = "[微信红包]恭喜发财，大吉大利"
private val WX_Notification_Tip = "[微信红包]"
private val WX_Friend_Send_RedPacket_TIP = "恭喜发财，大吉大利"
private val WX_Already_Open = "已领取"
private val WECHAT_PACKEY_TIP = "微信红包"
private val WECHAT_DISCOVER_TIP = "发现"
private val WECHAT_COMMUNICATE_TIP = "通讯录"

/**
 * 微信6.7.3版本的版本号
 */
private val WX_673_VERCODE = 1360
/**
 * 微信7.0.0版本的版本号
 */
private val WX_7_VERCODE: Int = 1380
private val HANDLER_CLOSE_PACKEY = 0x01
private val HANDLER_POSTDELAY_OPEN = 0x02           //延时打开红包

class RedPacketService : AccessibilityService() {
    var flag: Boolean = false
    var openPacketFlag = false
    var currentActivityName = WX_Chat_Launch_UI
    var currentNoteInfo = ""
    var WX_versionCode: Int = 0
    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) {
            return
        }
        Log.log(event.className.toString())
        if (!flag) {
            flag = true
            setCurrentActivityName(event)
            when (event.eventType) {
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                    handleNotificationEvent(event)
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                    handleWindowEvent(event)
                }
            }
            flag = false
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        //获取版本号
        WX_versionCode = WXUtils.getWXVersion(this)
    }

    private fun setCurrentActivityName(event: AccessibilityEvent) {
        var activityName = event.className.toString()
        currentNoteInfo = event.className.toString()
        if (activityName.startsWith("com.tencent.mm", true)) {
            currentActivityName = activityName
            Log.log(activityName)
        }
    }


    private fun handleNotificationEvent(event: AccessibilityEvent) {
        val tip = event.text.toString()
        if (tip.contains(WX_Notification_Tip, true)) {
            val parcelable = event.parcelableData
            if (parcelable is Notification) {
                var notification = parcelable
                notification.contentIntent.send()
            }
        }
    }


    private fun handleWindowEvent(event: AccessibilityEvent) {
        when (currentActivityName) {
            //红包弹窗界面
            WX_Red_Packet_UI, WX_Red_Packet_UI_7 -> {

            }
            //聊天界面或聊天列表界面
            WX_Chat_Launch_UI -> {
                if (WXUtils.isWXChatList(this)) {
                    goChatSurface(event)
                } else {
                    getRedPacked()
                }
            }
            //红包详情UI
            WX_Red_Packet_Detail_UI -> {

            }
        }
    }

    /**
     * 发现红包进入聊天界面
     */
    private fun goChatSurface(event: AccessibilityEvent) {
        val info = rootInActiveWindow ?: return
        //检查是否有红包字样
        recycle(info)
    }

    private fun recycle(info: AccessibilityNodeInfo) {
        if (info.childCount == 0) {
            Log.log("child weight -------${info.className}")
            Log.log("text         -------${info.text}")
            Log.log("windowId     -------${info.windowId}")
        }
        for (i in 0 until info.childCount) {
            if (info.getChild(i) != null) {
                recycle(info.getChild(i))
            }
        }
    }

    /**
     * 点开红包
     */
    private fun getRedPacked() {
        val accessibilityNodeInfo = rootInActiveWindow ?: return
        val list = accessibilityNodeInfo.findAccessibilityNodeInfosByText(WX_Friend_Send_RedPacket_TIP)
        if (list == null || list.size <= 0) {
            return
        }
        list.forEach {
            if (it.parent == null) {
                return
            }
            val childCount = it.parent.childCount
            for (i in 0 until childCount) {
                if (it.parent.getChild(i).text == null) {
                    break
                }
                if (it.parent.getChild(i).text.contains(WX_Already_Open)) {
                    return
                }
            }
            it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            it.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }
}
