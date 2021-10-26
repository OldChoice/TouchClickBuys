package com.myq.touchclickbuys.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.myq.touchclickbuys.MyApplication;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 我的临时工具类
 */
public class CommClass {

    /**
     * 检测网络是否可用
     */
    public static boolean isOpenNetwork() {
        // 可以检测WiFi和数据流量是否开启
        ConnectivityManager connManager = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }

        return false;
    }

    /**
     * 判断是不是IP
     */
    public static boolean isIPAddress(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }


    /**
     * 设置状态栏和导航栏透明为附近颜色
     */
    @SuppressLint("InlinedApi")
    public static void setBarColor(Activity activity) {
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            // 透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }

    /**
     * 对比两个时间是否相差12小时，当然计算时间差,默认是true大于12小时的
     */
    public static boolean dateDiff(String startTime, String endTime, String format) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数long diff;try {
        // 获得两个时间的毫秒时间差异
        long diff;
        boolean b = true;
        try {
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            long day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒//输出结果
            // System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟"
            // + sec + "秒。");
            if (hour > 12) {
                b = true;
            } else if (day > 0) {
                b = true;
            } else {
                b = false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 获取当前时间 第二种方法能获取秒以下的详细时间,如果传入为空就默认输出格式为"yyyy-MM-dd HH:mm:ss"，例如2016-09-06
     * 16:16:20
     */
    @SuppressLint("SimpleDateFormat")
    public static String getSystemTime(String format) {
        // SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        // String date = sDateFormat.format(new java.util.Date());
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);

        return str;

    }

    /**
     * 字符串时间从一种格式转换成另一种格式string,传过来要转换的time是 2016-09-06 16:16:20。15
     * ，这里面给了特定的格式转换成2016-09-06 16:16:20
     */
    public static String timeTranslateFormat(String time) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long now = 0;
        try {
            now = formatter.parse(time).getTime();
        } catch (ParseException e) {
            System.out.println("format parseException:" + e.toString());
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        // DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return formatter.format(calendar.getTime());
    }

    /**
     * 字符串时间从一种格式转换成另一种格式string,传过来要转换的time是 2016-09-06
     * ，这里面给了特定的格式转换成2016-09-06
     */
    public static String dateTranslateFormat(String date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long now = 0;
        try {
            now = formatter.parse(date).getTime();
        } catch (ParseException e) {
            System.out.println("format parseException:" + e.toString());
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        // DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(calendar.getTime());
    }

    /**
     * 对比两个时间是否为同一天，默认为false不是同一天
     */
    public static boolean isSameDay(String time) {

        boolean same = false;

        String nowTime = CommClass.getSystemTime("yyyy-MM-dd");
        // 先判断当前月份是否一样,再判断月份和日期,如果想对数据不一样就是false，不是同一天
        if (!nowTime.substring(0, 4).equals(time.substring(0, 4))) {
            same = false;
        } else if (!nowTime.substring(5, 7).equals(time.substring(5, 7))) {
            same = false;
        } else if (!nowTime.substring(8, 10).equals(time.substring(8, 10))) {
            same = false;
        } else {
            same = true;
        }

        return same;
    }

    /**
     * 转换url地址格式,由于转换的时候空格会转换为加号，所以编译后要替换加号
     */
    public static String toURLDecoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.d("d", "toURLDecoded error:" + paramString);
            return "";
        }
        try {
            paramString = URLEncoder.encode(paramString, "UTF-8");
            paramString = paramString.replaceAll("\\+", "%20");
            return paramString;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("e", "toURLDecoded error:" + paramString, e);
        }

        return paramString;
    }

    /**
     * 获得屏幕分辨率
     */
    public static DisplayMetrics getDisplayMetrix(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 强行隐藏软键盘
     */
    public static void hideSoftKeybord(Activity activity) {

        if (null == activity) {
            return;
        }
        try {
            final View v = activity.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }
    }

    /**
     * dialog等强行隐藏软键盘
     * CommClass.hideSoftKeybordDialog(v.getWindowToken(), SHInspectionActivity.this);
     */
    public static void hideSoftKeybordDialog(IBinder token, Activity activity) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 判断是否平板设备
     *
     * @param context
     * @return true:平板,false:手机
     */
    public static boolean isTabletDevice(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    //   byte转换为16进制
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    //获取手机唯一识别码,手机重置可能会改变
    public static String getDeviceUUID() {

        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                serial = android.os.Build.getSerial();
//            } else {
            serial = Build.SERIAL;
//            }
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }


}
