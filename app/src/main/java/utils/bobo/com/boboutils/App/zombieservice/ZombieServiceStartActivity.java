package utils.bobo.com.boboutils.App.zombieservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import utils.bobo.com.boboutils.R;

/**
 * Created by huangzb1 on 2018/1/30.
 */

public class ZombieServiceStartActivity extends Activity implements View.OnClickListener {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_two_button);
        this.findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button1: {
                Intent intent = new Intent(this, ZombieServiceFirst.class);
                this.startService(intent);
                break;
            }
        }
    }
}
