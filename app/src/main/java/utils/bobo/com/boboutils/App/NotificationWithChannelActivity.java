package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.morgoo.helper.Log;

import utils.bobo.com.boboutils.R;

public class NotificationWithChannelActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "NotificationWithChannelActivity";
    private static final String ACTION_1 = "bobo.com.boboutils.App.ACTION_1";
    private static final String ACTION_REPLY = "bobo.com.boboutils.App.ACTION_REPLY";
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_notificaion_with_channel);
        findViewById(R.id.start_normal).setOnClickListener(this);
        findViewById(R.id.stop_normal).setOnClickListener(this);
        findViewById(R.id.start_direct_reply).setOnClickListener(this);
        findViewById(R.id.stop_direct_reply).setOnClickListener(this);
        IntentFilter filter = new IntentFilter(ACTION_1);
        filter.addAction(ACTION_REPLY);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public  void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_1:
                    Log.d(TAG, "receive ACTION_1");
                    break;
                case ACTION_REPLY:
                    Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                    if (remoteInput != null) {
                        String input = remoteInput.getCharSequence(KEY_TEXT_REPLY).toString();
                        Log.d(TAG, "receive reply = " + input);
                    } else {
                        Log.d(TAG, "receive reply = null");
                    }
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(){
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel screenshotChannel = new NotificationChannel(TAG,
                "NotificationWithChannelActivity", NotificationManager.IMPORTANCE_HIGH); // pop on screen
        screenshotChannel.enableLights(true);
        screenshotChannel.enableVibration(true);
        screenshotChannel.setVibrationPattern(new long[] {100, 200, 300});
        notificationManager.createNotificationChannel(screenshotChannel);

        Notification.Builder builder = new Notification.Builder(this,TAG);
        builder.setContentTitle("BoboUtilsNotificationTest");
        builder.setContentText("BoboUtilsNotificationTest");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(this,NotificationWithChannelActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        builder.setContentIntent(pendingIntent);

        notificationManager.notify(0,builder.build());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendDirectReplyNotification(){
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel screenshotChannel = new NotificationChannel(TAG,
                "NotificationWithChannelActivity", NotificationManager.IMPORTANCE_HIGH); // pop on screen
        screenshotChannel.enableLights(true);
        screenshotChannel.enableVibration(true);
        screenshotChannel.setVibrationPattern(new long[] {100, 200, 300});
        notificationManager.createNotificationChannel(screenshotChannel);

        Notification.Builder builder = new Notification.Builder(this,TAG);
        builder.setContentTitle("BoboUtilsNotificationTest");
        builder.setContentText("BoboUtilsNotificationTest");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(this,NotificationWithChannelActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        builder.setContentIntent(pendingIntent);

        //action 1
        Intent action1Intent = new Intent();
        action1Intent.setAction(ACTION_1);
        action1Intent.setPackage(getPackageName());
        PendingIntent action1PendingIntent =
                PendingIntent.getBroadcast(this, 0, action1Intent, 0);
        builder.addAction(R.drawable.ic_launcher, "Action1",
                action1PendingIntent);


        //direct reply
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("direct reply")
                .build();
        Intent replyIntent = new Intent();
        replyIntent.setAction(ACTION_REPLY);
        replyIntent.setPackage(getPackageName());
        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(this,
                        1,
                        replyIntent,
                        0);
        Notification.Action action =
                new Notification.Action.Builder(R.drawable.ic_launcher,
                        "DirectReply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();
        builder.addAction(action);
        notificationManager.notify(1,builder.build());
    }
    private Handler mHandler= new Handler();
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_normal: {
                mHandler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        sendNotification();
                    }
                }, 0);
                break;
            }
            case R.id.stop_normal:{
                NotificationManager notificationManager = (NotificationManager) this
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
                break;
            }
            case R.id.start_direct_reply: {
                mHandler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        sendDirectReplyNotification();
                    }
                }, 0);
                break;
            }
            case R.id.stop_direct_reply:{
                NotificationManager notificationManager = (NotificationManager) this
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                break;
            }
        }
    }
}
