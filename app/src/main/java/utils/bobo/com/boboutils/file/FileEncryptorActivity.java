package utils.bobo.com.boboutils.file;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import utils.bobo.com.boboutils.R;

public class FileEncryptorActivity extends FragmentActivity implements OnRequestPermissionsResultCallback {
    private static final int REQUESTCODE_PERMISSION_STORAGE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        initContent();
    }
    private void initContent() {
        Fragment fragment = CommonFileListFragment.newInstance();
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //ft.add(R.id.content, mStatusListFragment);
        ft.commit();
    }
}
