package utils.bobo.com.boboutils;

import android.app.ListActivity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.bobo.com.boboutils.MultiApp.*;

/**
 * Created by huangzb1 on 2016/6/24.
 */
public class DemosActivity extends ListActivity {
    private static final String KEY_META_DATA_PATH = "path";
    private static final String KEY_EXTRA_PATH = "utils.bobo.com.boboutils.PATH";
    private static final String CATEGORY_DEMO_CODE = "bobo.intent.category.DEMO_CODE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DevicePolicyManager manager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (manager.isProfileOwnerApp(getApplicationContext().getPackageName())) {
            Intent intent = new Intent(this, utils.bobo.com.boboutils.MultiApp.MainActivity.class);
            this.startActivity(intent);
            finish();
            return;
        }
        Intent intent = getIntent();
        String path = intent.getStringExtra(KEY_EXTRA_PATH);

        if (path == null) {
            path = "";
        }else{
            setTitle(path);
        }

        setListAdapter(new SimpleAdapter(this, getData(path),
                android.R.layout.simple_list_item_1, new String[] { "title" },
                new int[] { android.R.id.text1 }));
        getListView().setTextFilterEnabled(true);
    }
    protected List<Map<String, Object>> getData(String prefix) {
        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(CATEGORY_DEMO_CODE);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA);

        if (null == list)
            return myData;

        String[] prefixPath;
        String prefixWithSlash = prefix;

        if (prefix.equals("")) {
            prefixPath = null;
        } else {
            prefixPath = prefix.split("/");
            prefixWithSlash = prefix + "/";
        }

        int len = list.size();

        Map<String, Boolean> entries = new HashMap<String, Boolean>();

        for (int i = 0; i < len; i++) {
            ResolveInfo info = list.get(i);
            CharSequence labelSeq = info.loadLabel(pm);
            String metaPath = info.activityInfo.metaData.getString(KEY_META_DATA_PATH);



            if (prefixWithSlash.length() == 0 || metaPath.startsWith(prefixWithSlash)) {

                String[] metaPaths = metaPath.split("/");

                String nextLabel = prefixPath == null ? metaPaths[0] : metaPaths[prefixPath.length];

                if ((prefixPath != null ? prefixPath.length : 0) == metaPaths.length - 1) {
                    String label = labelSeq != null
                            ? labelSeq.toString()
                            : info.activityInfo.name;
                    addItem(myData, label, activityIntent(
                            info.activityInfo.applicationInfo.packageName,
                            info.activityInfo.name));
                } else {
                    if (entries.get(nextLabel) == null) {
                        addItem(myData, nextLabel, browseIntent(prefix.equals("") ? nextLabel : prefix + "/" + nextLabel));
                        entries.put(nextLabel, true);
                    }
                }
            }
        }

        Collections.sort(myData, sDisplayNameComparator);

        return myData;
    }
    private final static Comparator<Map<String, Object>> sDisplayNameComparator =
            new Comparator<Map<String, Object>>() {
                private final Collator collator = Collator.getInstance();

                public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                    return collator.compare(map1.get("title"), map2.get("title"));
                }
            };

    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    protected Intent browseIntent(String path) {
        Intent result = new Intent();
        result.setClass(this, DemosActivity.class);
        result.putExtra(KEY_EXTRA_PATH, path);
        return result;
    }

    protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        temp.put("intent", intent);
        data.add(temp);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, Object> map = (Map<String, Object>)l.getItemAtPosition(position);

        Intent intent = new Intent((Intent) map.get("intent"));
        intent.addCategory(CATEGORY_DEMO_CODE);
        startActivity(intent);
    }
}
