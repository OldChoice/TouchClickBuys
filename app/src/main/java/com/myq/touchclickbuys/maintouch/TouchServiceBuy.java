package com.myq.touchclickbuys.maintouch;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.myq.touchclickbuys.MyApplication;
import com.myq.touchclickbuys.tools.Sharpreferens;

import java.util.List;

import gr.free.grfastuitils.tools.MyToast;

/**
 * Create by guorui on 2020/5/21
 * Last update 2021-11-1 14:17:41
 * Description:京东淘宝刷任务
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
//        System.out.println(Sharpreferens.isLight());
//        if (Sharpreferens.isLight()) {
//            Intent intent = new Intent(this, BrightActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }

        if (isStart()) {
            //默认
            int screenBrightness = 100;
            try {
                screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//                System.out.println(screenBrightness);
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
            jdCount = "";
            try {
                AccessibilityNodeInfo s = getRootInActiveWindow();
                if (s.getPackageName().equals("com.jd.jrapp")) {
                    doJdjr();
                } else {
                    recycle(s);
                }
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
//            if (s.getPackageName().equals("com.taobao.taobao")) {
//                touchSlow();
//            }


        }
        handler.postDelayed(runnable, Sharpreferens.getTime());

    }

    private int i = 0;
    private CharSequence jdCount = "";

    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
//            System.out.println(info);
//            System.out.println(info.getText());

            //京东，找位置
            if ((info.getPackageName() + "").equals("com.jingdong.app.mall")) {
                //先判断返回
                if ((info.getText() + "").contains("恭喜完成") || (info.getText() + "").contains("获得2000汪汪币") || (info.getText() + "").contains("获得3000汪汪币")
                        || (info.getText() + "").contains("获得4000汪汪币") || (info.getText() + "").contains("获得5000汪汪币") || (info.getText() + "").contains("获得7000汪汪币") ||
                        (info.getText() + "").contains("获得8000汪汪币") || (info.getText() + "").contains("获得9000汪汪币") || (info.getText() + "").contains("获得10000汪汪币")
                        || (info.getText() + "").contains("已达上限")) {
                    //  mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    //京东返回键一下全返回了，这里使用滑动退出操作
                    touchBack();
                }

                //不取图片了，取也取不出来有问题，就直接取前面的字符串然后算大概去完成图片的点击位置
                if ((info.getText() + "").contains("s可得2000汪汪币") || (info.getText() + "").contains("s可得3000汪汪币") || (info.getText() + "").contains("s可得4000汪汪币")
                        || (info.getText() + "").contains("s可得5000汪汪币") || (info.getText() + "").contains("s可得7000汪汪币")) {
//                    System.out.println(info);
//                    System.out.println(info.getText());
                    String jds = jdCount.toString();
                    String jds1 = jds.substring(jds.indexOf("(") + 1, jds.indexOf(")"));
                    String a = jds1.substring(0, jds1.indexOf("/"));
                    String b = jds1.substring(jds1.indexOf("/") + 1);
                    int sf = Integer.parseInt(a) / Integer.parseInt(b);
                    //算出一个分组是不是全部结束了,未除尽取整为0
                    if (sf == 0) {
//                        System.out.println(jdCount);
                        Rect rect = new Rect();
                        info.getBoundsInScreen(rect);
                        doTouchGestureClick(getResources().getDisplayMetrics().widthPixels - rect.left, rect.exactCenterY() - 20);
                    }
                }
                //记录上一条留着下次对比是否完成用
                jdCount = info.getText() + "";

            } else if ((info.getPackageName() + "").equals("com.taobao.taobao")) {
                ////////淘宝，是找控件
                if ((info.getContentDescription() + "").contains("任务完成") || (info.getText() + "").contains("任务完成")
                        || (info.getContentDescription() + "").contains("任务已完成") || (info.getText() + "").contains("任务已完成")
                        || (info.getContentDescription() + "").contains("任务已经") || (info.getText() + "").contains("任务已经")
                        || (info.getContentDescription() + "").contains("明天再来吧") || (info.getText() + "").contains("明天再来吧")) {
                    mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }
                if ((info.getContentDescription() + "").contains("浏览得奖励") || (info.getText() + "").contains("浏览得奖励")) {
                    //每次执行都滑动
                    touchSlow();
                }
                if ((info.getText() + "").equals("去浏览")) {
                    i++;
                    if (i == 1) {
                        //找到你的节点以后 就直接点击他就行了
                        info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            } else if ((info.getPackageName() + "").equals("com.jd.jrapp")) {


            }

        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    //当前值是否包含
    private boolean isContains(CharSequence str, String contain) {
        return (str + "").contains(contain);
    }

    // 输入框输入，只能修改输入框
    public void setText(AccessibilityNodeInfo info, String str) {
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, str);
        info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

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

    //京东金融
    private void touchJDJR() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        doTouchGesture(displayMetrics.widthPixels / 2, displayMetrics.heightPixels * 16 / 20, displayMetrics.widthPixels / 2, displayMetrics.heightPixels * 5 / 20, 500);
    }

    boolean isDetailBack = false;

    //京东金融操作
    private void doJdjr() {
        //这个地方要判断到哪一页了然后执行哪一页的逻辑
        List<AccessibilityNodeInfo> list = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.jd.jrapp:id/iv_stock_red");
        List<AccessibilityNodeInfo> title = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.jd.jrapp:id/tv_tab_strip");//顶部全部栏
        List<AccessibilityNodeInfo> bottom = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.jd.jrapp:id/ll_nav_bar");//底部导航栏高度,点击列表时候因为导航栏下面还有列表
        if (list.size() > 0) {
            isDetailBack = false;
            Rect rect = new Rect();
            list.get(0).getBoundsInScreen(rect);
            Rect rect1 = new Rect();
            bottom.get(0).getBoundsInScreen(rect1);
            if (rect1.top < rect.exactCenterY() + 70) {
                touchJDJR();
            } else {
                doTouchGestureClick(rect.exactCenterX(), rect.exactCenterY());
            }

        } else if (title.size() > 0) {
            touchJDJR();
        }

        List<AccessibilityNodeInfo> detail = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.jd.jrapp:id/minchart_view");
//        System.out.println(detail.size());
        if (detail.size() > 0) {
            if (isDetailBack) {
                touchBack();
            }
            Rect rect = new Rect();
            detail.get(0).getBoundsInScreen(rect);
            //  System.out.println(rect);
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                System.out.println(e);
                e.printStackTrace();
            }
            for (int i = rect.left; i < rect.right; i = i + 50) {
                for (int j = rect.top; j < rect.bottom - 400; j = j + 60) {
                    List<AccessibilityNodeInfo> s33 = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.jd.jrapp:id/minchart_view");
                    if (s33.size() == 0) break;
                    doTouchGestureClick(i, j);
//                    System.out.println(i + "----" + j);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }
                    //判断循环结束也没跳转就返回
                    if (i + 50 > rect.right && j + 60 > rect.bottom - 400) {
                        touchBack();
                    }
                }
            }
        }

        List<AccessibilityNodeInfo> vMoney = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.jd.jrapp:id/web_show_root");
        if (vMoney.size() > 0) {
            isDetailBack = true;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.out.println(e);
                e.printStackTrace();
            }
            touchBack();
        }

    }

}