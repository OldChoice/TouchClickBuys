package com.myq.touchclickbuys.maintouch;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.myq.touchclickbuys.MyApplication;
import com.myq.touchclickbuys.tools.Sharpreferens;

import gr.free.grfastuitils.tools.MyToast;

/**
 * Create by guorui on 2020/5/21
 * Last update 2020/5/21
 * Description:京东刷任务
 */
public class TouchServiceBuy extends AccessibilityService {

    public static TouchServiceBuy mService;
    private Handler handler;
    private boolean isBegin = false;

    //初始化
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        MyToast.showShort("滑动已开启");
        handler = new Handler();
        mService = this;
        slides();
        startBright();
    }

    //处理挂机自动调节亮度
    private void startBright() {
        if (Sharpreferens.isLight()) {
            Intent intent = new Intent(this, BrightActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    //实现辅助功能
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //slides();
        isBegin = true;

    }

    @Override
    public void onInterrupt() {
        MyToast.showShort("(；′⌒`)\r\n功能被迫中断");
        mService = null;
        isBegin = false;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyToast.showShort("%>_<%\r\n功能已关闭");
        //结束实时数据的获取
        handler.removeCallbacks(runnable);
        mService = null;
        startBright();

    }

    /**
     * 辅助功能是否启动
     */
    public static boolean isStart() {
        return mService != null;
    }

    /**
     * 辅助功能是否启动
     */
    public static boolean stops() {
        if (mService != null) {
            mService.disableSelf();
            MyToast.showShort("%>_<%\r\n功能已关闭");
            mService = null;
            return true;
        } else {
            return false;
        }

    }

    //定时每6秒获取一次数据
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            slides();
        }

    };

    public void slides() {

        PowerManager pm = (PowerManager) MyApplication.getInstance().getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        //开始，屏幕亮才执行滑动
        if (isBegin && screenOn) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                MyToast.showShort("7.0及以上才能使用手势");
                return;
            }
//            AccessibilityServiceInfo info=getServiceInfo();
//            info.flags=AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
            i = 0;
            AccessibilityNodeInfo s = getRootInActiveWindow();
            recycle(s);
            if (s.getPackageName().equals("com.taobao.taobao")) {
                touchSlow();
            }

        }
        handler.postDelayed(runnable, Sharpreferens.getTime());

    }

    private int i = 0;
    private boolean jdclick = true;
    private CharSequence jdCount = "";

    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
//            System.out.println(info);
//            System.out.println(info.getText());
            ///////////京东
//            if ((info.getText() + "").contains("恭喜完成") || (info.getText() + "").contains("获得5000金币") || (info.getText() + "").contains("获得7000金币") ||
//                    (info.getText() + "").contains("获得8000金币") || (info.getText() + "").contains("获得9000金币") || (info.getText() + "").contains("获得10000金币")) {
//                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
//            }
//            System.out.println(info.getText() + "---" + jdclick);
//            if ((info.getText() + "").equals("去完成") & jdclick) {
//                i++;
//                if (i == 1) {
//                    //找到你的节点以后 就直接点击他就行了
//                    info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
//                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                }
//            }
//            jdclick = false;
//            if ((info.getText() + "").contains("浏览") || (info.getText() + "").contains("去逛") || (info.getText() + "").contains("去玩")
//                    || (info.getText() + "").contains("逛店")) {
//                jdclick = true;
//            }


            //京东新版本更新
            if (info.getPackageName().equals("com.jingdong.app.mall")) {
                //先判断返回
                if ((info.getText() + "").contains("恭喜完成") || (info.getText() + "").contains("获得2000汪汪币") || (info.getText() + "").contains("获得3000汪汪币")
                        || (info.getText() + "").contains("获得4000汪汪币") || (info.getText() + "").contains("获得5000汪汪币") || (info.getText() + "").contains("获得7000汪汪币") ||
                        (info.getText() + "").contains("获得8000汪汪币") || (info.getText() + "").contains("获得9000汪汪币") || (info.getText() + "").contains("获得10000汪汪币")) {
                    //  mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    //京东返回键一下全返回了，这里使用滑动退出操作
                    touchBack();
                }

                //去完成改成了图片就为空了
                if ((info.getText() + "").equals("") & jdclick) {
                    i++;
                    if (i == 1) {
                        //里面被京东已某种手段处理了找不到那个图片只能找到相应位置
                        jdclick = false;
                        Rect rect = new Rect();
                        info.getBoundsInScreen(rect);
                        doTouchGestureClick(rect.exactCenterX(), rect.exactCenterY());
                    }
                }
                if ((info.getText() + "").contains("s可得2000汪汪币") || (info.getText() + "").contains("s可得3000汪汪币") || (info.getText() + "").contains("s可得7000汪汪币")) {
                    jdclick = true;
//                    System.out.println(jdCount);
//                    String jds = jdCount.toString();
//                    String jds1 = jds.substring(jds.indexOf("(") + 1, jds.indexOf(")"));
//                    String a = jds1.substring(0, jds1.indexOf("/"));
//                    String b = jds1.substring(jds1.indexOf("/") + 1);
//                    int sf = Integer.parseInt(a) / Integer.parseInt(b);
//                    //算出一个分组是不是全部结束了
//                    if (sf == 0) {
//                        jdclick = true;
//                    }
//                    System.out.println(sf);
                }
                jdCount = info.getText();

            } else if (info.getPackageName().equals("com.taobao.taobao")) {
                ////////淘宝
                if ((info.getContentDescription() + "").contains("任务完成") || (info.getText() + "").contains("任务完成") || (info.getContentDescription() + "").contains("任务已完成")
                        || (info.getText() + "").contains("任务已完成") || (info.getContentDescription() + "").contains("任务已经")
                        || (info.getText() + "").contains("任务已经")) {
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }
                if ((info.getText() + "").equals("去浏览")) {
                    i++;
                    if (i == 1) {
                        //找到你的节点以后 就直接点击他就行了
                        info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }

        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    //执行滑动操作
    private void doTouchGesture(float mx, float my, float lx, float ly, long duration) {
        Path path = new Path();
        path.moveTo(mx, my);
        path.lineTo(lx, ly);
        final GestureDescription.StrokeDescription sd = new GestureDescription.StrokeDescription(path, 0, duration);
        mService.dispatchGesture(new GestureDescription.Builder().addStroke(sd).build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
//                MyToast.showShort("上滑成功");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
//                MyToast.showShort("手势失败，请重启手机再试");
            }
        }, null);
        path.close();
    }

    //点击操作
    private void doTouchGestureClick(float mx, float my) {
        Path path = new Path();
        path.moveTo(mx, my);
        final GestureDescription.StrokeDescription sd = new GestureDescription.StrokeDescription(path, 10, 10);
        mService.dispatchGesture(new GestureDescription.Builder().addStroke(sd).build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
//                MyToast.showShort("成功");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
//                MyToast.showShort("手势失败，请重启手机再试");
            }
        }, null);
        path.close();
    }

    //淘宝方式黑屏并模拟缓慢上滑
    private void touchSlow() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //从屏幕的2/3处开始滑动
        doTouchGesture(displayMetrics.widthPixels / 2, displayMetrics.heightPixels * 11 / 20, displayMetrics.widthPixels / 2, displayMetrics.heightPixels * 10 / 20, 500);
    }

    //京东不能用指令返回则用滑动返回，京东返回键一下全返回了，这里使用滑动退出操作
    private void touchBack() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //从屏幕的2/3处开始滑动
        doTouchGesture(0, displayMetrics.heightPixels / 2, displayMetrics.widthPixels / 2, displayMetrics.heightPixels / 2, 200);
    }


}