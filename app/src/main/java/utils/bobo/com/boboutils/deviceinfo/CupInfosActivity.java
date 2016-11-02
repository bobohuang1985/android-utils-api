package utils.bobo.com.boboutils.deviceinfo;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bobo.commonutils.deviceinfo.CpuUtils;
import com.bobo.commonutils.deviceinfo.DeviceInfoUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import utils.bobo.com.boboutils.R;

/**
 * Created by bobohuang(bobohuang1985@qq.com)  on 2016/6/24.
 */
public class CupInfosActivity extends Activity {
    private static final int MSG_UPDATE_CUR_FREQ = 0;
    private TextView mCpuCurFreqTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_infos);
        mCpuCurFreqTextView = (TextView)findViewById(R.id.txCpuCurFreq);
        new Thread(new UpdateUiThread()).start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_CUR_FREQ:
                List<Integer>  freqs = (List<Integer>) msg.obj;
                if(freqs!=null){
                    String freqText = "";
                    for(int i=0; i<freqs.size(); i++){
                        int freq = freqs.get(i);
                        if(freq>0){
                            freqText = freqText+"\r\n"+"CPU" + (i + 1) + ": " + freq/1000+" MHZ";
                        }else{
                            freqText = freqText+"\r\n"+"CPU" + (i + 1) + ": " +  getString(R.string.offline);
                        }
                    }
                    mCpuCurFreqTextView.setText(freqText);
                }
                break;
            }
        }
    };
    class UpdateUiThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    if(CupInfosActivity.this.isDestroyed()){
                        return;
                    }
                    List<Integer> curCpuFreqs = CpuUtils.getCpuCurFreq();
                    if(curCpuFreqs != null && curCpuFreqs.size()>0) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_CUR_FREQ, curCpuFreqs));
                    }
                    Thread.sleep(1000);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
