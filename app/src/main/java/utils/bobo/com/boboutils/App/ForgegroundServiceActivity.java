package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import utils.bobo.com.boboutils.R;

public class ForgegroundServiceActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ForgegroundServiceActivity";
    TextView mStatusTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_foreground_service);
        this.findViewById(R.id.startService).setOnClickListener(this);
        findViewById(R.id.stopService).setOnClickListener(this);
    }
    private Handler mHandler= new Handler();
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.startService: {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setClass(ForgegroundServiceActivity.this, ForegroundService.class);
                        ForgegroundServiceActivity.this.startService(intent);
                    }
                },5);
                break;
            }
            case R.id.stopService:{
                Intent intent = new Intent();
                intent.setClass(this,ForegroundService.class);
                this.stopService(intent);
                break;
            }
        }
    }
}
