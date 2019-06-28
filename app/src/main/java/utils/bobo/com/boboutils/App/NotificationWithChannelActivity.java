package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.TextView;

import utils.bobo.com.boboutils.R;

public class NotificationWithChannelActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "NotificationWithChannelActivity";
    TextView mStatusTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_two_button);
        this.findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
    }
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
    private Handler mHandler= new Handler();
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button1: {
                mHandler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        sendNotification();
                    }
                }, 0);
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
