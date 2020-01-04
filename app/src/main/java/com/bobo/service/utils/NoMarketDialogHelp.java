package com.bobo.service.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * Created by bobohuang on 2018/12/25.
 */

public class NoMarketDialogHelp {
    public static boolean showDialogIfNeed(final Activity activity){
        Intent intent = activity.getIntent();
        if (intent == null){
            return false;
        }
        boolean  ifFromMarket = intent.getBooleanExtra("ifFromMarket",false);
        if(ifFromMarket) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setMessage("请使用“应用中心”下载安装应用");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.create().show();
        return true;
    }
}
