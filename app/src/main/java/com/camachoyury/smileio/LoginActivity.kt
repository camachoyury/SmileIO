package com.camachoyury.smileio

import android.content.Intent
import android.os.Bundle

import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.facebook.FacebookException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult
import android.view.View
import com.facebook.login.LoginManager



/**
 * Created by yury on 6/19/17.
 */
class LoginActivity:AppCompatActivity() {


    val TAG ="LoginActivity"

    var callbackManager: CallbackManager? = null

    var firebaseAuth: FirebaseAuth? = null
    var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        callbackManager = CallbackManager.Factory.create();

        val facebookCallback = object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
             loginWithFacebook(loginResult.accessToken)
            }
            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
            }

        }

        loginButton!!.registerCallback(callbackManager, facebookCallback)

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuthListener  = FirebaseAuth.AuthStateListener { firebaseAuth ->
            var firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                goMain()
            } else {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager!!.onActivityResult(requestCode,resultCode,data)
    }

    fun goMain(){

        val flags = flags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        start<SmileActivity>(flags)

    }

    private fun loginWithFacebook(loginResult: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(loginResult.token)
        loginWithFirebase(credential)
    }

    private fun loginWithFirebase(credential: AuthCredential) {

        val completeCallback = object :  OnCompleteListener<AuthResult>{

           override fun onComplete(task: Task<AuthResult>) {

                if (!task.isSuccessful()) {

                    toast("oopss occurred an error")
                } else {
                    Log.d(TAG, "User is logged")
//                    val flags = flags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    start<SmileActivity>(flags)
                }
            }
        }

        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener(this, completeCallback )
    }


    override fun onStart() {
        super.onStart()
        firebaseAuth!!.addAuthStateListener(firebaseAuthListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseAuth!!.addAuthStateListener(firebaseAuthListener!!)
    }




}