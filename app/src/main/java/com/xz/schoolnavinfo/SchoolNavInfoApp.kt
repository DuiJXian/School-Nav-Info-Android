package com.xz.schoolnavinfo

import android.app.Application
import com.baidu.location.LocationClient
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.tts.WNTTSManager
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory
import com.baidu.navisdk.adapter.IBNTTSManager
import com.baidu.navisdk.adapter.IBNTTSManager.IBNOuterTTSPlayerCallback.PLAYER_STATE_IDLE
import com.baidu.navisdk.adapter.IBNTTSManager.IBNOuterTTSPlayerCallback.PLAYER_STATE_PLAYING
import com.baidu.navisdk.adapter.IBaiduNaviManager.INaviInitListener
import com.baidu.navisdk.adapter.struct.BNaviInitConfig
import com.xz.schoolnavinfo.common.utils.TTSHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SchoolNavInfoApp : Application() {
    private var ttsHelper: TTSHelper? = null
    override fun onCreate() {
        super.onCreate()
        setPrivacy()
        ttsHelper = TTSHelper(applicationContext)
        initNavi()
        initTTs()
    }


    private fun setPrivacy() {
        SDKInitializer.setAgreePrivacy(applicationContext, true)
        SDKInitializer.initialize(applicationContext)
        LocationClient.setAgreePrivacy(true)
    }

    private fun initTTs() {
//        百度tts
//        //驾车tts
//        val carConfig = BNTTsInitConfig.Builder()
//            .context(applicationContext)
//            .appId("118297887")
//            .appKey("30V5h5U25KKziWUHZY4onYjT")
//            .secretKey("cKpJAMpv67O6VqTG78vtdP1zQUCxSenA")
//            .authSn("9e38afe3-82ca8bd1-01-07e9-003f-09a7-01")
//            .build()
//        //步行骑行tts
//        val wbConfig = WNTTsInitConfig.Builder()
//            .context(applicationContext)
//            .appKey("30V5h5U25KKziWUHZY4onYjT")
//            .secretKey("cKpJAMpv67O6VqTG78vtdP1zQUCxSenA")
//            .authSn("9e38afe3-82ca8bd1-01-07e9-003f-09a7-01")
//            .build()
//
//        BaiduNaviManagerFactory.getTTSManager().initTTS(carConfig)
//        WNTTSManager.getInstance().initTTS(wbConfig)

//      使用系统tts
        BaiduNaviManagerFactory.getTTSManager().initTTS(object :
            IBNTTSManager.IBNOuterTTSPlayerCallback() {
            override fun getTTSState(): Int {
                return getTTsSate()
            }
            override fun playTTSText(text: String, p1: String?, bPreempt: Int, p3: String?): Int {
                return playText(text = text, bPreempt = bPreempt)
            }
            override fun getCurTTSSpeech(): String {
                return ""
            }
        })
        WNTTSManager.getInstance().initTTS(object : WNTTSManager.IWNOuterTTSPlayerCallback {
            override fun getTTSState(): Int {
                return getTTsSate()
            }
            override fun playTTSText(text: String, bPreempt: Int, speechId: Int): Int {
                return playText(text, bPreempt)
            }
        })

    }

    private fun getTTsSate(): Int {
        return if (ttsHelper?.isSpeaking() == true) {
            PLAYER_STATE_PLAYING
        } else {
            PLAYER_STATE_IDLE
        }
    }

    private fun playText(text: String, bPreempt: Int): Int {
        ttsHelper?.speakAdd(text) //追加到队列播放
        if (bPreempt == 1) { //如果需要打断 应该立即清楚当前队列 然后播放当前文本
            ttsHelper?.stop()
            ttsHelper?.speakFlush(text)
        }
        return 1
    }

    private fun initNavi() {
        if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited) {
            return
        }

        val config = BNaviInitConfig
            .Builder()
            .naviInitListener(object : INaviInitListener {
                override fun onAuthResult(status: Int, msg: String) {}

                override fun initStart() {
                    BaiduNaviManagerFactory.getBaiduNaviManager().enableOutLog(true)
                }

                override fun initSuccess() {
                    BaiduNaviManagerFactory.getBaiduNaviManager().setGpsNeverClose(true)
                }

                override fun initFailed(errCode: Int) {
                }
            })
            .build()
        BaiduNaviManagerFactory.getBaiduNaviManager().init(this, config)
    }

}