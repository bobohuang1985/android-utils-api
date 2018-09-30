package utils.bobo.com.boboutils.App.QuickSetting;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.util.Log;

/**
 * Created by bobohuang on 2018/8/15.
 */

@TargetApi(Build.VERSION_CODES.N)
public class QuickSettingService extends TileService {
    private static final String TAG = "QuickSettingService";
    //当用户从Edit栏添加到快速设定中调用
    @Override
    public void onTileAdded() {
        Log.d(TAG, "onTileAdded");
    }
    //当用户从快速设定栏中移除的时候调用
    @Override
    public void onTileRemoved() {
        Log.d(TAG, "onTileRemoved");
    }
    // 点击的时候
    @Override
    public void onClick() {
        Log.d(TAG, "onClick");
    }
    // 打开下拉菜单的时候调用,当快速设置按钮并没有在编辑栏拖到设置栏中不会调用
    //在TleAdded之后会调用一次
    @Override
    public void onStartListening () {
        Log.d(TAG, "onStartListening");
    }
    // 关闭下拉菜单的时候调用,当快速设置按钮并没有在编辑栏拖到设置栏中不会调用
    // 在onTileRemoved移除之前也会调用移除
    @Override
    public void onStopListening () {
        Log.d(TAG, "onStopListening");
    }
}
