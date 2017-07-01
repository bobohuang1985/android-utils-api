package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import utils.bobo.com.boboutils.R;

public class ShowWhenLockActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_when_lock);
    }
}
