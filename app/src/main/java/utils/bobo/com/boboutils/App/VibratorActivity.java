package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import utils.bobo.com.boboutils.R;

public class VibratorActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "VibratorActivity";
    private Vibrator mVibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_vibrator);
        findViewById(R.id.one_shot).setOnClickListener(this);
        findViewById(R.id.wave_form).setOnClickListener(this);
        findViewById(R.id.one_shot_delay).setOnClickListener(this);
        findViewById(R.id.wave_form_delay).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        mVibrator=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
    }
    private Handler mHandler= new Handler();
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.one_shot: {
                mVibrator.vibrate(10*1000);
                break;
            }
            case R.id.one_shot_delay: {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVibrator.vibrate(100*1000);

                    }
                }, 10*1000);
                break;
            }
            case R.id.wave_form: {
                mVibrator.vibrate(new long[]{100,100,100,1000},-1);
                break;
            }
            case R.id.wave_form_delay: {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVibrator.vibrate(new long[]{100,100,100,1000},-1);
                    }
                }, 10*1000);
                break;
            }
            case R.id.stop: {
                mVibrator.cancel();
                mHandler.removeCallbacksAndMessages(null);
                break;
            }
        }
    }
}
