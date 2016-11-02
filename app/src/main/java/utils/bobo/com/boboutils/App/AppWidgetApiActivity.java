package utils.bobo.com.boboutils.App;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import utils.bobo.com.boboutils.R;

public class AppWidgetApiActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "AppWidgetApiActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_widget_api);
        this.findViewById(R.id.btApiTest).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btApiTest:
                testApi();
                break;
        }
    }
    private static final String STATE_FILENAME = "appwidgets.xml";
    private void testApi(){
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        List<AppWidgetProviderInfo> list = manager.getInstalledProviders();
        for(AppWidgetProviderInfo info:list){
            Log.d(TAG,info.toString());
        }
        //File file = new File("data/system/users/0/appwidgets.xml");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("data/system/users/0/appwidgets.xml");
        } catch (FileNotFoundException e) {
            return;
        }

        //List<String> list = new ArrayList<String>();
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new InputStreamReader(fis));
            String line = bufReader.readLine();
            Log.d(TAG,line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bufReader != null) {
                    bufReader.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
