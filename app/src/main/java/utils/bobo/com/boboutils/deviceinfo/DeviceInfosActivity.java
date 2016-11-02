package utils.bobo.com.boboutils.deviceinfo;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bobo.commonutils.deviceinfo.DeviceInfoUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import utils.bobo.com.boboutils.R;

/**
 * Created by bobohuang(bobohuang1985@qq.com)  on 2016/6/24.
 */
public class DeviceInfosActivity extends ListActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUESTCODE_PERMISSION= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE_PERMISSION);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        if(REQUESTCODE_PERMISSION == requestCode){
            for(int result:grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    //finish();
                    //return;
                }
            }
            setListAdapter(new SimpleAdapter(this, getData(),
                    android.R.layout.simple_list_item_2, new String[] { "title","content" },
                    new int[] { android.R.id.text1,android.R.id.text2 }));
            getListView().setTextFilterEnabled(true);
        }
    }
    protected List<Map<String, Object>> getData() {
        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

        addItem(myData, "CPU", DeviceInfoUtils.getCpuMap());
        addItem(myData, getString(R.string.system_info_kernel), DeviceInfoUtils.getKernelMap());
        addItem(myData, getString(R.string.system_info_ids), DeviceInfoUtils.getAndroidIds(this));
        addItem(myData, "OS", DeviceInfoUtils.getAllOsMap());
        //addItem(myData,"Device", DeviceInfoUtils.getTelephonyMap(this));
        addItem(myData, getString(R.string.system_info_screen), DeviceInfoUtils.getScreenMap(this));
        addItem(myData, getString(R.string.system_info_mtd), DeviceInfoUtils.getMTDMap());

        addItem(myData, getString(R.string.system_info_mount), DeviceInfoUtils.getMountMap());

        addItem(myData, getString(R.string.system_info_ram), DeviceInfoUtils.getMemoryMap(this));
        addItem(myData, getString(R.string.system_info_rom), DeviceInfoUtils.getRootStoreMap());
        addItem(myData, getString(R.string.system_info_sdcard), DeviceInfoUtils.getSDCARDStoreMap());
        addItem(myData, "build.prop", DeviceInfoUtils.getAllBuildMap());
        Collections.sort(myData, sDisplayNameComparator);

        return myData;
    }
    private void addItem(List<Map<String, Object>> myData,String title,Map<String, String> content){
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", title);
        Iterator<Map.Entry<String, String>> iterator = content.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, String> prop = iterator.next();
            sb.append(prop.getKey()).append(" = ").append(prop.getValue()).append("\r\n");
        }
        temp.put("content", sb.toString());
        myData.add(temp);
    }
    private final static Comparator<Map<String, Object>> sDisplayNameComparator =
            new Comparator<Map<String, Object>>() {
                private final Collator collator = Collator.getInstance();

                public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                    return collator.compare(map1.get("title"), map2.get("title"));
                }
            };
}
