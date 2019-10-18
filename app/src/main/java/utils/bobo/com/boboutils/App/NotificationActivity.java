package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import utils.bobo.com.boboutils.R;

public class NotificationActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "NotificationActivity";
    private static final int NORMAL_NOTIFICATION_ID = 100;
    private static final int HEADSUP_NOTIFICATION_ID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        this.findViewById(R.id.btNormal).setOnClickListener(this);
        findViewById(R.id.btHeadsup).setOnClickListener(this);
    }
    private void sendNormalNotification(){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("BoboUtilsNotificationTest");
        builder.setContentText("BoboUtilsNotificationTest");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(this,NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NORMAL_NOTIFICATION_ID,builder.build());
    }
    private void sendHeadsUpNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        Intent intent = new Intent(this,NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        builder.setContentTitle("悬挂式通知");
        builder.setFullScreenIntent(pendingIntent, true);
        notificationManager.notify(HEADSUP_NOTIFICATION_ID, builder.build());
        new Exception().printStackTrace();
    }

    private Handler mHandler= new Handler();
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btNormal: {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendNormalNotification();
                    }
                }, 0);
                break;
            }
            case R.id.btHeadsup:{
                sendHeadsUpNotification();
                break;
            }
        }
    }
}
