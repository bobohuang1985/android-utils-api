package utils.bobo.com.boboutils.App.call;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by hgx on 2016/6/13.
 */

public class AudioRecorder8Dot0Service extends Service {
    private MediaRecorder mRecorder;
    private Boolean isRecording = false;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //maintain the relationship between the caller activity and the callee service, currently useless here
        return null;
    }

    @Override
    public void onDestroy() {
        if (isRecording){
            stopRecord();
        }else{
            Toast.makeText(this, "Recording is already stopped",Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRecording){
            startRecord();
        }else {
            Toast.makeText(this, "Recording is already started",Toast.LENGTH_SHORT).show();
        }
        return 1;
    }

    private void startRecord(){
        mRecorder = initializeRecord();
        if (mRecorder != null){
            Toast.makeText(this, "Recording is  started",Toast.LENGTH_SHORT).show();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + "aditi_" + System.currentTimeMillis() + ".aac";
            mRecorder.setOutputFile(path);
            try {
                mRecorder.prepare();
                mRecorder.start();
                Toast.makeText(this, "Recording is start success",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Strart Recording failed",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Strart Recording failed",Toast.LENGTH_SHORT).show();
            return;
        }
        isRecording = true;
    }

    /*
     * Initialize audio record
     *
     * @param
     * @return android.media.AudioRecord
     */
    private MediaRecorder initializeRecord() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        // 设置录音文件的清晰度，这里将 1s音频压缩到 1k大小
        mRecorder.setAudioSamplingRate(8000);
        mRecorder.setAudioEncodingBitRate(8000);
        return mRecorder;
    }

    /*
    * Method to stop and release audio record
    *
    * @param
    * @return void
    */
    private void stopRecord() {
        if (null != mRecorder) {
            isRecording = false;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            Toast.makeText(getApplicationContext(), "Recording is stopped", Toast.LENGTH_LONG).show();
        }
    }
}