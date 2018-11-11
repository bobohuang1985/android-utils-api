package utils.bobo.com.boboutils.App.overlay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


/**
 * Created by bobohuang on 2018/10/8.
 */

public class OverlayViewServiceHelp {
    private static final String TAG = "BindWakeupHelp";
    private static OverlayViewServiceHelp sOverlayViewServiceHelp;
    private Context mContext;
    private IOverlayViewService mIOverlayViewService;
    private Handler mHandler = new Handler();

    private static synchronized OverlayViewServiceHelp getInstance(Context context) {
        if (sOverlayViewServiceHelp == null) {
            sOverlayViewServiceHelp = new OverlayViewServiceHelp(context);
        }
        return sOverlayViewServiceHelp;
    }

    private OverlayViewServiceHelp(Context context) {
        mContext = context.getApplicationContext();
    }

    public static void bindService(Context context) {
        OverlayViewServiceHelp help = getInstance(context);
        help.bindService();
    }

    public static void showOverlay(Activity activity) {
        OverlayViewServiceHelp help = getInstance(activity.getApplicationContext());
        help.show(activity);
    }
    private void show(Activity activity){
        if (mIOverlayViewService != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(OverlayViewService.KEY_WINDOWMANAGER_LAYOUTPARAMS,
                    activity.getWindow().getAttributes());
            try {
                mIOverlayViewService.showOverlay(bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Log.e(TAG, "Service is null");
        }
    }
    private void bindService() {
        if (mIOverlayViewService != null) {
            return;
        }
        Intent intent = new Intent("utils.bobo.com.boboutils.BIND_OVERLAY_VIEW_SERVICE");
        intent.setPackage("utils.bobo.com.boboutils");
        if (!mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)) {
            Log.d(TAG, "bind fail");
        } else {
            Log.d(TAG, "bind start");
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接后拿到 Binder，转换成 AIDL，在不同进程会返回个代理
            Log.d(TAG, "onServiceConnected");
            mIOverlayViewService = (IOverlayViewService) IOverlayViewService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            mIOverlayViewService = null;
        }
    };
}
