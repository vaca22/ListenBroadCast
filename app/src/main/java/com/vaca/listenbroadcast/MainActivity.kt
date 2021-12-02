package com.vaca.listenbroadcast

import add
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.IOException
import java.lang.Thread.sleep
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class MainActivity : AppCompatActivity() {
    val localPort=13207
    var localIp=""
    private var mAudioTrack: AudioTrack? = null
    var remoteport=13207
    var remoteip=""
    var pool: ByteArray?=null
    var wandorful=false
    val fuckx=ByteArray(1000000){
        0.toByte()
    }
    var fuckIndex=0;


    lateinit  var channel: DatagramChannel
    private val bufReceive: ByteBuffer = ByteBuffer.allocate(600)


    private var mPlayer: MyAudioTrack? = null
    fun bytebuffer2ByteArray(buffer: ByteBuffer): ByteArray? {
        buffer.flip()
        val len = buffer.limit() - buffer.position()
        val bytes = ByteArray(len)
        for (i in bytes.indices) {
            bytes[i] = buffer.get()
        }
        return bytes
    }


    fun initUdp(){
        try {
            channel = DatagramChannel.open();
            channel.socket().bind(InetSocketAddress(localPort));
        } catch (e: IOException) {

            e.printStackTrace();
        }
    }
    fun ip2String(s: InetAddress):String{
        var ip=s.toString()
        ip=ip.substring(ip.lastIndexOf("/")+1)
        return ip
    }
    val mutex= Mutex()
    fun StartListen() {
        while (true) {
            try {
                bufReceive.clear()
                val sourceAddress=channel.receive(bufReceive) as InetSocketAddress
                val sip=ip2String(sourceAddress.address)
                val sport=sourceAddress.port
                //播放解码后的数据
                val receiveByteArray=bytebuffer2ByteArray(bufReceive)!!

                for(k in receiveByteArray){
                    fuckx[fuckIndex]=k
                    fuckIndex++
                    if(fuckIndex>=1000000){
                        fuckIndex=0
                    }
                }




            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun send2Destination(message:ByteArray,ip:String,port:Int) {
        try {
            val buf: ByteBuffer = ByteBuffer.allocate(600)
            buf.clear()
            buf.put(message)
            buf.flip()
            channel.send(buf, InetSocketAddress(ip,port))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PathUtil.initVar(this)
       File( PathUtil.getPathX("fuck")).writeBytes(byteArrayOf(0.toByte()))
        // 初始化AudioTrack
        mPlayer = MyAudioTrack(
           16000,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        mPlayer!!.init()
        initUdp()
        send2Destination("fuck".toByteArray(),"192.168.6.110",13207)
        Thread{
            Thread.sleep(100)
            StartListen()
        }.start()

        Thread{
            sleep(3000)
            var ss=0
            while(true){
                mPlayer!!.playAudioTrack(fuckx, ss,1000)
                ss+=1000
                if(ss>=1000000){
                    ss=0
                }
            }

        }.start()

    }
}