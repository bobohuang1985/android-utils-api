package utils.bobo.com.boboutils.accessibility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import utils.bobo.com.boboutils.App.TestService;
import utils.bobo.com.boboutils.R;

public class AccessibilityServiceActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "AccServiceActivity";
    private HandlerThread mHandlerThread = new HandlerThread(TAG);
    private Handler mWorkHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_two_button);
        ((Button) findViewById(R.id.button1)).setText("延迟dump窗口信息");
        this.findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setVisibility(View.GONE);
        mHandlerThread.start();
        mWorkHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandlerThread.quit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1: {
                dumpWindowInfo();
                break;
            }
        }
    }

    private void dumpWindowInfo() {
        if (AccessibilityListenerService.sAccessibilityListenerService == null) {
            AccessibilityListenerService.requestAccessibilityPermission(this, 0);
            return;
        }
        mWorkHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AccessibilityListenerService.sAccessibilityListenerService == null) {
                    Log.d(TAG, "AccessibilityListenerService is null");
                    return;
                }
                AccessibilityNodeInfo rootNodeInfo = AccessibilityListenerService.sAccessibilityListenerService.getRootInActiveWindow();
                Log.d(TAG, "dumpWindowInfo start");
                Rect rect = new Rect();
                rootNodeInfo.getBoundsInScreen(rect);
                Log.d(TAG, "dumpABViewInfo rootNodeInfo rect" + rect);
                Log.d(TAG, "dumpABViewInfo rootNodeInfo text" + rootNodeInfo.getText());
                dumpABViewInfo(rootNodeInfo, 1);
                Log.d(TAG, "dumpWindowInfo end");
                rootNodeInfo.refresh();
                rootNodeInfo.recycle();
            }
        }, 5000);

    }

    private void dumpABViewInfo(AccessibilityNodeInfo accessibilityNodeInfo, int depth) {
        String depthString = "";
        for (int i = 0; i < depth; i++) {
            depthString += "#";
        }
        if (accessibilityNodeInfo == null) {
            Log.d(TAG, depthString + "dumpABViewInfo break");
            return;
        }
        Log.d(TAG, depthString + "dumpABViewInfo getChildCount=" + accessibilityNodeInfo.getChildCount());
        for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childView = accessibilityNodeInfo.getChild(i);
            Log.d(TAG, depthString + "dumpABViewInfo getChild i=" + i);
            if (childView == null) {
                Log.d(TAG, depthString + "dumpABViewInfo childView == null");
                continue;
            }
            if (!childView.isVisibleToUser()) {
                Log.d(TAG, depthString + "dumpABViewInfo childView is not visible");
                continue;
            }

            Log.d(TAG, depthString + "dumpABViewInfo childView = " + childView.getClassName());
            Rect rect = new Rect();
            childView.getBoundsInScreen(rect);
            Log.d(TAG, depthString + "dumpABViewInfo childView rect" + rect);
            Log.d(TAG, depthString + "dumpABViewInfo childView text" + childView.getText());
            dumpABViewInfo(childView, depth + 1);
            childView.recycle();
        }
    }
}
