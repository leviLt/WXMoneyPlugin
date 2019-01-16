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
        val nodeInfosByText = rootInActiveWindow.findAccessibilityNodeInfosByText(WX_FriendSend_RedPacket_ForList_Tip)
        if (nodeInfosByText != null && nodeInfosByText.size > 0) {
            nodeInfosByText.forEach {
                it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                if (it.parent != null && it.parent.isCheckable) {
                    it.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
            }
        }
    }

    /**
     * 点开红包
     */
    private fun getRedPacked() {
        val accessibilityNodeInfo = rootInActiveWindow
        if (accessibilityNodeInfo == null) {
            return
        }
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
        /*
        val displayMetrics = resources.displayMetrics
        val dpi = displayMetrics.densityDpi
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            toast("Android 7.0一下暂时不支持")
            return
        }
        if (!openPacketFlag) {
            openPacketFlag = true
            var path = Path()
            var x = 0f
            var y = 0f
            when (dpi) {
                320 -> {//720
                    x = 355f
                    x = 780f
                }
                440 -> {//1080*2160
                    x = 450f
                    y = 1250f
                }
                480 -> {//1080
                    x = 533f
                    y = 1115f
                }

                640 -> {//1440
                    x = 750f
                    y = 1575f
                }
            }
            if (Build.BOARD == "")
                if (WX_versionCode == WX_7_VERCODE) {
                    y += 0.15.toFloat() * y
                }
            when (0f) {
                //如果为0，按照1080p处理
                x, y -> {
                    x = 533f
                    y = 1250f
                }
            }
            path.moveTo(x, y)
            try {
                val gestureDescription = GestureDescription.Builder()
                val description = gestureDescription.addStroke(GestureDescription.StrokeDescription(path, 450, 50)).build()
                dispatchGesture(description, @RequiresApi(Build.VERSION_CODES.N)
                object : GestureResultCallback() {
                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        Log.log("gestureDescription : onCancelled")
                        openPacketFlag = false
                        super.onCancelled(gestureDescription)
                    }

                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        openPacketFlag = false
                        Log.log("gestureDescription : onCompleted")
                    }
                }, null)
            } catch (e: Exception) {
                e.printStackTrace()
                openPacketFlag = false
            }
        }
        */
    }
}
