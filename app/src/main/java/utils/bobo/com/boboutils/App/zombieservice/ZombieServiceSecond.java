
package utils.bobo.com.boboutils.App.zombieservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ZombieServiceSecond extends Service {
    private static final String TAG = "ZombieServiceSecond";
    public static final String BIND_ACTION = "com.boboutils.action.BIND_ZOMBIESERVICESECOND";
    private IZombieServiceFirst mIZombieServiceFirst;
    public ZombieServiceSecond() {
    }
    public void onCreate(){
        super.onCreate();
        bindService(new Intent(ZombieServiceFirst.BIND_ACTION)
                        .setComponent(new ComponentName(this,ZombieServiceFirst.class)),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        bindService(new Intent(ZombieServiceFirst.BIND_ACTION)
                        .setComponent(new ComponentName(this,ZombieServiceFirst.class)),
                mServiceConnection, Context.BIND_AUTO_CREATE);
        return START_STICKY;
    }
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIZombieServiceFirst = IZombieServiceFirst.Stub.asInterface(service);
            try {
                Log.d(TAG, "onServiceConnected:"+ mIZombieServiceFirst.getServiceName());
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
    private IZombieServiceSecond.Stub mBinder = new IZombieServiceSecond.Stub() {
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                               double aDouble, String aString){

        }
        public String getServiceName(){
            return ZombieServiceSecond.class.getSimpleName();
        }
    };
}
