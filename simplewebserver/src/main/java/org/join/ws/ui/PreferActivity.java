package org.join.ws.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.join.web.serv.R;
import org.join.ws.Constants.Config;
import org.join.ws.WSApplication;
import org.join.ws.ui.widget.FileBrowser;
import org.join.ws.ui.widget.ProgressBarPreference;
import org.join.ws.util.CopyUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

/**
 * @brief 偏好设置
 * @author join
 */
@SuppressWarnings("deprecation")
public class PreferActivity extends PreferenceActivity {

    static final String TAG = "PreferActivity";
    static final boolean DEBUG = false || Config.DEV_MODE;

    public static final String KEY_SERV_PORT = "serv_port";
    public static final String KEY_SERV_ROOT = "serv_root";
    public static final String KEY_SERV_RESET = "serv_reset";
    public static final String KEY_ALLOW_DOWNLOAD = "allow_download";
    public static final String KEY_ALLOW_DELETE = "allow_delete";
    public static final String KEY_ALLOW_UPLOAD = "allow_upload";
    public static final String KEY_MORE_GZIP = "more_gzip";
    public static final String KEY_MORE_CACHE = "more_cache";
    public static final String KEY_MORE_CLEAN = "more_clean";
    public static final String KEY_ABOUT_MORE = "about_more";

    private static final int DLG_EDIT_PORT = 0x0101;
    private static final int DLG_LIST_ROOT = 0x0102;
    private static final int DLG_CONF_RESET = 0x0103;
    private static final int DLG_CONF_CLEAN = 0x0104;

    private Preference currentPreference;

    private String currentRoot = Config.WEBROOT;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefer);

        isRunning = getIntent().getBooleanExtra("isRunning", false);
    }

    /**
     * 保存int或String值
     * @param key 键
     * @param value 值
     */
    public static void save(String key, Object value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WSApplication
                .getApp());
        Editor editor = sp.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        editor.commit();
    }

    /**
     * 恢复某些配置
     */
    public static void restore(String... keys) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WSApplication
                .getApp());
        for (String key : keys) {
            restore(sp, key);
        }
    }

    /**
     * 恢复所有配置
     */
    public static void restoreAll() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WSApplication
                .getApp());
        Config.PORT = sp.getInt(KEY_SERV_PORT, 7766);
        Config.WEBROOT = sp.getString(KEY_SERV_ROOT, "/");
        Config.ALLOW_DOWNLOAD = sp.getBoolean(KEY_ALLOW_DOWNLOAD, true);
        Config.ALLOW_DELETE = sp.getBoolean(KEY_ALLOW_DELETE, true);
        Config.ALLOW_UPLOAD = sp.getBoolean(KEY_ALLOW_UPLOAD, true);
        Config.USE_GZIP = sp.getBoolean(KEY_MORE_GZIP, true);
        Config.USE_FILE_CACHE = sp.getBoolean(KEY_MORE_GZIP, true);
    }

    private static void restore(SharedPreferences sp, String key) {
        if (key.equals(KEY_SERV_PORT)) {
            Config.PORT = sp.getInt(KEY_SERV_PORT, 7766);
        } else if (key.equals(KEY_SERV_ROOT)) {
            Config.WEBROOT = sp.getString(KEY_SERV_ROOT, "/");
        } else if (key.equals(KEY_ALLOW_DOWNLOAD)) {
            Config.ALLOW_DOWNLOAD = sp.getBoolean(KEY_ALLOW_DOWNLOAD, true);
        } else if (key.equals(KEY_ALLOW_DELETE)) {
            Config.ALLOW_DELETE = sp.getBoolean(KEY_ALLOW_DELETE, true);
        } else if (key.equals(KEY_ALLOW_UPLOAD)) {
            Config.ALLOW_UPLOAD = sp.getBoolean(KEY_ALLOW_UPLOAD, true);
        } else if (key.equals(KEY_MORE_GZIP)) {
            Config.USE_GZIP = sp.getBoolean(KEY_MORE_GZIP, true);
        } else if (key.equals(KEY_MORE_CACHE)) {
            Config.USE_FILE_CACHE = sp.getBoolean(KEY_MORE_GZIP, true);
        } else {
            Log.w(TAG, "Ignore key: " + key);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        currentPreference = preference;
        String key = preference.getKey();
        if (preference instanceof CheckBoxPreference) {
            boolean isChecked = ((CheckBoxPreference) preference).isChecked();
            if (DEBUG)
                Log.d(TAG, "isChecked=" + isChecked);
            if (key.equals(KEY_ALLOW_DOWNLOAD)) {
                Config.ALLOW_DOWNLOAD = isChecked;
            } else if (key.equals(KEY_ALLOW_DELETE)) {
                Config.ALLOW_DELETE = isChecked;
            } else if (key.equals(KEY_ALLOW_UPLOAD)) {
                Config.ALLOW_UPLOAD = isChecked;
            } else if (key.equals(KEY_MORE_GZIP)) {
                Config.USE_GZIP = isChecked;
            } else if (key.equals(KEY_MORE_CACHE)) {
                Config.USE_FILE_CACHE = isChecked;
            }
        } else if (key.equals(KEY_SERV_PORT)) {
            showDialog(DLG_EDIT_PORT);
        } else if (key.equals(KEY_SERV_ROOT)) {
            showDialog(DLG_LIST_ROOT);
        } else if (key.equals(KEY_SERV_RESET)) {
            showDialog(DLG_CONF_RESET);
        } else if (key.equals(KEY_MORE_CLEAN)) {
            showDialog(DLG_CONF_CLEAN);
        } else if (key.equals(KEY_ABOUT_MORE)) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.set_about_more_s))));
            } catch (ActivityNotFoundException e) {
            }
        } else {
            return false;
        }
        return true;
    }

    public void doReset() {
        CopyUtil mCopyUtil = new CopyUtil(this);
        mCopyUtil.deleteFile(new File(Config.SERV_ROOT_DIR)); // delete assets
        mCopyUtil.deleteFile(new File(Config.FILE_CACHE_DIR)); // delete caches
        try {
            mCopyUtil.assetsCopy("WebServer", Config.SERV_ROOT_DIR, false); // reset assets
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doClean() {
        try {
            FileUtils.deleteDirectory(new File(Config.FILE_CACHE_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
        case DLG_EDIT_PORT:
            final EditText editText = (EditText) dialog.findViewById(R.id.dlg_edittext);
            String text = Config.PORT + "";
            editText.setText(text);
            editText.setSelection(text.length());
            break;
        case DLG_LIST_ROOT:
            currentRoot = Config.WEBROOT;
            dialog.setTitle(currentRoot);
            final FileBrowser fileBrowser = (FileBrowser) dialog.findViewById(R.id.filebrowser);
            fileBrowser.browse(currentRoot);
            break;
        }
        super.onPrepareDialog(id, dialog);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DLG_EDIT_PORT:
            final EditText editText = new EditText(this);
            editText.setId(R.id.dlg_edittext);
            final OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        String val = editText.getEditableText().toString();
                        Pattern pattern = Pattern.compile("[0-9]{4,5}");
                        if (pattern.matcher(val).matches()) {
                            int port = Integer.parseInt(val);
                            if (port >= 1024 && port <= 65535) {
                                if (isRunning) {
                                    // Restore before server is created.
                                } else {
                                    Config.PORT = port;
                                }
                                save(KEY_SERV_PORT, port);
                                closeDialog(dialog, true);
                                return;
                            }
                        }
                        editText.setError(getString(R.string.info_port_incorrect));
                        closeDialog(dialog, false);
                    } else {
                        editText.setError(null);
                        closeDialog(dialog, true);
                    }
                }
            };
            return new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.set_serv_port_dlg_t).setView(editText)
                    .setPositiveButton(android.R.string.ok, listener)
                    .setNegativeButton(android.R.string.cancel, listener).create();
        case DLG_LIST_ROOT:
            final FileBrowser fileBrowser = (FileBrowser) getLayoutInflater().inflate(
                    R.layout.filebrowser, null);
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert).setTitle(currentRoot)
                    .setView(fileBrowser)
                    .setPositiveButton(android.R.string.ok, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isRunning) {
                                // Restore before server is created.
                            } else {
                                Config.WEBROOT = currentRoot;
                            }
                            save(KEY_SERV_ROOT, currentRoot);
                        }
                    }).setNegativeButton(android.R.string.cancel, null).create();
            fileBrowser.setOnBrowserListener(new FileBrowser.OnBrowserListener() {
                @Override
                public void onFileItemClick(String filename) {
                }

                @Override
                public void onDirItemClick(String path) {
                    currentRoot = path;
                    dialog.setTitle(currentRoot);
                }
            });
            return dialog;
        case DLG_CONF_RESET:
            return createConfirmDialog(android.R.drawable.ic_delete, R.string.set_serv_reset_t,
                    R.string.set_serv_reset_s, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            runAsyncTask(new Runnable() {
                                @Override
                                public void run() {
                                    doReset();
                                }
                            });
                        }
                    });
        case DLG_CONF_CLEAN:
            return createConfirmDialog(android.R.drawable.ic_delete, R.string.set_more_clean_t,
                    R.string.set_more_clean_s, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            runAsyncTask(new Runnable() {
                                @Override
                                public void run() {
                                    doClean();
                                }
                            });
                        }
                    });
        }
        return super.onCreateDialog(id);
    }

    private Dialog createConfirmDialog(int iconId, int titleId, int messageId,
            OnClickListener positiveListener) {
        return new AlertDialog.Builder(this).setIcon(iconId).setTitle(titleId)
                .setMessage(messageId).setPositiveButton(android.R.string.ok, positiveListener)
                .setNegativeButton(android.R.string.cancel, null).create();
    }

    private void closeDialog(DialogInterface dialog, boolean closeEnabled) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, closeEnabled);
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runAsyncTask(final Runnable runnable) {
        new MyAsyncTask(new OnAsyncTaskListener() {

            private ProgressBar mProgressBar;

            @Override
            public void pre() {
                if (currentPreference instanceof ProgressBarPreference) {
                    mProgressBar = ((ProgressBarPreference) currentPreference).getProgressBar();
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void run() {
                runnable.run();
            }

            @Override
            public void post() {
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }).execute();
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private OnAsyncTaskListener mListener;

        public MyAsyncTask(OnAsyncTaskListener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("listener may not be null");
            }
            mListener = listener;
        }

        @Override
        protected void onPreExecute() {
            mListener.pre();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mListener.run();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mListener.post();
        }

    }

    public interface OnAsyncTaskListener {
        void pre();

        void run();

        void post();
    }

}
