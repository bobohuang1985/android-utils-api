package utils.bobo.com.boboutils.App;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by bobohuang(bobohuang1985@qq.com) on 2016/10/31.
 */
public class WakeLockTestApi {
    private static final String TAG = "WakeLockTestApi";
    private Context mContext;
    private PowerManager.WakeLock mCpuWakeLock;
    private PowerManager mPowerManager;
    private static WakeLockTestApi sApi;
    public synchronized static WakeLockTestApi getInstance(Context context){
        if(sApi == null){
            sApi = new WakeLockTestApi(context);
        }
        return sApi;
    }
    private WakeLockTestApi(Context context){
        mContext = context;
        mPowerManager = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
    }
    public boolean isWakeLockExist(){
        if(mCpuWakeLock != null){
            return true;
        }
        return false;
    }
    public boolean getAWakeLock(){
        if(mCpuWakeLock != null){
            return true;
        }
        mCpuWakeLock = mPowerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, TAG + "-cpu");
        mCpuWakeLock.acquire();
        return true;
    }
    public boolean releaseWakeLock(){
        if(mCpuWakeLock == null){
            return true;
        }
        mCpuWakeLock.release();
        mCpuWakeLock = null;
        return true;
    }
}
