package utils.bobo.com.boboutils.App.call;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import utils.bobo.com.boboutils.R;

public class CallAudioRecord8Dot0Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "CallAudioRecordActivity";
    private BroadcastReceiver mBroadcastReceiver = new PhoneCall8Dot0Receiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_two_button);
        this.findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction("android.intent.action.PHONE_STATE");
        this.registerReceiver(mBroadcastReceiver,filter);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(mBroadcastReceiver);
    }
    private Handler mHandler= new Handler();
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button1: {

                break;
            }
            case R.id.button2:{
                break;
            }
        }
    }
}
