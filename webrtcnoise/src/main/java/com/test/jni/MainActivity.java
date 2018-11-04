package com.test.jni;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends Activity implements View.OnClickListener{

    SeekBar skbVolume;//调节音量
    boolean isProcessing = true;//是否录放的标记
    boolean isRecording = false;//是否录放的标记

    static final int frequency = 8000;
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int recBufSize, playBufSize;
    AudioRecord audioRecord;
    AudioTrack audioTrack;

    private String outFilePath;
    private OutputStream mOutputStream;
    private static final int FLAG_RECORD_START = 1;
    private static final int FLAG_RECORDING = 2;
    private static final int FLAG_RECORD_FINISH = 3;

    private WebrtcProcessor mProcessor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webrtc_noise_main);
        recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        Log.e("", "recBufSize:" + recBufSize);
        playBufSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recBufSize);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, playBufSize, AudioTrack.MODE_STREAM);

        findViewById(R.id.btnRecord).setOnClickListener(this);
        findViewById(R.id.btnStop).setOnClickListener(this);

        skbVolume = (SeekBar) this.findViewById(R.id.skbVolume);
        skbVolume.setMax(100);//音量调节的极限
        skbVolume.setProgress(50);//设置seekbar的位置值
        audioTrack.setStereoVolume(0.7f, 0.7f);//设置当前音量大小
        skbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float vol = (float) (seekBar.getProgress()) / (float) (seekBar.getMax());
                audioTrack.setStereoVolume(vol, vol);//设置音量
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
        ((CheckBox) findViewById(R.id.cb_ap)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton view, boolean checked) {
                isProcessing = checked;
            }
        });

        initProccesor();
    }

    @Override
    protected void onDestroy() {

        releaseProcessor();

        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRecord) {
            isRecording = true;
            new RecordPlayThread().start();
        } else if (v.getId() == R.id.btnStop) {
            isRecording = false;
        }
    }

    class RecordPlayThread extends Thread {
        public void run() {
            try {

                short[] buffer = new short[recBufSize/2];
                audioRecord.startRecording();//开始录制
                audioTrack.play();//开始播放

                saveToFile(FLAG_RECORD_START, null);

                while (isRecording) {
                    //setp 1 从MIC保存数据到缓冲区
                    int bufferReadResult = audioRecord.read(buffer, 0, recBufSize/2);
                    short[] tmpBuf_src = new short[bufferReadResult];
                    System.arraycopy(buffer, 0, tmpBuf_src, 0, bufferReadResult);

                    //setp 2 进行处理
                    if (isProcessing) {

                        processData(tmpBuf_src);

                    } else {
                    }
                    //写入数据即播放
                    audioTrack.write(tmpBuf_src, 0, tmpBuf_src.length);

                    //saveToFile(FLAG_RECORDING, tmpBuf_src);

                }

                saveToFile(FLAG_RECORD_FINISH, null);

                audioTrack.stop();
                audioRecord.stop();
            } catch (Exception t) {
                t.printStackTrace();
            }
        }
    };

    class RecordPlayThread2 extends Thread {
        public void run() {
            try {

                byte[] buffer = new byte[recBufSize];
                audioRecord.startRecording();//开始录制
                audioTrack.play();//开始播放

                saveToFile(FLAG_RECORD_START, null);

                while (isRecording) {
                    //setp 1 从MIC保存数据到缓冲区
                    int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);
                    byte[] tmpBuf_src = new byte[bufferReadResult];
                    System.arraycopy(buffer, 0, tmpBuf_src, 0, bufferReadResult);

                    //setp 2 进行处理
                    if (isProcessing) {

                        processData(tmpBuf_src);

                    } else {
                    }
                    //写入数据即播放
                    audioTrack.write(tmpBuf_src, 0, tmpBuf_src.length);

                    //saveToFile(FLAG_RECORDING, tmpBuf_src);

                }

                saveToFile(FLAG_RECORD_FINISH, null);

                audioTrack.stop();
                audioRecord.stop();
            } catch (Exception t) {
                t.printStackTrace();
            }
        }
    };

    private void saveToFile(int flag, byte[] data){

        if(true) return;

        switch (flag){
            case FLAG_RECORD_START:

                String pcmPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record/record.pcm";
                try {
                    mOutputStream = new FileOutputStream(pcmPath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case FLAG_RECORDING:

                if(mOutputStream != null){
                    try {
                        mOutputStream.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case FLAG_RECORD_FINISH:

                try {
                    if(mOutputStream != null){
                        mOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                pcmPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record/record.pcm";
                String wavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record/record.wav";

                AudioEncodeUtil.convertPcm2Wav(pcmPath, wavePath);

                break;
        }

    }

    private void initProccesor(){
        mProcessor = new WebrtcProcessor();
        mProcessor.init(frequency);
    }

    private void releaseProcessor(){
        if(mProcessor != null){
            mProcessor.release();
        }
    }

    private void processData(byte[] data){
        if(mProcessor != null){
            mProcessor.processNoise(data);
        }
    }

    private void processData(short[] data){
        if(mProcessor != null){
            mProcessor.processNoise(data);
        }
    }



}
