package utils.bobo.com.boboutils.App.overlay;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import utils.bobo.com.boboutils.R;


public class OverlayViewService extends Service {
    private final static String TAG = "OverlayViewService";
    public final static String KEY_WINDOWMANAGER_LAYOUTPARAMS = "LayoutParams";
    private static View sOverlayView;
    private Handler mHandler = new Handler();

    public OverlayViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }
    private void show(WindowManager.LayoutParams inParam) {
        sOverlayView = LayoutInflater.from(this).inflate(R.layout.system_overlay, null);
        if (sOverlayView != null && !sOverlayView.isShown()) {
            sOverlayView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
                    wm.removeView(sOverlayView);
                    sOverlayView = null;
                }
            });
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            WindowManager.LayoutParams params = inParam;//new WindowManager.LayoutParams();

            //params.type = WindowManager.LayoutParams.TYPE_PHONE;//WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            params.flags = 0;//WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.format = PixelFormat.TRANSPARENT;

            params.gravity = Gravity.LEFT | Gravity.TOP;
            params.x = 0;
            params.y = 0;
            //params.token = inParam.token;
            wm.addView(sOverlayView, params);
        }
    }

    private IBinder mIBinder = new IOverlayViewService.Stub() {
        @Override
        public void showOverlay(Bundle param){
            final WindowManager.LayoutParams layoutParams = param.getParcelable(KEY_WINDOWMANAGER_LAYOUTPARAMS);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    show(layoutParams);
                }
            });
        }
    };
}
