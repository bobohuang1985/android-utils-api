package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bobo.commonutils.file.AESHelper;
import com.morgoo.helper.Log;

import utils.bobo.com.boboutils.App.overlay.OverlayViewServiceHelp;
import utils.bobo.com.boboutils.R;

public class SystemOverlayActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "SystemOverlayActivity";
    private static View sOverlayView;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_two_button);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1: {
                show();
                break;
            }
            case R.id.button2: {
                OverlayViewServiceHelp.bindService(this);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        OverlayViewServiceHelp.showOverlay(SystemOverlayActivity.this);
                    }
                },2000);
                break;
            }
        }
    }

    private void show() {
        sOverlayView = this.getLayoutInflater().inflate(R.layout.system_overlay, null);
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
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();

            params.type = WindowManager.LayoutParams.TYPE_PHONE;//WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            params.flags = 0;//WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            params.format = PixelFormat.TRANSPARENT;

            params.gravity = Gravity.LEFT | Gravity.TOP;
            params.x = 0;
            params.y = 0;
            wm.addView(sOverlayView, params);
        }
    }
}
