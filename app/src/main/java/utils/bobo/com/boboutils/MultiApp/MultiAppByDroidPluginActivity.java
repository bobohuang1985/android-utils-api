package utils.bobo.com.boboutils.MultiApp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import utils.bobo.com.boboutils.App.zombieservice.ZombieServiceFirst;
import utils.bobo.com.boboutils.R;

/**
 * Created by huangzb1 on 2018/3/25.
 */

public class MultiAppByDroidPluginActivity extends Activity implements View.OnClickListener{
    private static final int REQUESTCODE_PERMISSION_STORAGE = 0;
    private Handler mHandler;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_two_button);
        Button button1 = (Button)findViewById(R.id.button1);
        button1.setText("安装apk");
        button1.setOnClickListener(this);

        Button button2 = (Button)findViewById(R.id.button2);
        button2.setText("启动apk");
        button2.setOnClickListener(this);

        mHandler = new Handler();
        this.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE_PERMISSION_STORAGE);
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
        }
    }
    public void installApk(){
        new Thread(){
            @Override
            public void run(){

                AssetManager am = getAssets();
                String filepath = Environment.getExternalStorageDirectory()+"/weixin.apk";
                File file = new File(filepath);
                if(file.exists()){
                    file.delete();
                }
                InputStream in = null;
                try {
                    in = am.open("weixin_1280.apk");
                    FileOutputStream fout = new FileOutputStream(new File(filepath));
                    byte bt[] = new byte[1024*1024];
                    int c;
                    while ((c = in.read(bt)) > 0) {
                        fout.write(bt, 0, c); // 将内容写到新文件当中
                    }
                    in.close();
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    PluginManager.getInstance().deletePackage("com.tencent.mm",0);
                    PluginManager.getInstance().installPackage(filepath, 0);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MultiAppByDroidPluginActivity.this,"插件安装成功!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MultiAppByDroidPluginActivity.this,"插件安装失败!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }
    public void lanchPlugin(){
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage("com.tencent.mm");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button1: {
                installApk();
                break;
            }
            case R.id.button2: {
                lanchPlugin();
                break;
            }
        }
    }
}
