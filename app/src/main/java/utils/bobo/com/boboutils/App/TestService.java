package utils.bobo.com.boboutils.App;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TestService extends Service {
    public static AsyncTaskRunAlways sAsyncTaskRunAlways;
    private static final String TAG = "TestService";
    public static final String ACTION_START_ASYNC_TASK
            = "utils.bobo.com.boboutils.App.TestService.ACTION_START_ASYNC_TASK";

    public TestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            Log.d(TAG, "onStartCommand action=" + action);
            if (ACTION_START_ASYNC_TASK.equals(action)) {
                if(sAsyncTaskRunAlways == null) {
                    sAsyncTaskRunAlways = new AsyncTaskRunAlways(String.valueOf(System.currentTimeMillis()));
                    sAsyncTaskRunAlways.execute();
                }
            }
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
