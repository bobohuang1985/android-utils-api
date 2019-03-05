package com.bobo.service.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by bobohuang on 2018/12/27.
 */

public class SilentInstallHelp {
    public static final String TAG = "SilentInstallHelp";
    public static final int RESULT_FAILED = -1;
    public static final int RESULT_SUCCESS = 1;

    public static boolean silentInstallIfNeed(final Activity activity) {
        Intent intent = activity.getIntent();
        if (intent == null) {
            return false;
        }
        boolean isSilentInstall = intent.getBooleanExtra("SilentInstall", false);
        Log.d(TAG, "isSilentInstall = " + isSilentInstall);
        if (!isSilentInstall) {
            return false;
        }
        final String sourceFilePath = intent.getStringExtra("ApkFilePath");
        Log.d(TAG, "sourceFilePath = " + sourceFilePath);
        if (TextUtils.isEmpty(sourceFilePath)) {
            return false;
        }
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            Log.d(TAG, "sourceFile no exists");
            return false;
        }
        if (!sourceFile.isFile()) {
            Log.d(TAG, "sourceFile no file");
            return false;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                install(sourceFilePath);
            }
        }).start();
        activity.finish();
        return true;
    }

    private static int install(String app_path) {
        Log.d(TAG, "install app_path = " + app_path);
        int result = RESULT_FAILED;
        try {
            final File sourceFile = new File(app_path);
            //String cmd = "pm install -r " + app_path;
            //Process p = Runtime.getRuntime().exec(cmd);
            Process p = Runtime.getRuntime().exec(new String[]{"pm", "install", "-r", app_path});
            //Process p =  Runtime.getRuntime().exec("pm", new String[]{"-r",app_path});
            InputStream is = p.getInputStream();
            InputStream isErr = p.getErrorStream();


            p.waitFor();
            int exitValue = p.exitValue();
            Log.d(TAG, "install " + app_path + "; exitValue = " + exitValue);
            String isString = readTextFile(is);
            Log.d(TAG, "install " + app_path + "; isString = " + isString);
            if (exitValue == 0
                    && isString != null
                    && isString.contains("Success")) {
                result = 1;//PackageManager.INSTALL_SUCCEEDED;
                Log.d(TAG, "install " + app_path + "; install SUCCESS ");
            } else {
                String isErrorString = readTextFile(isErr);
                Log.e(TAG, "install " + app_path + "; isErrorString = " + isErrorString);
                result = installFailureToInt(isErrorString);
                Log.e(TAG, "install " + app_path + "; isError result = " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static int installFailureToInt(String errorString) {
        if (TextUtils.isEmpty(errorString)) {
            return -0xffff;
        }
        // final int result = obs.result;
        Field[] fields = PackageManager.class.getFields();
        for (Field f : fields) {
            if (f.getType() == int.class) {
                int modifiers = f.getModifiers();
                // only look at public final static fields.
                if (((modifiers & Modifier.FINAL) != 0) &&
                        ((modifiers & Modifier.PUBLIC) != 0) &&
                        ((modifiers & Modifier.STATIC) != 0)) {
                    String fieldName = f.getName();
                    if (fieldName.startsWith("INSTALL_FAILED_") ||
                            fieldName.startsWith("INSTALL_PARSE_FAILED_")) {
                        // get the int value and compare it to result.
                        try {
                            if (errorString.contains(fieldName)) {
                                return f.getInt(fieldName);
                                //Object result = LoadMethod.getObject(null, "android.content.pm.PackageManager", fieldName);
                                //return (Integer)result;
                            }
                        } catch (Exception e) {
                            // this shouldn't happen since we only look for public static fields.
                        }
                    }
                }
            }
        }
        return -0xffff;
    }

    private static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte buf[] = new byte[inputStream.available()];
            int len;
            len = inputStream.read(buf);
            while (len > 0) {
                outputStream.write(buf, 0, len);
                len = inputStream.read(buf);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
        }
        return outputStream.toString();
    }
}
