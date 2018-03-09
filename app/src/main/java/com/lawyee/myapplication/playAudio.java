package com.lawyee.myapplication;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * All rights Reserved, Designed By www.lawyee.com
 *
 * @version V 1.0 xxxxxxxx
 * @Title: MyApplication
 * @Package com.lawyee.myapplication
 * @Description: $todo$
 * @author: YFL
 * @date: 2018/3/9 16:00
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018 www.lawyee.com Inc. All rights reserved.
 * 注意：本内容仅限于北京法意科技有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class playAudio {

//音频获取源
    private int audioSource
            = MediaRecorder.AudioSource.MIC;

//    设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    private static int sampleRateInHz
            = 16000;
//    设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private static int channelConfig
            = AudioFormat.CHANNEL_CONFIGURATION_MONO;
//    音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private static int audioFormat
            = AudioFormat.ENCODING_PCM_16BIT;
    public void playAudioData(String path){
        File file = new File(path);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 获得满足条件的最小缓冲区大小
      int   bufferSizeInBytes = AudioRecord.getMinBufferSize(
                sampleRateInHz, channelConfig, audioFormat);
        byte[] buffer = new byte[bufferSizeInBytes];
        int byteread=0;
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRateInHz, channelConfig, audioFormat,
                bufferSizeInBytes*4, AudioTrack.MODE_STREAM);
        // 放音
        audioTrack.play();
        try {
            while ((byteread = in.read(buffer)) != -1) {
                System.out.write(buffer, 0, byteread);
                System.out.flush();
                audioTrack.write(buffer, 0, bufferSizeInBytes);
            }
        } catch (Exception e) {
            Log.e("AudioTrack", "Playback Failed");
        }
    }
}
