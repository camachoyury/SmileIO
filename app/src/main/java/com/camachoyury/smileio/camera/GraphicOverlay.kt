package com.camachoyury.smileio.camera

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.android.gms.vision.CameraSource

/**
 * Created by yury camacho on 6/14/17.
 */

class GraphicOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var mPreviewWidth: Int = 0
    var mWidthScaleFactor = 1.0f
    var mPreviewHeight: Int = 0
    var mHeightScaleFactor = 1.0f
    var mFacing = CameraSource.CAMERA_FACING_FRONT
    val mGraphics = HashSet<Graphic>()


    fun clear(){
        synchronized(this){
            mGraphics.clear();
        }
        postInvalidate()

    }

    fun add(graphic: Graphic){
        synchronized(this){
            mGraphics.add(graphic)
        }
        postInvalidate()
    }

    fun remove(graphic: Graphic){
        synchronized(this){
            mGraphics.remove(graphic)
        }
        postInvalidate()
    }


    fun setCameraInfo(previewWidht: Int, previewHeght: Int, facing: Int){

        synchronized(this){

            mPreviewWidth = previewWidht
            mPreviewHeight = previewHeght
            mFacing = facing

        }
        postInvalidate()

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(this){
            if (mPreviewHeight != 0 && mPreviewWidth !=0){

                mWidthScaleFactor = (canvas!!.getWidth().toFloat())  / (mPreviewWidth).toFloat()
                mHeightScaleFactor = (canvas!!.getHeight().toFloat())  / (mPreviewHeight).toFloat()
            }

            for (graphic in mGraphics) {
                graphic.draw(canvas!!)
            }
        }
    }

    abstract class Graphic(val mOverlay: GraphicOverlay) {

        /**
         * Draw the graphic on the supplied canvas.  Drawing should use the following methods to
         * convert to view coordinates for the graphics that are drawn:
         *
         *  1. [Graphic.scaleX] and [Graphic.scaleY] adjust the size of
         * the supplied value from the preview scale to the view scale.
         *  1. [Graphic.translateX] and [Graphic.translateY] adjust the
         * coordinate from the preview's coordinate system to the view coordinate system.
         *

         * @param canvas drawing canvas
         */
        abstract fun draw(canvas: Canvas)

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view
         * scale.
         */
        fun scaleX(horizontal: Float): Float {
            return horizontal * mOverlay.mWidthScaleFactor
        }

        /**
         * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
         */
        fun scaleY(vertical: Float): Float {
            return vertical * mOverlay.mHeightScaleFactor
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        fun translateX(x: Float): Float {
            if (mOverlay.mFacing === CameraSource.CAMERA_FACING_FRONT) {
                return mOverlay.width - scaleX(x)
            } else {
                return scaleX(x)
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        fun translateY(y: Float): Float {
            return scaleY(y)
        }

        fun postInvalidate() {
            mOverlay.postInvalidate()
        }
    }

}

