package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import utils.bobo.com.boboutils.R;

public class WakeLockGetAndReleaseActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "WakeLockGetAndReleaseActivity";
    TextView mStatusTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_wakelock_get_release);
        this.findViewById(R.id.getWakeLock).setOnClickListener(this);
        findViewById(R.id.releaseWakeLock).setOnClickListener(this);
        mStatusTextView = (TextView)findViewById(R.id.wakeLockStatus);
        if(WakeLockTestApi.getInstance(this.getApplication()).isWakeLockExist()){
            mStatusTextView.setText("WakeLock acquired");
        }else{
            mStatusTextView.setText("WakeLock not acquire");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.getWakeLock:
                if(WakeLockTestApi.getInstance(this.getApplication()).getAWakeLock()){
                    mStatusTextView.setText("WakeLock acquired");
                }else{
                    mStatusTextView.setText("WakeLock acquire fail");
                }
                break;
            case R.id.releaseWakeLock:
                if(WakeLockTestApi.getInstance(this.getApplication()).releaseWakeLock()){
                    mStatusTextView.setText("WakeLock released");
                }else{
                    mStatusTextView.setText("WakeLock release fail");
                }
                break;
        }
    }
}
