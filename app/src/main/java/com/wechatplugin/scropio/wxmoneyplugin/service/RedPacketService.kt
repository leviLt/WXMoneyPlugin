package com.wechatplugin.scropio.wxmoneyplugin.service

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent

class RedPacketService : AccessibilityService() {
    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }


}
