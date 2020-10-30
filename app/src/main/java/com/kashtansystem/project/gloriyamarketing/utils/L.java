package com.kashtansystem.project.gloriyamarketing.utils;

import android.util.Log;

public class L {
    private static final String TAG = "gloriyamarketing";

    public static final String keyUpdateTimePriceList = "keyUpdateTimePriceList";
    public static final String keyoldUpdateTimePriceList = "keyoldUpdateTimePriceList";
    public static String UpdateTimePriceList = "";
    public static String oldUpdateTimePriceList = "";

    public static void info(String text) {
        try {
            final Exception e = new Exception();
            final String className = e.getStackTrace()[1].getFileName().replace(".java", "");
            final String methodName = e.getStackTrace()[1].getMethodName();
            Log.i(className + "." + methodName, text != null ? text : "null");
        } catch (Exception e) {
            Log.i(TAG, text != null ? text : "null");
        }
    }

    public static void exception(Exception except) {
        exception(except.getMessage());
    }

    public static void exception(String text) {
        try {
            final Exception e = new Exception();
            final String className = e.getStackTrace()[1].getFileName().replace(".java", "");
            final String methodName = e.getStackTrace()[1].getMethodName();
            Log.e(className + "." + methodName, text != null ? text : "null");
        } catch (Exception e) {
            Log.e(TAG, text != null ? text : "null");
        }
    }
}