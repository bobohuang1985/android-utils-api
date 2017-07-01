package org.join.ws.ui;

import org.join.web.serv.R;
import org.join.ws.Constants.Config;
import org.join.ws.WSApplication;
import org.join.ws.receiver.OnWsListener;
import org.join.ws.receiver.WSReceiver;
import org.join.ws.serv.WebServer;
import org.join.ws.util.CommonUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * @brief 主活动界面
 * @details If you want a totally web server, <a href="https://code.google.com/p/i-jetty/">i-jetty</a> may be your choice.
 * @author join
 */
@SuppressWarnings("deprecation")
public class WSActivity extends WebServActivity implements OnClickListener, OnWsListener {

    static final String TAG = "WSActivity";
    static final boolean DEBUG = false || Config.DEV_MODE;

    private CommonUtil mCommonUtil;

    private ToggleButton toggleBtn;
    private TextView urlText;
    private ImageView qrCodeView;
    private LinearLayout contentLayout;

    private String ipAddr;

    private boolean needResumeServer = false;

    private static final int W_START = 0x0101;
    private static final int W_STOP = 0x0102;
    private static final int W_ERROR = 0x0103;

    private static final int DLG_SERV_USELESS = 0x0201;
    private static final int DLG_PORT_IN_USE = 0x0202;
    private static final int DLG_TEMP_NOT_FOUND = 0x0203;
    private static final int DLG_SCAN_RESULT = 0x0204;

    //private static final int REQ_CAPTURE = 0x0001;
    private String lastResult;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case W_START: {
                setUrlText(ipAddr);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toggleBtn
                        .getLayoutParams();
                params.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                contentLayout.setVisibility(View.VISIBLE);
                break;
            }
            case W_STOP: {
                urlText.setText("");
                qrCodeView.setImageResource(0);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toggleBtn
                        .getLayoutParams();
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                contentLayout.setVisibility(View.GONE);
                break;
            }
            case W_ERROR:
                switch (msg.arg1) {
                case WebServer.ERR_PORT_IN_USE: {
                    showDialog(DLG_PORT_IN_USE);
                    break;
                }
                case WebServer.ERR_TEMP_NOT_FOUND: {
                    showDialog(DLG_TEMP_NOT_FOUND);
                    break;
                }
                case WebServer.ERR_UNEXPECT:
                default:
                    Log.e(TAG, "ERR_UNEXPECT");
                    break;
                }
                doStopClick();
                return;
            }
            toggleBtn.setEnabled(true);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WSApplication.getInstance().initAppDir();
        setContentView(R.layout.main);

        initObjs(savedInstanceState);
        initViews(savedInstanceState);

        WSApplication.getInstance().startWsService();
        WSReceiver.register(this, this);
    }

    private void initObjs(Bundle state) {
        mCommonUtil = CommonUtil.getSingleton();
    }

    private void initViews(Bundle state) {
        toggleBtn = (ToggleButton) findViewById(R.id.toggleBtn);
        toggleBtn.setOnClickListener(this);
        urlText = (TextView) findViewById(R.id.urlText);
        qrCodeView = (ImageView) findViewById(R.id.qrCodeView);
        contentLayout = (LinearLayout) findViewById(R.id.contentLayout);

        if (state != null) {
            ipAddr = state.getString("ipAddr");
            needResumeServer = state.getBoolean("needResumeServer", false);
            boolean isRunning = state.getBoolean("isRunning", false);
            if (isRunning) {
                toggleBtn.setChecked(true);
                setUrlText(ipAddr);
                doBindService();
            }
        }
    }

    private void setUrlText(String ipAddr) {
        String url = "http://" + ipAddr + ":" + Config.PORT + "/";
        urlText.setText(url);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ipAddr", ipAddr);
        outState.putBoolean("needResumeServer", needResumeServer);
        boolean isRunning = webService != null && webService.isRunning();
        outState.putBoolean("isRunning", isRunning);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (DEBUG)
            Log.d(TAG,
                    newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "ORIENTATION_LANDSCAPE"
                            : "ORIENTATION_PORTRAIT");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WSReceiver.unregister(this);
        WSApplication.getInstance().stopWsService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_preferences) {
            toPreferActivity();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        boolean isChecked = toggleBtn.isChecked();
        if (isChecked) {
            if (!isWebServAvailable()) {
                toggleBtn.setChecked(false);
                urlText.setText("");
                showDialog(DLG_SERV_USELESS);
                return;
            }
            doStartClick();
        } else {
            doStopClick();
        }
        needResumeServer = false;
    }

    private void doStartClick() {
        ipAddr = mCommonUtil.getLocalIpAddress();
        if (ipAddr == null) {
            toggleBtn.setChecked(false);
            urlText.setText("");
            toast(getString(R.string.info_net_off));
            return;
        }
        toggleBtn.setChecked(true);
        toggleBtn.setEnabled(false);
        doBindService();
    }

    private void doStopClick() {
        toggleBtn.setChecked(false);
        toggleBtn.setEnabled(false);
        doUnbindService();
        ipAddr = null;
    }

    private boolean isWebServAvailable() {
        return mCommonUtil.isNetworkAvailable() && mCommonUtil.isExternalStorageMounted();
    }

    @Override
    public void onStarted() {
        mHandler.sendEmptyMessage(W_START);
    }

    @Override
    public void onStopped() {
        mHandler.sendEmptyMessage(W_STOP);
    }

    @Override
    public void onError(int code) {
        Message msg = mHandler.obtainMessage(W_ERROR);
        msg.arg1 = code;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onServAvailable() {
        if (needResumeServer) {
            doStartClick();
            needResumeServer = false;
        }
    }

    @Override
    public void onServUnavailable() {
        if (webService != null && webService.isRunning()) {
            doStopClick();
            needResumeServer = true;
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // DialogFragment needs android-support.jar in API-8.
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
        case DLG_SERV_USELESS:
            return createConfirmDialog(android.R.drawable.ic_dialog_info,
                    R.string.tit_serv_useless, R.string.msg_serv_useless, null);
        case DLG_PORT_IN_USE:
            return createConfirmDialog(android.R.drawable.ic_dialog_info, R.string.tit_port_in_use,
                    R.string.msg_port_in_use, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toPreferActivity();
                        }
                    });
        case DLG_TEMP_NOT_FOUND:
            return createConfirmDialog(android.R.drawable.ic_dialog_info,
                    R.string.tit_temp_not_found, R.string.tit_temp_not_found,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toPreferActivity();
                        }
                    });
        case DLG_SCAN_RESULT:
            AlertDialog dialog = createConfirmDialog(android.R.drawable.ic_dialog_info,
                    R.string.tit_scan_result, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            copy2Clipboard(lastResult);
                        }
                    });
            dialog.setMessage(lastResult);
            return dialog;
        }
        return super.onCreateDialog(id, args);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
        case DLG_SCAN_RESULT:
            ((AlertDialog) dialog).setMessage(lastResult);
            break;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    private AlertDialog createConfirmDialog(int iconId, int titleId, int messageId,
            DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (iconId > 0)
            builder.setIcon(iconId);
        if (titleId > 0)
            builder.setTitle(titleId);
        if (messageId > 0)
            builder.setMessage(messageId);
        builder.setPositiveButton(android.R.string.ok, positiveListener);
        return builder.create();
    }

    private void toPreferActivity() {
        try {
            Intent intent = new Intent(this, PreferActivity.class);
            intent.putExtra("isRunning", webService == null ? false : webService.isRunning());
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void toBrowserActivity(String uri) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

    private void copy2Clipboard(String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setText(text);
    }

    private int getDimension() {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int dimension = width < height ? width : height;
        dimension = dimension * 3 / 4;
        return dimension;
    }

}