package utils.bobo.com.boboutils.App.zombieservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ZombieServiceFirst extends Service {
    public static final String BIND_ACTION = "com.boboutils.action.BIND_ZOMBIESERVICEFIRST";
    private static final String TAG = "ZombieServiceFirst";
    private IZombieServiceSecond mIZombieServiceSecond;
    public ZombieServiceFirst() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        bindService(new Intent(ZombieServiceSecond.BIND_ACTION)
                        .setComponent(new ComponentName(this,ZombieServiceSecond.class)),
                mServiceConnection, Context.BIND_AUTO_CREATE);
        return START_STICKY;
    }
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIZombieServiceSecond = IZombieServiceSecond.Stub.asInterface(service);
            try {
                Log.d(TAG, "onServiceConnected:"+ mIZombieServiceSecond.getServiceName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:");
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private IZombieServiceFirst.Stub mBinder = new IZombieServiceFirst.Stub() {
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                        double aDouble, String aString){

        }
        public String getServiceName(){
            return ZombieServiceFirst.class.getSimpleName();
        }
    };
}
