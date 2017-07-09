package com.camachoyury.smileio.camera

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import java.io.IOException
import android.annotation.SuppressLint


/**
 * Created by yury camacho on 6/14/17.
 */
class CameraSourcePreview(context: Context,attrs: AttributeSet) : ViewGroup(context, attrs) {

    val TAG = "CameraSourcePreview"

    var mContext: Context? = null
    var mSurfaceView: SurfaceView? = null
    var mStartRequested: Boolean? = null
    var  mSurfaceAvailable: Boolean? = null
    var mCameraSource: CameraSource? = null

    var mOverlay: GraphicOverlay? = null


    init {
        mContext = context
        mStartRequested = false
        mSurfaceAvailable = false
        mSurfaceView = SurfaceView(context)
        mSurfaceView!!.holder.addCallback(SurfaceCallback())
        addView(mSurfaceView)
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource, overlay: GraphicOverlay) {
        mOverlay = overlay
        start(cameraSource)
    }

    fun start(cameraSource: CameraSource){
        if (cameraSource != null)stop()

        mCameraSource = cameraSource
        if (mCameraSource != null){
            mStartRequested = true;
            startIfReady();
        }
    }


    fun stop() {
        if (mCameraSource != null) {
            mCameraSource!!.stop()
        }
    }

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    private fun startIfReady() {
        if (mStartRequested!! && mSurfaceAvailable!!) {
            mCameraSource!!.start(mSurfaceView!!.getHolder())
            if (mOverlay != null) {
                val size = mCameraSource!!.getPreviewSize()
                val min = Math.min(size.width, size.height)
                val max = Math.max(size.width, size.height)
                if (isPortraitMode()) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    mOverlay!!.setCameraInfo(min, max, mCameraSource!!.getCameraFacing())
                } else {
                    mOverlay!!.setCameraInfo(max, min, mCameraSource!!.getCameraFacing())
                }
                mOverlay!!.clear()
            }
            mStartRequested = false
        }
    }

    private fun isPortraitMode(): Boolean {
        val orientation = mContext!!.getResources().configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true
        }

        Log.d(TAG, "isPortraitMode returning false by default")
        return false
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var width = 320
        var height = 240
        if (mCameraSource != null) {
            val size = mCameraSource!!.getPreviewSize()
            if (size != null) {
                width = size.width
                height = size.height
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            val tmp = width
            width = height
            height = tmp
        }

        val layoutWidth = right - left
        val layoutHeight = bottom - top

        // Computes height and width for potentially doing fit width.
        var childWidth = layoutWidth
        var childHeight = (layoutWidth.toFloat() / width.toFloat() * height).toInt()

        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight
            childWidth = (layoutHeight.toFloat() / height.toFloat() * width).toInt()
        }

        for (i in 0..childCount - 1) {
            getChildAt(i).layout(0, 0, childWidth, childHeight)
        }

        try {
            startIfReady()
        } catch (e: IOException) {
            Log.e(TAG, "Could not start camera source.", e)
        }

    }
    inner class SurfaceCallback : SurfaceHolder.Callback {

        override fun surfaceCreated(surface: SurfaceHolder) {
            mSurfaceAvailable = true
            try {
                startIfReady()
            } catch (e: IOException) {
                Log.e(TAG, "Could not start camera source.", e)
            }

        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            mSurfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }



}