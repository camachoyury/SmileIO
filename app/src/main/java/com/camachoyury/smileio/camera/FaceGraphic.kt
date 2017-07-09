package com.camachoyury.smileio.camera

import android.content.Context
import android.graphics.*
import com.google.android.gms.vision.face.Face;
import android.graphics.drawable.Drawable
import android.util.Log
import com.camachoyury.smileio.R
import com.camachoyury.smileio.camera.GraphicOverlay


/**
 * Created by yury camacho on 6/14/17.
 */
class FaceGraphic(overlay: GraphicOverlay, context: Context) : GraphicOverlay.Graphic(overlay) {


    val TAG ="FaceGraphic"
    private val FACE_POSITION_RADIUS = 10.0f
    private val ID_TEXT_SIZE = 40.0f
    private val ID_Y_OFFSET = 50.0f
    private val ID_X_OFFSET = -50.0f
    private val BOX_STROKE_WIDTH = 5.0f
    var context: Context? = null



    val COLOR_CHOICES = intArrayOf(Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW)
    var mCurrentColorIndex = 0

    var mFacePositionPaint: Paint? = null
    var mIdPaint: Paint? = null
    var mBoxPaint: Paint? = null


    @Volatile  var mFace: Face? = null
    private var mFaceId: Int = 0
    private val mFaceHappiness: Float = 0.toFloat()

    init {

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.size
        val selectedColor = COLOR_CHOICES[mCurrentColorIndex]

        mFacePositionPaint = Paint()
        mFacePositionPaint!!.color = selectedColor

        mIdPaint = Paint()
        mIdPaint!!.color = selectedColor
        mIdPaint!!.textSize = ID_TEXT_SIZE

        mBoxPaint = Paint()
        mBoxPaint!!.color = selectedColor
        mBoxPaint!!.style = Paint.Style.STROKE
        mBoxPaint!!.strokeWidth = BOX_STROKE_WIDTH
        this.context = context

    }

    fun setId(id: Int) {
        mFaceId = id
    }

    fun updateFace(face: Face) {
        mFace = face
        postInvalidate()
    }


    override fun draw(canvas: Canvas) {
        val face = mFace ?: return



        // Draws a circle at the position of the detected face, with the face's track id below.
        val x = translateX(face.position.x + face.width / 2)
        val y = translateY(face.position.y + face.height / 2)
//        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint)
//        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint)
//        canvas.drawText("happiness: " + String.format("%.2f", face.isSmilingProbability), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint)
//        canvas.drawText("right eye: " + String.format("%.2f", face.isRightEyeOpenProbability), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint)
//        canvas.drawText("left eye: " + String.format("%.2f", face.isLeftEyeOpenProbability), x - ID_X_OFFSET * 2, y - ID_Y_OFFSET * 2, mIdPaint)


        var bitImage = BitmapFactory.decodeResource(context!!.getResources(), R.drawable.sombrero_chola)

        Log.e(TAG,"Ancho de la imagen es: ${bitImage.width} y el alto es: ${bitImage.height} ")

        canvas.drawBitmap(bitImage,x - bitImage.width/2, face.position.y, mFacePositionPaint)
        // Draws a bounding box around the face.
        val xOffset = scaleX(face.width / 2.0f)
        val yOffset = scaleY(face.height / 2.0f)
        val left = x - xOffset
        val top = y - yOffset
        val right = x + xOffset
        val bottom = y + yOffset

//        canvas.drawRect(left, top, right, bottom, mBoxPaint)
    }


}