package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import utils.bobo.com.boboutils.R;

public class ScreenPinningActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_pinning);
    }
    @Override
    public void onResume(){
        super.onResume();
        this.startLockTask();
    }
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if((keyCode==KeyEvent.KEYCODE_BACK)
                ||(keyCode == KeyEvent.KEYCODE_HOME))
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }
}
