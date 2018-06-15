package utils.bobo.com.boboutils.App.call;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by hgx on 2016/6/13.
 */

public class AudioRecorderService extends Service {
    private static int RECORD_RATE = 0;
    private static int RECORD_BPP = 32;
    private static int RECORD_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static int RECORD_ENCODER = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord audioRecorder = null;
    private Thread recordT = null;
    private Boolean isRecording = false;
    private int bufferEle = 1024, bytesPerEle = 2;// want to play 2048 (2K) since 2 bytes we use only 1024 2 bytes in 16bit format
    private static int[] recordRate ={44100 , 22050 , 11025 , 8000};
    int bufferSize = 0;
    File uploadFile;



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
        audioRecorder = initializeRecord();
        if (audioRecorder != null){
            Toast.makeText(this, "Recording is  started",Toast.LENGTH_SHORT).show();
            audioRecorder.startRecording();
        }else
            return;

        isRecording = true;
        recordT = new Thread(new Runnable() {
            @Override
            public void run() {
                writeToFile();
            }
        },"Recording Thread");
        recordT.start();

    }

    private void writeToFile(){
        byte bDate[] = new byte[bufferEle];
        FileOutputStream fos =null;
        File recordFile = createTempFile();
        try {
            fos = new FileOutputStream(recordFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording){
            audioRecorder.read(bDate,0,bufferEle);
            try {
                fos.write(bDate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Following function converts short data to byte data
    private byte[] writeShortToByte(short[] sData) {
        int size = sData.length;
        byte[] byteArrayData = new byte[size * 2];
        for (int i = 0; i < size; i++) {
            byteArrayData[i * 2] = (byte) (sData[i] & 0x00FF);
            byteArrayData[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }

        return byteArrayData;
    }

    //Creates temporary .raw file for recording
    private File createTempFile() {
        File tempFile = new File(Environment.getExternalStorageDirectory(), "aditi.raw");
        return tempFile;
    }

    //Create file to convert to .wav format
    private File createWavFile() {
        File wavFile = new File(Environment.getExternalStorageDirectory(), "aditi_" + System.currentTimeMillis() + ".wav");
        return wavFile;
    }

    /*
     *  Convert raw to wav file
     *  @param java.io.File temporay raw file
     *  @param java.io.File destination wav file
     *  @return void
     *
     * */
    private void convertRawToWavFile(File tempFile, File wavFile) {
        FileInputStream fin = null;
        FileOutputStream fos = null;
        long audioLength = 0;
        long dataLength = audioLength + 36;
        long sampleRate = RECORD_RATE;
        int channel = 1;
        long byteRate = RECORD_BPP * RECORD_RATE * channel / 8;
        String fileName = null;

        byte[] data = new byte[bufferSize];
        try {
            fin = new FileInputStream(tempFile);
            fos = new FileOutputStream(wavFile);
            audioLength = fin.getChannel().size();
            dataLength = audioLength + 36;
            createWaveFileHeader(fos, audioLength, dataLength, sampleRate, channel, byteRate);

            while (fin.read(data) != -1) {
                fos.write(data);
            }

            uploadFile = wavFile.getAbsoluteFile();
        } catch (FileNotFoundException e) {
            //Log.e("MainActivity:convertRawToWavFile",e.getMessage());
        } catch (IOException e) {
            //Log.e("MainActivity:convertRawToWavFile",e.getMessage());
        } catch (Exception e) {
            //Log.e("MainActivity:convertRawToWavFile",e.getMessage());
        }
    }

    /*
   * To create wav file need to create header for the same
   *
   * @param java.io.FileOutputStream
   * @param long
   * @param long
   * @param long
   * @param int
   * @param long
   * @return void
   */
    private void createWaveFileHeader(FileOutputStream fos, long audioLength, long dataLength, long sampleRate, int channel, long byteRate) {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (dataLength & 0xff);
        header[5] = (byte) ((dataLength >> 8) & 0xff);
        header[6] = (byte) ((dataLength >> 16) & 0xff);
        header[7] = (byte) ((dataLength >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channel;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (audioLength & 0xff);
        header[41] = (byte) ((audioLength >> 8) & 0xff);
        header[42] = (byte) ((audioLength >> 16) & 0xff);
        header[43] = (byte) ((audioLength >> 24) & 0xff);

        try {
            fos.write(header, 0, 44);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //Log.e("MainActivity:createWavFileHeader()",e.getMessage());
        }

    }

    /*
    * delete created temperory file
    * @param
    * @return void
    */
    private void deletTempFile() {
        File file = createTempFile();
        file.delete();
    }

    /*
     * Initialize audio record
     *
     * @param
     * @return android.media.AudioRecord
     */
    private AudioRecord initializeRecord() {
        short[] audioFormat = new short[]{AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
        short[] channelConfiguration = new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO};
        for (int rate : recordRate) {
            for (short aFormat : audioFormat) {
                for (short cConf : channelConfiguration) {
                    //Log.d("MainActivity:initializeRecord()","Rate"+rate+"AudioFormat"+aFormat+"Channel Configuration"+cConf);
                    try {
                        int buffSize = AudioRecord.getMinBufferSize(rate, cConf, aFormat);
                        bufferSize = buffSize;

                        if (buffSize != AudioRecord.ERROR_BAD_VALUE) {
                            AudioRecord aRecorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, cConf, aFormat, buffSize);

                            if (aRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                RECORD_RATE = rate;
                                //Log.d("MainActivity:InitializeRecord - AudioFormat",String.valueOf(aFormat));
                                //Log.d("MainActivity:InitializeRecord - Channel",String.valueOf(cConf));
                                //Log.d("MainActivity:InitialoizeRecord - rceordRate", String.valueOf(rate));
                                return aRecorder;
                            }
                        }
                    } catch (Exception e) {
                        //Log.e("MainActivity:initializeRecord()",e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    /*
    * Method to stop and release audio record
    *
    * @param
    * @return void
    */
    private void stopRecord() {
        if (null != audioRecorder) {
            isRecording = false;
            audioRecorder.stop();
            audioRecorder.release();
            audioRecorder = null;
            recordT = null;
            Toast.makeText(getApplicationContext(), "Recording is stopped", Toast.LENGTH_LONG).show();
        }
        convertRawToWavFile(createTempFile(), createWavFile());
        if (uploadFile.exists()) {
            //Log.d("AudioRecorderService:stopRecord()", "UploadFile exists");
        }
        new UploadFile().execute(uploadFile);
        deletTempFile();
    }

    /*
     * Asynchronous task to upload audio file in background
     *
     *
     */
    private class UploadFile extends AsyncTask<File, Void, Void> {
        protected Void doInBackground(File... files) {

            if (files[0] == null)
                return null;
            try {
                File fileToUpload = files[0];
                String boundary = "*****";
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                int maxBufferSize = 1 * 1024 * 1024;
                String fileName = fileToUpload.getAbsolutePath();
                FileInputStream fis = new FileInputStream(new File(fileName));
                URL serverUrl = new URL("http://192.168.78.128/UploadToServer.php");
                HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", fileName);
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                int bytesAvailable = fis.available();
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fis.read(buffer, 0, bufferSize);


                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fis.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fis.read(buffer, 0, bufferSize);

                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // Responses from the server (code and message)
                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                // /Log.d("AudioRecorderService:AsyncTask",String.valueOf(serverResponseCode));
                // /Log.d("AudioRecorderService:AsyncTask",serverResponseMessage);

                if (serverResponseCode == 200) {

                    Toast.makeText(getApplicationContext(), "File is uploaded successfully", Toast.LENGTH_SHORT).show();

                }

                //close the streams //
                fis.close();
                dos.flush();
                dos.close();


            } catch (Exception e) {
                Log.e("AudioRecorder:Asynctask", e.getMessage());
            }


            return null;

        }
    }
}