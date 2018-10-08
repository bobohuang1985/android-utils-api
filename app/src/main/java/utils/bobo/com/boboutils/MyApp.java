package utils.bobo.com.boboutils;

import android.app.Application;
import android.content.Context;

import com.bobo.service.utils.BindWakeupServerServiceHelp;
import com.morgoo.droidplugin.PluginHelper;

import org.join.ws.WSApplication;

/**
 * Created by bobohuang on 2018/3/25.
 */

public class MyApp extends Application {
    @Override

    public void onCreate() {
        super.onCreate();
        WSApplication.getInstance().onCreate(this);

        PluginHelper.getInstance().applicationOnCreate(getBaseContext());
        BindWakeupServerServiceHelp.bindService(this);
    }

    @Override
    protected void attachBaseContext(Context base) {

        PluginHelper.getInstance().applicationAttachBaseContext(base);

        super.attachBaseContext(base);

    }
}
