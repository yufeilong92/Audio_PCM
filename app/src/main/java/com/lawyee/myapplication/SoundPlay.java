package com.lawyee.myapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * All rights Reserved, Designed By www.lawyee.com
 *
 * @version V 1.0 xxxxxxxx
 * @Title: MyApplication
 * @Package com.lawyee.myapplication
 * @Description: $todo$
 * @author: YFL
 * @date: 2018/3/9 16:09
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018 www.lawyee.com Inc. All rights reserved.
 * 注意：本内容仅限于北京法意科技有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class SoundPlay {

//音频获取源
    private int audioSource
            = MediaRecorder.AudioSource.MIC;
//设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    private static int sampleRateInHz
            = 44100;
//设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private static int channelConfig
            = AudioFormat.CHANNEL_CONFIGURATION_MONO;
//音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private static int audioFormat
            = AudioFormat.ENCODING_PCM_16BIT;
    public void sound(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.pcm");
        // 删除录音文件
        if (file.exists())
            file.delete();
        // 创建录音文件
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create "
                    + file.toString());
        }
        try {
            // Create a DataOuputStream to write the audio data into the
            // saved file.
            FileOutputStream fos = new FileOutputStream(file);// 建立一个可存取字节的文件
            // Create a new AudioRecord object to record the audio.
            // 获得满足条件的最小缓冲区大小
          int   bufferSizeInBytes = AudioRecord.getMinBufferSize(
                    sampleRateInHz, channelConfig, audioFormat);
            // 创建AudioRecord对象
            AudioRecord  audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                    channelConfig, audioFormat, bufferSizeInBytes);
            byte[] buffer = new byte[bufferSizeInBytes];
            audioRecord.startRecording();
           boolean isRecording = true;
            while (isRecording) {
                audioRecord.read(buffer, 0, bufferSizeInBytes);
                fos.write(buffer);
            }
            audioRecord.stop();
            audioRecord.stop();
            audioRecord.release();// 释放资源
            audioRecord = null;
            fos.close();
        } catch (Throwable t) {
            Log.e("AudioRecord", "Recording Failed");
        }
    }
}
