package utils.bobo.com.boboutils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.join.ws.ui.WSActivity;

public class SimpleWebServerProxy extends Activity {
    private static final int REQUESTCODE_PERMISSION_STORAGE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUESTCODE_PERMISSION_STORAGE);
        setContentView(R.layout.activity_simple_web_server_proxy);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResulits){
        if(REQUESTCODE_PERMISSION_STORAGE == requestCode){
            for(int result:grantResulits){
                if(result != PackageManager.PERMISSION_GRANTED){
                    finish();
                    return;
                }
            }
            Intent intent = new Intent(this,WSActivity.class);
            this.startActivity(intent);
        }
    }
}
