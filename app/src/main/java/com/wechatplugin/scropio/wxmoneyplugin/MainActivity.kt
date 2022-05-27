package com.wechatplugin.scropio.wxmoneyplugin

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.lifecycle.lifecycleScope
import com.wechatplugin.scropio.wxmoneyplugin.databinding.ActivityMainBinding
import com.wechatplugin.scropio.wxmoneyplugin.extensions.killAppByPackageName
import com.wechatplugin.scropio.wxmoneyplugin.extensions.openAppByPackage
import com.wechatplugin.scropio.wxmoneyplugin.extensions.toast
import com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.net.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    //无障碍的服务
    private var accessibilityManager: AccessibilityManager? = null

    @Inject
    lateinit var repository: Repository

    //binding
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        //获取网络动作
        getActionByNetWork()
    }


    private fun initView() {
        accessibilityManager =
            getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        accessibilityManager?.addAccessibilityStateChangeListener {
            //更新服务的状态
            updateServiceStatus()
            if (it) {
                openAppByPackage(Constant.SHOPEE_PACKAGE_NAME)
            } else {
                killAppByPackageName(Constant.SHOPEE_PACKAGE_NAME)
            }
        }
        binding.switchOnOff.setOnClickListener {
            //开启服务
            openService()
        }
        binding.btnOpen.setOnClickListener {
            openAppByPackage(Constant.SHOPEE_PACKAGE_NAME)
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

    /**
     * 网络请求
     */
    private fun getActionByNetWork() {
        lifecycleScope.launch {
            repository.getAction("")
                .catch {
                    toast(it.message ?: "")
                }
                .collect {

                }
        }
    }
}
