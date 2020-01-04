package com.bobo.service.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;

/**
 * Created by bobohuang on 2018/10/8.
 */

public class BindMultiAppServiceHelp {
    private static final String TAG = "BindMultiAppServiceHelp";
    private static BindMultiAppServiceHelp sBindWakeupServerServiceHelp;
    private Context mContext;
    private IWakeupService mIWakeupService;
    private Handler mHandler = new Handler();

    private static synchronized BindMultiAppServiceHelp getInstance(Context context) {
        if (sBindWakeupServerServiceHelp == null) {
            sBindWakeupServerServiceHelp = new BindMultiAppServiceHelp(context);
        }
        return sBindWakeupServerServiceHelp;
    }

    private BindMultiAppServiceHelp(Context context) {
        mContext = context.getApplicationContext();
    }

    public static void bindService(Context context) {
        BindMultiAppServiceHelp help = getInstance(context);
        help.bindService();
    }

    private void bindService() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bindService();
            }
        }, 2000);
        if (mIWakeupService != null) {
            return;
        }
        Intent intent = new Intent("com.bobo.service.utils.BIND_WAKEUP_SERVICE");
        intent.setPackage("com.tencent.mm.a");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!mContext.bindServiceAsUser(intent, mConnection, Context.BIND_AUTO_CREATE, UserHandle.SYSTEM)) {
                Log.d(TAG, "bind delay");
            } else {
                Log.d(TAG, "bind start");
            }
        } else {
            if (!mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)) {
                Log.d(TAG, "bind delay");
            } else {
                Log.d(TAG, "bind start");
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接后拿到 Binder，转换成 AIDL，在不同进程会返回个代理
            Log.d(TAG, "onServiceConnected");
            mIWakeupService = IWakeupService.Stub.asInterface(service);
            try {
                mIWakeupService.log(mContext.getPackageName());
            } catch (RemoteException e) {
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            mIWakeupService = null;
            bindService();
        }
    };
}
