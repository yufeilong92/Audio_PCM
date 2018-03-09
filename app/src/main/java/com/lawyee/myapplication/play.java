package com.lawyee.myapplication;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * All rights Reserved, Designed By www.lawyee.com
 *
 * @version V 1.0 xxxxxxxx
 * @Title: MyApplication
 * @Package com.lawyee.myapplication
 * @Description: $todo$
 * @author: YFL
 * @date: 2018/3/9 14:04
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018 www.lawyee.com Inc. All rights reserved.
 * 注意：本内容仅限于北京法意科技有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class play {

    private AudioTrack player;

    public void playAudio() {

            DataInputStream dis = null;
            try {
                //从音频文件中读取声音
                dis = new DataInputStream(new BufferedInputStream(new FileInputStream("/storage/emulated/0/baiduASR/115442.63075767734.pcm")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int sampleRateInHz = 16000;
            //最小缓存区
            int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            //创建AudioTrack对象   依次传入 :流类型、采样率（与采集的要一致）、音频通道（采集是IN 播放时OUT）、量化位数、最小缓冲区、模式
            player = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);

            byte[] data = new byte[bufferSizeInBytes];


            player.play();//开始播放
            while (true) {
                int i = 0;
                try {
                    while (dis.available() > 0 && i < data.length) {
                        data[i] = dis.readByte();//录音时write Byte 那么读取时就该为readByte要相互对应
                        i++;
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                player.write(data, 0, data.length);

                if (i != bufferSizeInBytes) //表示读取完了
                {
                    player.stop();//停止播放
                    player.release();//释放资源
                    break;
                }
        }
    }
}
