package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import utils.bobo.com.boboutils.R;

public class TestServiceActivity extends Activity implements View.OnClickListener {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_two_button);
        this.findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
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
        }
    }
}
