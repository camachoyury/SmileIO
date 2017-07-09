package com.camachoyury.smileio

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.camachoyury.smileio.camera.CameraSourcePreview
import com.camachoyury.smileio.camera.FaceGraphic
import com.camachoyury.smileio.camera.GraphicOverlay
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability.*
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException

/**
 * Created by yury on 6/17/17.
 */
class SmileActivity :AppCompatActivity(){



    val TAG = "FaceTracker"
    var mCameraSource: CameraSource? = null
    var mPreview: CameraSourcePreview? = null
    var mGraphicOverlay: GraphicOverlay? = null

    val RC_HANDLE_GMS = 9001
    val RC_HANDLE_CAMERA_PERM = 2

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_smile)

        mPreview = findViewById(R.id.preview)
        mGraphicOverlay = findViewById(R.id.faceOverlay)

        var user = FirebaseAuth.getInstance().currentUser
        if ( user == null){

            goLoginActivity()
        }else{
            if (permissionsEnabled())
                createCameraSource()
            else
                requestCameraPermission()
        }
    }

    private fun createCameraSource() {

        val context = applicationContext
        val detector = FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build()

        detector.setProcessor(MultiProcessor.Builder(GraphicFaceTrackerFactory()).build())

        if (!detector.isOperational()) {

            Log.w(TAG, "Face detector dependencies are not yet available.")
        }

        mCameraSource = CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setAutoFocusEnabled(true)
                .setRequestedFps(15.0f)
                .build()

    }

    inner class GraphicFaceTrackerFactory : MultiProcessor.Factory<Face> {

        override fun create(face: Face): Tracker<Face> {
            return GraphicFaceTracker(mGraphicOverlay!!)
        }
    }



    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()

        startCameraSource()
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        mPreview!!.stop()
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mCameraSource != null) {
            mCameraSource!!.release()
        }
    }


    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    private fun startCameraSource() {

        // check that the device has play services available.
        val code = getInstance().isGooglePlayServicesAvailable(
                applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
            dlg.show()
        }

        if (mCameraSource != null) {
            try {
                mPreview!!.start(mCameraSource!!,mGraphicOverlay!!)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                mCameraSource!!.release()
                mCameraSource = null
            }

        }
    }
    inner class GraphicFaceTracker internal constructor(private val mOverlay: GraphicOverlay) : Tracker<Face>() {
        private val mFaceGraphic: FaceGraphic

        init {
            var mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sombrero_chola);

            mFaceGraphic = FaceGraphic(mOverlay,this@SmileActivity.baseContext)
        }

        override fun onNewItem(faceId: Int, item: Face?) {
            mFaceGraphic.setId(faceId);
        }

        override fun onUpdate(p0: Detector.Detections<Face>?, face: Face?) {

            synchronized(this){

                if (face!!.isSmilingProbability > 0.5) {

                     if (mCameraSource != null) {

                         mCameraSource!!.takePicture(null, CameraSource.PictureCallback { bytes ->

                             SmileApp.setCapturedPhotoData(bytes)
                             var intent = Intent(this@SmileActivity, MainActivity::class.java)
                             startActivity(intent);

                         })
                     }
                 }

            }

            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face!!);
        }

        override fun onMissing(p0: Detector.Detections<Face>?) {
            mOverlay.remove(mFaceGraphic);
        }

        override fun onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

   override fun onCreateOptionsMenu( menu: Menu):Boolean{
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        if (item?.itemId == R.id.logout){

            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            goLoginActivity()
        }
        return super.onOptionsItemSelected(item)
    }



    //<editor-fold desc="permissons request">
    //validate if app has permissions access
    fun permissionsEnabled(): Boolean{
        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return rc == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission")

        val permissions = arrayOf<String>(Manifest.permission.CAMERA)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM)
            return
        }

        val thisActivity = this

        val listener = View.OnClickListener({ v ->
            ActivityCompat.requestPermissions(thisActivity, permissions,
                    RC_HANDLE_CAMERA_PERM)
        })

        Snackbar.make(mGraphicOverlay!!, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source")
            // we have permission, so create the camerasource
            createCameraSource()
            return
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.size +
                " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)")

        val listener = DialogInterface.OnClickListener { dialog, id -> finish() }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show()
    }
    //</editor-fold>

    fun goLoginActivity() {
        val flags = flags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        start<LoginActivity>()

    }




}