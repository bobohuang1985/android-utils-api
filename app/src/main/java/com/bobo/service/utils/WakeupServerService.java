package com.bobo.service.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.morgoo.helper.Log;

public class WakeupServerService extends Service {
    private static final String TAG = "WakeupServerService";
    public WakeupServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }
    private IBinder mIBinder = new IWakeupService.Stub() {
        @Override
        public void log(String clientPackageName){
            Log.e(TAG, clientPackageName+" bind " + WakeupServerService.this.getPackageName());
        }
    };
}
