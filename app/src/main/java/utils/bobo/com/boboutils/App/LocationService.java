package utils.bobo.com.boboutils.App;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class LocationService extends Service {
	public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
	private static final String TAG = "LocationService";
	private LocationManager mLocationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
			Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				Log.d(TAG, "getLastKnownLocation location=" + location);
			}
		}
		mLocationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 1000, 1,
				mLocationListener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
			mLocationManager.removeUpdates(mLocationListener);
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags,int startId) {
		if(intent!=null&&ACTION_STOP_SERVICE.equals(intent.getAction())){
			stopSelf();
		}
		return Service.START_NOT_STICKY;
	}

	private LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.d(TAG, "onLocationChanged location=" + location);
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d(TAG, "onStatusChanged provider=" + provider);
			
		}
		@Override
		public void onProviderEnabled(String provider) {
			Log.d(TAG, "onProviderEnabled provider=" + provider);
			
		}
		@Override
		public void onProviderDisabled(String provider) {
			Log.d(TAG, "onProviderDisabled provider=" + provider);
		}
	};
}
