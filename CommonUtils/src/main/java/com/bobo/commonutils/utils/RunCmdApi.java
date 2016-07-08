package com.bobo.commonutils.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Created by bobohuang(bobohuang!985@qq.com) on 2016/7/5.
 */
public class RunCmdApi {
    private static final String TAG = "RunCmdApi";
    public static void execCmd(String cmd) {
        try {
            Log.i(TAG, "command = " + cmd);
            Runtime localException = Runtime.getRuntime();
            Process exec = localException.exec(cmd);
            exec.waitFor();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static Vector execCmdWithReturn(String cmd) {
        Vector result = new Vector();
        try {
            Log.d(TAG, "execCmdWithReturn:" + cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes(cmd + ";exit\n");
            outputStream.flush();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
            process.waitFor();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }
}
