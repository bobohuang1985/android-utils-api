package utils.bobo.com.boboutils.App;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by huangzb1 on 2018/3/7.
 */

public class AsyncTaskRunAlways extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "AsyncTaskRunAlways";
    private String mTag;
    public AsyncTaskRunAlways(String tag){
        mTag = tag;
    }
    @Override
    protected Void doInBackground(Void... params) {
        int index = 1;
        while (!isCancelled()){
            Log.d(TAG, mTag+"; doInBackground; index="+index);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            index++;
        }
        return null;
    }
}
