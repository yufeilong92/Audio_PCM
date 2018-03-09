package com.lawyee.myapplication;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
   public static final String TAG = "MainActivity.class";
    private Button mStartAudio;
    private Button mStopAudio;
    private Button mPlayAudio;
    private Button mDeleteAudio;
    private TextView mTvAudioSucceess;
    private ScrollView mMScrollView;
    private File file;
    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mStartAudio = (Button) findViewById(R.id.startAudio);
        mStopAudio = (Button) findViewById(R.id.stopAudio);
        mPlayAudio = (Button) findViewById(R.id.playAudio);
        mDeleteAudio = (Button) findViewById(R.id.deleteAudio);
        mTvAudioSucceess = (TextView) findViewById(R.id.tv_audio_succeess);
        mMScrollView = (ScrollView) findViewById(R.id.mScrollView);

        mStartAudio.setOnClickListener(this);
        mStopAudio.setOnClickListener(this);
        mPlayAudio.setOnClickListener(this);
        mDeleteAudio.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startAudio:
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StartRecord();
                        Log.e(TAG,"start");
                    }
                });
                thread.start();
                printLog("开始录音");
                ButtonEnabled(false, true, false);
                break;
            case R.id.stopAudio:
                isRecording = false;
                ButtonEnabled(true, false, true);
                printLog("停止录音");
                break;
            case R.id.playAudio:
                PlayRecord();
                ButtonEnabled(true, false, false);
                printLog("播放录音");
                break;
            case R.id.deleteAudio:
                deleFile();
                break;
        }
    }
    //删除文件
    private void deleFile() {
        if(file == null){
            return;
        }
        file.delete();
        printLog("文件删除成功");
    }
    //播放文件
    public void PlayRecord() {
//       file = new File("/storage/emulated/0/baiduASR/617375.7941559432.pcm");
        if(this.file == null){
            return;
        }

        //读取文件

        int musicLength = (int) (this.file.length() / 2);
        short[] music = new short[musicLength];
        try {
            InputStream is = new FileInputStream(this.file);
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);
            int i = 0;
            while (dis.available() > 0) {
                music[i] = dis.readShort();
                i++;
            }
            dis.close();
            /**
             * AudioFormat.CHANNEL_CONFIGURATION_STEREO //双声道
             * AudioFormat.CHANNEL_CONFIGURATION_MONO 单声道
             *
             */

            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    16000, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                    AudioFormat.ENCODING_PCM_8BIT,
                    musicLength * 2,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
            audioTrack.write(music, 0, musicLength);
            audioTrack.stop();
        } catch (Throwable t) {
            Log.e(TAG, "播放失败");
        }
    }
    //获取/失去焦点
    private void ButtonEnabled(boolean start, boolean stop, boolean play) {
        mStartAudio.setEnabled(start);
        mStopAudio.setEnabled(stop);
        mPlayAudio.setEnabled(play);
    }
    //打印log
    private void printLog(final String resultString) {
        mTvAudioSucceess.post(new Runnable() {
            @Override
            public void run() {
                mTvAudioSucceess.append(resultString + "\n");
                mMScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
    //开始录音
    public void StartRecord() {
        Log.i(TAG,"开始录音");
        //16K采集率
        int frequency = 16000;
        //格式
        /**
         * 双声道
         */
        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
        /**
         * 单声道
         */
//        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        //16Bit
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        //生成PCM文件
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/reverseme.pcm");
        Log.i(TAG,"生成文件");
        //如果存在，就先删除再创建
        if (file.exists())
            file.delete();
        Log.i(TAG,"删除文件");
        try {
            file.createNewFile();
            Log.i(TAG,"创建文件");
        } catch (IOException e) {
            Log.i(TAG,"未能创建");
            throw new IllegalStateException("未能创建" + file.toString());
        }
        try {
            //输出流
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);

            short[] buffer = new short[bufferSize];
            audioRecord.startRecording();
            Log.i(TAG, "开始录音");
            isRecording = true;
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.writeShort(buffer[i]);
                }
            }
            audioRecord.stop();
            dos.close();
        } catch (Throwable t) {
            Log.e(TAG, "录音失败");
        }
    }
}
