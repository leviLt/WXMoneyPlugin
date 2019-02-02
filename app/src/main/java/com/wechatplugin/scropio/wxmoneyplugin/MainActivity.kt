package com.wechatplugin.scropio.wxmoneyplugin

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.wechatplugin.scropio.wxmoneyplugin.extensions.toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    var accessibilityManager: AccessibilityManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager != null) {
            accessibilityManager!!.addAccessibilityStateChangeListener {
                //更新服务的状态
                updateServiceStatus()
            }
        }
        switch_on_off.setOnClickListener {
            //开启服务
            openService()
        }
    }

    @SuppressLint("ServiceCast")
    private fun openService() {
        if (switch_on_off.isChecked) {
            toast("点击「无障碍-微信红包工具」开启插件")
        } else {
            toast("点击「无障碍-微信红包工具」关闭插件")
        }

        val accessibleIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(accessibleIntent)
    }

    private fun updateServiceStatus() {
        switch_on_off.isChecked = isServiceEnable()
    }

    /**
     * 检查服务是否开启
     */
    private fun isServiceEnable(): Boolean {
        if (accessibilityManager == null) {
            return false
        }
        val enabledAccessibilityServiceList =
                accessibilityManager!!.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        enabledAccessibilityServiceList.forEach {
            if (it.id == "$packageName/.service.RedPacketService") {
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
    }

}
