package com.myq.touchclickbuys.maintouch

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.myq.touchclickbuys.R
import com.myq.touchclickbuys.tools.CommClass
import com.myq.touchclickbuys.tools.Sharpreferens
import gr.free.grfastuitils.activitybase.BaseActivity
import gr.free.grfastuitils.tools.MyToast
import kotlinx.android.synthetic.main.activity_main.*
/**
* Create by guorui on 2020/5/21
* Last update 2020/5/21
* Description:
**/
class MainActivity : BaseActivity(), View.OnClickListener {
    val strName = Build.MODEL + "(" + CommClass.getDeviceUUID() + ")" //手机名称和固定唯一码组成名称


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setView()
        setListener()

    }


    override fun setView() {
        et_times.setText((Sharpreferens.getTime() / 1000).toString())
        cb_light.isChecked = Sharpreferens.isLight()
        tv_name.text = strName


    }

    override fun setListener() {
        btn_setting.setOnClickListener(this)
        btn_open.setOnClickListener(this)
        btn_close.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_open -> {
                Sharpreferens.setTime(et_times.text.toString().toInt() * 1000)
                startTouchs()
            }
            R.id.btn_close -> {
                if (TouchServiceBuy.stops()) {
                    MyToast.showShort("功能关闭成功")
                } else {
                    MyToast.showShort("功能未开启")
                }
            }
            R.id.btn_setting -> {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
        }

    }

    private fun startTouchs() {
        if (!TouchServiceBuy.isStart()) {
            try {
                this.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } catch (e: Exception) {
                this.startActivity(Intent(Settings.ACTION_SETTINGS))
                e.printStackTrace()
            }
        }
    }


}
