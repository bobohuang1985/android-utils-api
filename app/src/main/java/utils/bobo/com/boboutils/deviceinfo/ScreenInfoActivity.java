package utils.bobo.com.boboutils.deviceinfo;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import utils.bobo.com.boboutils.App.LocationService;
import utils.bobo.com.boboutils.R;

public class ScreenInfoActivity extends Activity{
    private static final String TAG = "ScreenInfoActivity";
    TextView mStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_info);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
