package utils.bobo.com.boboutils.jobscheduler;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import utils.bobo.com.boboutils.R;


public class JobSchedulerAndThreadAndAsyncTask extends Activity implements View.OnClickListener {
    private static final String TAG = "JobSchThreadAsyncTask";
    public static final int MSG_THREAD_RUNNING = 0;
    public static final int MSG_ASYNCTASK_RUNNING = 1;
    public static final int MSG_SERVICE_OBJ = 2;
    public static final int MSG_SCHEDULE_JOB = 3;

    /** Service object to interact scheduled jobs. */
    TestJobService mTestService;
    private TextView mTvJobSchedulerStatus,mTvThreadStatus,mTvAsycTaskStatus;
    private Button mBtvJobSchedulerStatus,mBtThreadStatus,mBtAsycTaskStatus;
    private int mTvJobSchedulerStatusCount,mTvThreadStatusCount,mTvAsycTaskStatusCount;
    private boolean mIsJobSchedulerRunning = false;
    private ComponentName mJobSchedulerServiceComponent;
    private MyAsyncTask mMyAsyncTask;
    private MyThread  mMyThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobscheduler_thread_asynctask);
        mJobSchedulerServiceComponent = new ComponentName(this, TestJobService.class);

        Intent startServiceIntent = new Intent(this, TestJobService.class);
        startServiceIntent.putExtra("messenger", new Messenger(mHandler));
        startService(startServiceIntent);

        mTvJobSchedulerStatus = (TextView)findViewById(R.id.tvJobSchedulerStatus);
        mTvThreadStatus = (TextView)findViewById(R.id.tvThreadStatus);
        mTvAsycTaskStatus = (TextView)findViewById(R.id.tvAsycTaskStatus);
        (mBtvJobSchedulerStatus = (Button) findViewById(R.id.btJobScheduler)).setOnClickListener(this);
        (mBtThreadStatus = (Button) findViewById(R.id.btThread)).setOnClickListener(this);
        (mBtAsycTaskStatus = (Button) findViewById(R.id.btAsycTask)).setOnClickListener(this);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mIsJobSchedulerRunning = false;
        cancelMyAsyncTask();
        cancelMyThread();
        if (!ensureTestService()) {
            return;
        }
        mTestService.callJobFinished();
    }
    public void onReceivedStartJob(JobParameters params) {
        Log.d(TAG,"onReceivedStartJob");
        Log.d(TAG,"scheduleJob is running");
        mTvJobSchedulerStatusCount++;
        mTvJobSchedulerStatus.setText("JobService Counting "+mTvJobSchedulerStatusCount);
        if (!ensureTestService()) {
            return;
        }
        mTestService.callJobFinished();
        if(mIsJobSchedulerRunning){
            mHandler.sendEmptyMessageDelayed(MSG_SCHEDULE_JOB,1000);
        }
    }
    public void onReceivedStopJob() {
        Log.d(TAG,"onReceivedStopJob");
    }
    Handler mHandler = new Handler(/* default looper */) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SERVICE_OBJ:
                    mTestService = (TestJobService) msg.obj;
                    mTestService.setUiCallback(JobSchedulerAndThreadAndAsyncTask.this);
                    break;
                case MSG_THREAD_RUNNING:
                    mTvThreadStatusCount++;
                    mTvThreadStatus.setText("Thread Counting "+mTvThreadStatusCount);
                    break;
                case MSG_ASYNCTASK_RUNNING:
                    mTvAsycTaskStatusCount++;
                    mTvAsycTaskStatus.setText("AsyncTask Counting "+mTvAsycTaskStatusCount);
                    break;
                case MSG_SCHEDULE_JOB:
                    scheduleJob();
                    break;
            }
        }
    };
    private boolean ensureTestService() {
        if (mTestService == null) {
            Toast.makeText(this, "Service null, never got callback?",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob() {
        if (!ensureTestService()) {
            return;
        }

        JobInfo.Builder builder = new JobInfo.Builder(mTvJobSchedulerStatusCount+1, mJobSchedulerServiceComponent);

        //String delay = mDelayEditText.getText().toString();
        //if (delay != null && !TextUtils.isEmpty(delay)) {
         //   builder.setMinimumLatency(Long.valueOf(delay) * 1000);
       // }
       // String deadline = mDeadlineEditText.getText().toString();
        //if (deadline != null && !TextUtils.isEmpty(deadline)) {
        //    builder.setOverrideDeadline(Long.valueOf(deadline) * 1000);
       // }
      /*  boolean requiresUnmetered = mWiFiConnectivityRadioButton.isChecked();
        boolean requiresAnyConnectivity = mAnyConnectivityRadioButton.isChecked();
        if (requiresUnmetered) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        } else if (requiresAnyConnectivity) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        }
        builder.setRequiresDeviceIdle(mRequiresIdleCheckbox.isChecked());
        builder.setRequiresCharging(mRequiresChargingCheckBox.isChecked());*/
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        mTestService.scheduleJob(builder.build());

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btJobScheduler:
                mIsJobSchedulerRunning = !mIsJobSchedulerRunning;
                if(mIsJobSchedulerRunning){
                    scheduleJob();
                    mBtvJobSchedulerStatus.setText("Stop JobScheduler");
                }else{
                    mBtvJobSchedulerStatus.setText("Start JobScheduler");
                }
                break;
            case R.id.btThread:
                if(mMyThread==null){
                    mMyThread = new MyThread();
                    mMyThread.start();
                    mBtThreadStatus.setText("Stop Thread");
                }else{
                    cancelMyThread();
                    mBtThreadStatus.setText("Start Thread");
                }
                break;
            case R.id.btAsycTask:
                if(mMyAsyncTask==null){
                    mMyAsyncTask = new MyAsyncTask();
                    mMyAsyncTask.execute();
                    mBtAsycTaskStatus.setText("Stop Async Task");
                }else{
                    cancelMyAsyncTask();
                    mBtAsycTaskStatus.setText("Start Async Task");
                }
                break;
        }
    }
    private void cancelMyAsyncTask(){
        if(mMyAsyncTask!=null){
            mMyAsyncTask.cancel(true);
            mMyAsyncTask = null;
        }
    }
    private void cancelMyThread(){
        if(mMyThread!=null){
            mMyThread.interrupt();
            mMyThread = null;
        }
    }
    private class MyAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            while(!this.isCancelled()){
                mHandler.sendEmptyMessage(MSG_ASYNCTASK_RUNNING);
                Log.d(TAG,"MyAsyncTask is running");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    private class MyThread extends Thread{
        @Override
        public void run(){
            while(!Thread.interrupted()){
                mHandler.sendEmptyMessage(MSG_THREAD_RUNNING);
                Log.d(TAG,"MyThread is running");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
