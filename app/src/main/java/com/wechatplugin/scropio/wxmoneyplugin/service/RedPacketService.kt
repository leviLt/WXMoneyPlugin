package com.wechatplugin.scropio.wxmoneyplugin.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Notification
import android.opengl.Visibility
import android.text.TextUtils
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import com.wechatplugin.scropio.wxmoneyplugin.utils.Log
import com.wechatplugin.scropio.wxmoneyplugin.utils.WXUtils
import java.util.concurrent.atomic.AtomicBoolean

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

    @Synchronized
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

    @Synchronized
    private fun handleWindowEvent(event: AccessibilityEvent) {
        when (currentActivityName) {
            //红包弹窗界面
            WX_Red_Packet_UI, WX_Red_Packet_UI_7 -> {
                openRetPacked()
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
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            }
        }
    }

    /**
     * 发现红包进入聊天界面
     */
    @Synchronized
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
     * 打开红包弹窗
     */
    @Synchronized
    private fun getRedPacked() {
        val accessibilityNodeInfo = rootInActiveWindow ?: return
        val list = accessibilityNodeInfo.findAccessibilityNodeInfosByText(WECHAT_PACKEY_TIP)
        if (list == null || list.size <= 0) {
            return
        }
        list.forEach {
            if (it.parent == null) {
                return
            }
            it.parent?.parent?.apply {
                accessibilityNodeInfo.findAccessibilityNodeInfosByText("已领取")?.run {
                    if (!isEmpty()) {
                        var isContain = AtomicBoolean(false)
                        isContainChildView(this@apply, this, isContain)
                        if (isContain.get()) {
                            return@apply
                        }
                    }
                    accessibilityNodeInfo.findAccessibilityNodeInfosByText("已被领完")?.run {
                        if (!isEmpty()) {
                            var isContain = AtomicBoolean(false)
                            isContainChildView(this@apply, this, isContain)
                            if (isContain.get()) {
                                return@apply
                            }
                        }
                    }
                    performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }

            }
        }
    }

    /**
     * 点开 抢红包 開 开
     */
    @Synchronized
    fun openRetPacked() {
        windows?.apply {
            forEach {
                it.root?.apply {
                    open(this)
                }
            }
        }

    }

    /**
     * 判断子View中是否含有已经领取红包  已经领取了则不打开
     */
    private fun isContainChildView(parent: AccessibilityNodeInfo, child: List<AccessibilityNodeInfo>, isContain: AtomicBoolean) {
        parent.childCount > 0.apply {
            for (i in 0 until parent.childCount) {
                parent.getChild(i)?.childCount?.apply {
                    if (this > 0) {
                        isContainChildView(parent.getChild(i), child, isContain)
                    } else {
                        for (j in 0 until child.size) {
                            isContain.apply {
                                if (child[j] == parent.getChild(i)) {
                                    set(true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 打开红包   找到节点为button的直接打开
     */
    private fun open(noteInfo: AccessibilityNodeInfo) {
        noteInfo.apply {
            if (childCount > 0) {
                for (i in 0 until childCount) {
                    noteInfo.getChild(i)?.apply {
                        open(this)
                    }
                }
            } else {
                if (className == "android.widget.Button") {
                    noteInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
            }
        }
    }
}
