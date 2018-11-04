package utils.bobo.com.boboutils.App.audio;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import utils.bobo.com.boboutils.R;
import vavi.sound.pcm.resampling.ssrc.SSRC;


public class AudioRecordActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "AudioRecordActivity";
    private static final int REQUEST_CODE_REQUEST_NECESSARY_PERMISSIONS = 3;
    private static final int RECORD_SAMPLE_RATE_IN_HZ = 8000;
    private static final int RECORD_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORD_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    public static final String[] sRequestPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };
    private AudioRecord mAudioRecord = null;
    private RecordThread mRecordThread;
    private boolean mIsRecording = false;
    private String mCurPCMFileName;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        findViewById(R.id.startRecord).setOnClickListener(this);
        findViewById(R.id.stopRecord).setOnClickListener(this);
        findViewById(R.id.resample).setOnClickListener(this);
        ActivityCompat.requestPermissions(this,
                sRequestPermissions,
                REQUEST_CODE_REQUEST_NECESSARY_PERMISSIONS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startRecord:
                startRecord();
                break;
            case R.id.stopRecord:
                stopLastRecord();
                break;
            case R.id.resample:
                new ResampleThread().start();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_REQUEST_NECESSARY_PERMISSIONS: {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        showRequestPermissionDialog();
                        return;
                    }
                }
            }
        }
    }

    private void showRequestPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("所需权限被拒绝");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
    }

    private AudioRecord initializeRecord() {

        int buffSize = AudioRecord.getMinBufferSize(RECORD_SAMPLE_RATE_IN_HZ, RECORD_CHANNEL_CONFIG, RECORD_AUDIO_FORMAT);
        AudioRecord aRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORD_SAMPLE_RATE_IN_HZ, RECORD_CHANNEL_CONFIG,
                RECORD_AUDIO_FORMAT, buffSize);
        return aRecorder;
    }

    private void startRecord() {
        if (mIsRecording == true) {
            return;
        }
        mIsRecording = true;
        mAudioRecord = initializeRecord();
        if (mAudioRecord == null) {
            Log.e(TAG, "startRecord fail mAudioRecord is null");
            return;
        }
        mAudioRecord.startRecording();
        mRecordThread = new RecordThread();
        mRecordThread.startRecord();

    }

    private void stopLastRecord() {
        if (mIsRecording == false) {
            return;
        }
        mIsRecording = false;
        if (mRecordThread != null) {
            mRecordThread.stopRecord();
        }
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    private class RecordThread extends Thread {
        private boolean mIsCancel = false;
        private byte[] mAudioData;
        private ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

        RecordThread() {
            int buffSize = AudioRecord.getMinBufferSize(RECORD_SAMPLE_RATE_IN_HZ, RECORD_CHANNEL_CONFIG, RECORD_AUDIO_FORMAT);
            mAudioData = new byte[buffSize];
        }

        public void startRecord() {
            this.start();
        }

        public void stopRecord() {
            mIsCancel = false;
            interrupt();
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            int readLen = 0;
            mCurPCMFileName = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + System.currentTimeMillis() + ".wav";
            File file = new File(mCurPCMFileName);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);

                while (!Thread.interrupted()) {
                    readLen = mAudioRecord.read(mAudioData, 0, mAudioData.length);
                    if (readLen > 0) {
                        fos.write(mAudioData, 0, readLen);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();// 关闭写入流
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AudioRecordActivity.this, "录音结束", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class ResampleThread extends Thread {
        @Override
        public void run() {
            File curPCMFile = new File(mCurPCMFileName);
            String resampleFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + curPCMFile.getName() + "_resample" + ".wav";
            File resampleFile = new File(resampleFilePath);
            try {
                FileInputStream fis = new FileInputStream(curPCMFile);
                FileOutputStream fos = new FileOutputStream(resampleFile);
                //同样低采样率转高采样率也是可以的，改下面参数就行。
                new SSRC(fis, fos, 8000, 15000, 2, 2, 1, Integer.MAX_VALUE, 0, 0, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AudioRecordActivity.this, "重采样结束", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
