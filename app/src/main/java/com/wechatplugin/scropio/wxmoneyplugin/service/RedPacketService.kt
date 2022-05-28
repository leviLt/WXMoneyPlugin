package com.wechatplugin.scropio.wxmoneyplugin.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.wechatplugin.scropio.wxmoneyplugin.Constant
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 包名
 */
//延时打开红包

class RedPacketService : AccessibilityService() {
    //当前activity name
    private var currentActivityName: String = ""

    //flag
    private var flag: Boolean = false

    //当前节点信息
    var currentNoteInfo = ""

    override fun onInterrupt() {
        Log.e("TAG", "onInterrupt====>")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG", "onDestroy====>")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("TAG", "onAccessibilityEvent====>${event.toString()}")
        if (event == null) {
            return
        }
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
        Log.e("TAG", "onServiceConnected====>")
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        info.notificationTimeout = 100
        info.packageNames = arrayOf(Constant.SHOPEE_PACKAGE_NAME)
        info.flags = AccessibilityServiceInfo.DEFAULT
        serviceInfo = info
    }

    private fun setCurrentActivityName(event: AccessibilityEvent) {
        val activityName = event.className.toString()
        currentNoteInfo = event.className.toString()
        Log.e("TAG", "currentActivityName====>${activityName}")
        if (activityName.startsWith(Constant.SHOPEE_HOME_ACTIVITY, true)) {
            currentActivityName = activityName
            Log.e("TAG", "currentActivityName====>")
        }
    }

    private fun handleNotificationEvent(event: AccessibilityEvent) {
//        val tip = event.text.toString()
//        if (tip.contains(WX_Notification_Tip, true)) {
//            val parcelable = event.parcelableData
//            if (parcelable is Notification) {
//                var notification = parcelable
//                notification.contentIntent.send()
//            }
//        }
    }

    private fun handleWindowEvent(event: AccessibilityEvent) {
        when (currentActivityName) {
            /*
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
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
            }
             */
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
            //7.0
            /*
            WX_7_ID.apply {
                forEach {
                    //7.0只能根据ID来获取到控件
                    info.findAccessibilityNodeInfosByViewId(it)?.apply {
                        if (isEmpty()) {
                            return@apply
                        }
                        forEach {
                            if (it.text.contains("[微信红包]")) {
                                it.parent?.parent?.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                it.parent?.parent?.parent?.parent?.performAction(
                                    AccessibilityNodeInfo.ACTION_CLICK
                                )
                                return
                            }
                        }
                    }
                }
            }
             */
        }
        /*
        for (i in 0 until info.childCount) {
            info.getChild(i)?.apply {
                recycle(info.getChild(i))
            }
        }
         */
    }

    /**
     * 打开红包弹窗
     */
    @Synchronized
    private fun getRedPacked() {
        val accessibilityNodeInfo = rootInActiveWindow ?: return
        val list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("WECHAT_PACKEY_TIP")
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
    private fun isContainChildView(
        parent: AccessibilityNodeInfo,
        child: List<AccessibilityNodeInfo>,
        isContain: AtomicBoolean
    ) {
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
    @Synchronized
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
