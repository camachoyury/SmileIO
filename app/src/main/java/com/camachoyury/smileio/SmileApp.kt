package com.camachoyury.smileio

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

/**
 * Created by yury on 6/19/17.
 */
class SmileApp:Application() {



    override fun onCreate() {
        super.onCreate()

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    companion object Factory {
        var sInstance: SmileApp? = null
        var mCapturedPhotoData: ByteArray? = null

        // Getters & Setters
        fun getCapturedPhotoData(): ByteArray? {
            return mCapturedPhotoData
        }

        fun setCapturedPhotoData(capturedPhotoData: ByteArray) {
            mCapturedPhotoData = capturedPhotoData
        }
    }
}