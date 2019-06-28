package utils.bobo.com.boboutils.service;

import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

import com.morgoo.helper.Log;

/**
 * Created by bobohuang on 2019/6/20.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MyTileService extends TileService {
    private static final String TAG = "MyTileService";
    private boolean mIsEnabled = false;
    //Called when the user adds this tile to Quick Settings.
    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Log.d(TAG, "onTileAdded");
    }

    //Called when the user removes this tile from Quick Settings.
    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Log.d(TAG, "onTileRemoved");
    }

    //Called when this tile moves into a listening state.
    @Override
    public void onStartListening() {
        super.onStartListening();
        Log.d(TAG, "onStartListening");
    }

    //Called when this tile moves out of the listening state.
    @Override
    public void onStopListening() {
        super.onStopListening();
        Log.d(TAG, "onStopListening");
    }

    //Called when the user clicks on this tile.
    @Override
    public void onClick() {
        super.onClick();
        Log.d(TAG, "onClick");
        mIsEnabled = !mIsEnabled;
        getQsTile().setState(mIsEnabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }
}
