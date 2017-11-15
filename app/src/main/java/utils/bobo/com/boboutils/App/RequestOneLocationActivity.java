package utils.bobo.com.boboutils.App;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.TimeUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import utils.bobo.com.boboutils.R;

public class RequestOneLocationActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RequestOneLocationActivity";
    private TextView mResultTextView;
    private boolean mIsRequesting = false;
    private LocationManager mLocationManager;
    private Handler mHandler = new Handler();
    private long mStartRequestTime = 0;
    private int mMyUid;
    private BatterySipper mStartBatterySipper;
    private BatterySipper mLastBatterySipper;
    private int mGetLocationCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_request_one_location);
        Button buttonRequest = (Button) findViewById(R.id.buttonRequest);
        buttonRequest.setText("Request One Permission");
        buttonRequest.setOnClickListener(this);
        mResultTextView = (TextView) findViewById(R.id.result);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        PackageManager pm = getPackageManager();
        try {
            mMyUid = pm.getPackageInfo(this.getPackageName(), 0).applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mStartBatterySipper = getBatteryCost();
        mLastBatterySipper = mStartBatterySipper;
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mLocationManager.removeUpdates(mLocationListener);
    }
    private void appendResult(String message) {
        mResultTextView.setText(mResultTextView.getText() + "\r\n" + message);
    }

    private String getLoaclTimeString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
        return t1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRequest: {
                if (mIsRequesting) {
                    Toast.makeText(this, "Requesting already", Toast.LENGTH_LONG).show();
                    return;
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Need ACCESS_FINE_LOCATION permission", Toast.LENGTH_LONG).show();
                    return;
                }
                mIsRequesting = true;
                mStartRequestTime = System.currentTimeMillis();
                appendResult(" ");
                appendResult("##########");
                appendResult("Start Request at " + getLoaclTimeString(mStartRequestTime));
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
                break;
            }
        }
    }

    private BatterySipper getBatteryCost() {
        BatteryStatsHelper statsHelper = new BatteryStatsHelper(getApplicationContext(), true);
        statsHelper.create((Bundle) null);
        UserManager mUm = (UserManager) getApplicationContext().getSystemService(Context.USER_SERVICE);
        statsHelper.refreshStats(BatteryStats.STATS_SINCE_CHARGED, mUm.getUserProfiles());
        long statsPeriod = statsHelper.getStatsPeriod();
        List<BatterySipper> usageList = statsHelper.getUsageList();
        if (usageList != null) {
            for (int i = 0; i < usageList.size(); i++) {
                BatterySipper sip = usageList.get(i);
                if (sip.getUid() == mMyUid) {
                    return sip;
                }
            }
        }
        return new BatterySipper(BatterySipper.DrainType.APP, null, 0);
    }

    private void calculateBatteryCost() {
        BatterySipper curBatterySipper = getBatteryCost();
        appendResult("1: cost gpsPowerMah: " + (curBatterySipper.gpsPowerMah - mLastBatterySipper.gpsPowerMah));
        appendResult("1: cost gpsTimeMs: " + (curBatterySipper.gpsTimeMs - mLastBatterySipper.gpsTimeMs));
        appendResult("2: total get GPS count:" + mGetLocationCount);
        appendResult("2: total cost gpsPowerMah: " + (curBatterySipper.gpsPowerMah - mStartBatterySipper.gpsPowerMah));
        appendResult("2: total cost gpsTimeMs: " + (curBatterySipper.gpsTimeMs - mStartBatterySipper.gpsTimeMs));
        appendResult("3: average cost gpsPowerMah: "
                + (curBatterySipper.gpsPowerMah - mStartBatterySipper.gpsPowerMah) / (double) mGetLocationCount);
        appendResult("3: average cost gpsTimeMs: "
                + (curBatterySipper.gpsTimeMs - mStartBatterySipper.gpsTimeMs) / (long) mGetLocationCount);
        mLastBatterySipper = curBatterySipper;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) { // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            // log it when the location changes
            if (location != null) {
                appendResult("Location changed : Lat: "
                        + location.getLatitude() + " Lng: "
                        + location.getLongitude());
                long curTime = System.currentTimeMillis();
                appendResult("Get Request at " + getLoaclTimeString(curTime));
                appendResult("Cost " + (curTime - mStartRequestTime) + " Millisecond");
                mLocationManager.removeUpdates(mLocationListener);
                mIsRequesting = false;
                mGetLocationCount++;
                calculateBatteryCost();
            }
        }

        public void onProviderDisabled(String provider) {
            // Provider被disable时触发此函数，比如GPS被关闭
        }

        public void onProviderEnabled(String provider) {
            // Provider被enable时触发此函数，比如GPS被打开
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Provider的转态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        }
    };
}
