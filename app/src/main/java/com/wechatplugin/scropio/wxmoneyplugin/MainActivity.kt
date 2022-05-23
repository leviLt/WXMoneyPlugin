package com.wechatplugin.scropio.wxmoneyplugin

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.wechatplugin.scropio.wxmoneyplugin.databinding.ActivityMainBinding
import com.wechatplugin.scropio.wxmoneyplugin.extensions.toast
class MainActivity : BaseActivity() {
    //无障碍的服务
    private var accessibilityManager: AccessibilityManager? = null

    //binding
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        accessibilityManager =
            getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        accessibilityManager?.addAccessibilityStateChangeListener {
            //更新服务的状态
            updateServiceStatus()
        }
        binding.switchOnOff.setOnClickListener {
            //开启服务
            openService()
        }
    }

    @SuppressLint("ServiceCast")
    private fun openService() {
        if (binding.switchOnOff.isChecked) {
            toast("点击「无障碍」开启插件")
        } else {
            toast("点击「无障碍」关闭插件")
        }
        val accessibleIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(accessibleIntent)
    }

    private fun updateServiceStatus() {
        binding.switchOnOff.isChecked = isServiceEnable()
    }

    /**
     * 检查服务是否开启
     */
    private fun isServiceEnable(): Boolean {
        if (accessibilityManager == null) {
            return false
        }
        val enabledAccessibilityServiceList =
            accessibilityManager?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        enabledAccessibilityServiceList?.forEach {
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
