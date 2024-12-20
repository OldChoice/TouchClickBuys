package com.myq.touchclickbuys.maintouch

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.myq.touchclickbuys.R
import com.myq.touchclickbuys.tools.CommClass
import com.myq.touchclickbuys.tools.Sharpreferens
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
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
//        permissionCamera()

//        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
//        intent.data = Uri.parse("package:$packageName")
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

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
        cb_light.setOnCheckedChangeListener { buttonView, isChecked ->  Sharpreferens.setLight(isChecked)}

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
    private fun permissionCamera() {
        AndPermission.with(this)
            .requestCode(100)
            .permission(Manifest.permission.WRITE_SETTINGS)
            .callback(object : PermissionListener {
                override fun onSucceed(requestCode: Int, grantPermissions: List<String>) {
                    // TODO do something.
//                        Toast.makeText(LoginActivity.this, "成功", Toast.LENGTH_SHORT).show();
//                        System.out.println("成功了000000000");
                }

                override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
//                        System.out.println("失败了11111111111");
//                        MyToast.showLong("请授权此权限,否则无法使用拍照功能！");
                    // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
                    if (AndPermission.hasAlwaysDeniedPermission(
                            this@MainActivity,
                            deniedPermissions
                        )
                    ) {
//                            System.out.println("失败了222222222");
                        // 用默认的提示语。
                        AndPermission.defaultSettingDialog(this@MainActivity, 300).show()
                    }
                }
            }) // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
            // 这样避免用户勾选不再提示，导致以后无法申请权限。
            //也可以不设置。
            .rationale { requestCode, rationale -> //                        System.out.println("再次申请的33333333");
                // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                AndPermission.rationaleDialog(this@MainActivity, rationale).show()
            }
            .start()
    }


}
