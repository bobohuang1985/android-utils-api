package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import utils.bobo.com.boboutils.R;

public class TempActivity extends Activity implements View.OnClickListener {
    private ToggleButton mToggleButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_temp);
        this.findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        mToggleButton = (ToggleButton) findViewById(R.id.tbutton_record);
        mToggleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1: {
                Intent intent = new Intent(this, TestService.class);
                intent.setAction(TestService.ACTION_START_ASYNC_TASK);
                this.startService(intent);
                break;
            }
            case R.id.button2: {
                Intent intent = new Intent(this, TestService.class);
                this.stopService(intent);
                if(TestService.sAsyncTaskRunAlways != null){
                    TestService.sAsyncTaskRunAlways.cancel(true);
                    TestService.sAsyncTaskRunAlways = null;
                }
                break;
            }
            case R.id.tbutton_record: {
                boolean isChecked = mToggleButton.isChecked();
                if (isChecked) {
                    v.announceForAccessibility("recording started");
                    mToggleButton.setTextOff("reRecord");
                } else {
                    v.announceForAccessibility("recording stoped");
                }
                break;
            }
        }
    }
}
