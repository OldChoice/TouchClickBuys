package com.myq.touchclickbuys.tools;

import gr.free.grfastuitils.tools.SharedPreferencesHelper;

public class Sharpreferens {

    private static SharedPreferencesHelper sp = new SharedPreferencesHelper("TouchClicks");
    private static String UserName;
    private static Integer time;//设置多久刷新一次视频
    private static boolean isBack;//是否点击返回
    private static boolean Light;//是否开启自动调节刷机亮度
    private static Integer light;//自动调节的亮度保存刷机时候的值

    public static String getUserName() {
        return sp.get(UserName, "");
    }

    public static void setUserName(String userName) {
        sp.put(UserName, userName);
    }

    public static Integer getTime() {
        return sp.getInt("time", 2000);
    }

    public static void setTime(Integer time) {
        sp.put("time", time);
    }

    public static boolean isBack() {
        return sp.getBoolean("back", false);
    }

    public static void setBack(boolean back) {
        sp.put("back", back);
    }

    public static Integer getLight() {
        return sp.getInt("light", 0);
    }

    public static void setLight(Integer light) {
        sp.put("light", light);
    }

    public static boolean isLight() {
        return sp.getBoolean("Light", false);
    }

    public static void setLight(boolean light) {
        sp.put("Light", light);
    }
}
