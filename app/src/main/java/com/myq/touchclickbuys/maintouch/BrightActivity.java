package com.myq.touchclickbuys.maintouch;

import android.app.Activity;
import android.app.ActivityOptions;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.myq.touchclickbuys.R;
import com.myq.touchclickbuys.tools.Sharpreferens;

import java.lang.reflect.Method;

/**
 * Create by guorui on 2020/3/17
 * Last update 2020/3/17
 * Description:小米手机要手动打开后台打开界面权限要不实现不了,控制屏幕亮度功能了
 */
public class BrightActivity extends AppCompatActivity {

    private LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_bright);
        translucentActivity(BrightActivity.this);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        permissionCamera();
        if (TouchServiceBuy.isStart()) {
            //默认
            int screenBrightness = 100;
            try {
                screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                System.out.println(screenBrightness);
            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            Sharpreferens.setLight(screenBrightness);
//            System.out.println(Sharpreferens.getLight());
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 1);
        } else {
            if (Sharpreferens.getLight() > 0) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, Sharpreferens.getLight());
                Sharpreferens.setLight(0);
            }
        }
        finish();

    }

    //背景透明
    private void translucentActivity(Activity activity) {
        try {
            activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            activity.getWindow().getDecorView().setBackground(null);
            Method activityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            activityOptions.setAccessible(true);
            Object options = activityOptions.invoke(activity);

            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> aClass = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    aClass = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    aClass, ActivityOptions.class);
            method.setAccessible(true);
            method.invoke(activity, null, options);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    private void setScreenBrightness(int paramInt) {
        Window localWindow = getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = paramInt / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }

}
