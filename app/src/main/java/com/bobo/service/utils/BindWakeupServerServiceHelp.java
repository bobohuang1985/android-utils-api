package com.bobo.service.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by bobohuang on 2018/10/8.
 */

public class BindWakeupServerServiceHelp {
    private static BindWakeupServerServiceHelp sBindWakeupServerServiceHelp;
    private Context mContext;
    private IWakeupService mIWakeupService;

    private static synchronized BindWakeupServerServiceHelp getInstance(Context context){
        if (sBindWakeupServerServiceHelp == null){
            sBindWakeupServerServiceHelp = new BindWakeupServerServiceHelp(context);
        }
        return sBindWakeupServerServiceHelp;
    }
    private BindWakeupServerServiceHelp(Context context){
        mContext = context.getApplicationContext();
    }
    public static void bindService(Context context){
        BindWakeupServerServiceHelp help = getInstance(context);
        help.bindService();
    }
    private void bindService(){
        if (mIWakeupService != null){
            return;
        }
        Intent intent = new Intent("com.bobo.service.utils.BIND_WAKEUP_SERVICE");
        intent.setPackage("utils.bobo.com.boboutils");
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接后拿到 Binder，转换成 AIDL，在不同进程会返回个代理
            mIWakeupService = IWakeupService.Stub.asInterface(service);
            try {
                mIWakeupService.log(mContext.getPackageName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIWakeupService = null;
            bindService();
        }
    };
}
