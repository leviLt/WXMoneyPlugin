package com.wechatplugin.scropio.wxmoneyplugin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author lt
 * @package com.wechatplugin.scropio.wxmoneyplugin
 * @date 2019/1/14 11:17 AM
 * @describe TODO
 * @project
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        initWindowParams()
        super.onCreate(savedInstanceState)
    }

    private fun initWindowParams() {

    }
}