package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import utils.bobo.com.boboutils.R;

public class LocationActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LocationActivity";
    TextView mStatusTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_location);
        this.findViewById(R.id.startLocationService).setOnClickListener(this);
        findViewById(R.id.stopLocationService).setOnClickListener(this);
    }
    private Handler mHandler= new Handler();
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.startLocationService: {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setClass(LocationActivity.this, LocationService.class);
                        LocationActivity.this.startService(intent);
                    }
                },5000);
                break;
            }
            case R.id.stopLocationService:{
                Intent intent = new Intent(LocationService.ACTION_STOP_SERVICE);
                intent.setClass(this,LocationService.class);
                this.stopService(intent);
                break;
            }
        }
    }
}
