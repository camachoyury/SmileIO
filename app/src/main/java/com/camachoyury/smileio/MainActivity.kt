package com.camachoyury.smileio

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var data: ByteArray? = SmileApp.mCapturedPhotoData;

        data?.let {
            val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
            this.pic.setImageBitmap(bmp);
        }


    }


}
