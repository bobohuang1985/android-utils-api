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
    TextView mStatusTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_two_button);
        this.findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
    }
    private void sendNotification(){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("BoboUtilsNotificationTest");
        builder.setContentText("BoboUtilsNotificationTest");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(this,NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }
    private Handler mHandler= new Handler();
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button1: {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendNotification();
                    }
                }, 10000);
                break;
            }
            case R.id.button2:{
                NotificationManager notificationManager = (NotificationManager) this
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
                break;
            }
        }
    }
}
