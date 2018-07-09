package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bobo.commonutils.file.AESHelper;
import com.morgoo.helper.Log;

import utils.bobo.com.boboutils.R;

public class ToastActivity extends Activity implements View.OnClickListener {
    private static final String KEY = "SFDJKFDSJFK*@#";

    private static final String TAG = "ToastActivity";
    private int mIndex = 0;
    AESHelper mAESHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_two_button);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        LayoutInflater layoutInflater;
        mAESHelper = new AESHelper();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1: {
                Toast.makeText(this, "Test Toast "+mIndex, Toast.LENGTH_SHORT).show();
                mIndex++;

                String source = "fdgfiagrj&*"+System.currentTimeMillis();
                String encrypted = mAESHelper.encrypt(KEY,source);
                String decrypt = mAESHelper.decrypt(KEY,encrypted);
                Log.d("huangzb1", "source="+source);
                Log.d("huangzb1", "encrypted="+encrypted);
                Log.d("huangzb1", "decrypt="+decrypt);
                if(source.equals(decrypt)){
                    Log.d("huangzb1", "encrypted success");
                }
                break;
            }
            case R.id.button2: {
                Toast.makeText(this, "Test Toast "+mIndex, Toast.LENGTH_SHORT).show();
                mIndex++;
                break;
            }
        }
    }
}
