package com.test.jni;

import android.util.Log;

/**
 * Created by hesong on 16/11/17.
 */

public class WebrtcProcessor {

    static {
        try {
            System.loadLibrary("webrtc");
        } catch (UnsatisfiedLinkError e) {
            Log.e("TAG", "Couldn't load lib:   - " + e.getMessage());
        }

    }

    /**
     * 处理降噪
     * @param data
     */
    public void processNoise(byte[] data){

        if(data == null) return;

        int newDataLength = data.length/2;
        if(data.length % 2 == 1){
            newDataLength += 1;
        }

        short[] newData = new short[newDataLength];

        for(int i=0; i<newDataLength; i++){
            byte low = 0;
            byte high = 0;

            if(2*i < data.length){
                low = data[2*i];
            }
            if((2*i+1) < data.length){
                high = data[2*i+1];
            }

            newData[i] = (short) (((high << 8) & 0xff00) | (low & 0x00ff));
        }

        processNoise(newData);

        for(int i=0; i<newDataLength; i++){
            if(2*i < data.length){
                data[2*i] = (byte) (newData[i] & 0xff);
            }
            if((2*i+1) < data.length){
                data[2*i+1] = (byte) ((newData[i] >> 8) & 0xff);
            }
        }

    }

    /**
     * 初始化降噪设置
     * @param sampleRate 采样率
     * @return 是否初始化成功
     */
    public native boolean init(int sampleRate);

    /**
     * 处理降噪
     * @param data
     * @return
     */
    public native boolean processNoise(short[] data);

    public native void release();

}
