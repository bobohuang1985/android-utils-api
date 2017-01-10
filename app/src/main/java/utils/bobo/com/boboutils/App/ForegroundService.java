package utils.bobo.com.boboutils.App;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import utils.bobo.com.boboutils.R;

public class ForegroundService extends Service {
    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onCreate(){
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags,int startId) {
        Intent notificationIntent = new Intent(this, ForgegroundServiceActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("ForegroundService");
        builder.setContentText("ForegroundService");
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        startForeground(1, builder.build());

        return Service.START_NOT_STICKY;
    }
}
