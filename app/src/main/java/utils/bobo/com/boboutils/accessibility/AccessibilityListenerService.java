package utils.bobo.com.boboutils.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class AccessibilityListenerService extends AccessibilityService {
    private static final String TAG = "AccessibilityListener";

    private static boolean sIsRunning = false;
    public static AccessibilityListenerService sAccessibilityListenerService;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        final int eventType = accessibilityEvent.getEventType();
        Log.d(TAG, "onAccessibilityEvent eventType:" + eventType);
    }

    @Override
    public void onInterrupt() {

    }

    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        sIsRunning = true;
        /*mBgHandlerThread = new HandlerThread("AccessibilityListenerService");
        mBgHandlerThread.start();
        mBgHandler = new Handler(mBgHandlerThread.getLooper());*/
        sAccessibilityListenerService = this;
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        sIsRunning = false;
        sAccessibilityListenerService = null;
    }

    public static boolean isRunning() {
        return sIsRunning;
    }

    public static void requestAccessibilityPermission(Activity context, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        context.startActivityForResult(intent, requestCode);
    }
}
