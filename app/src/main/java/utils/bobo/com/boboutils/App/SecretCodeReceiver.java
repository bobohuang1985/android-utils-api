package utils.bobo.com.boboutils.App;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import utils.bobo.com.boboutils.DemosActivity;

public class SecretCodeReceiver extends BroadcastReceiver {
    public SecretCodeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent activityIntent = new Intent(context, DemosActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }
}
