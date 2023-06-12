/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.coincounter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.util.LinkedList
import kotlin.math.max
import org.tensorflow.lite.task.vision.detector.Detection

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: List<Detection> = LinkedList<Detection>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()
    private var textPaintValue = Paint()

    private var scaleFactor: Float = 1f

    private var bounds = Rect()

    private var value:Float = 0.0f

    private var rates: changerates? = changerates("","",0,"",mapOf<String, Double>())

    private var CentsMap: Map<String, Boolean> = mapOf<String, Boolean>()
    private var hasFalseValue:Boolean = false
    private val PREFS_NAME = "YOUR_TAG"
    private val DATA_TAG = "RATE_PREFERENCE"
    private val CB1CENT = "CB1CENT"
    private val CB2CENT = "CB2CENT"
    private val CB5CENT = "CB5CENT"
    private val CB10CENT = "CB10CENT"
    private val CB20CENT = "CB20CENT"
    private val CB50CENT = "CB50CENT"
    private val CB1EURO = "CB1EURO"
    private val CB2EURO = "CB2EURO"

    init {
        initPaints()

        var ap: apiCall = apiCall()
        rates = ap.run(getContext())
        initCentMap()
    }

    fun clear() {
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
        initCentMap()
    }

    private fun initCentMap(){
        var mSettings = context!!.getSharedPreferences(PREFS_NAME, 0)
        CentsMap = mapOf<String, Boolean>("1" to mSettings.getBoolean(CB1CENT,true),"2" to mSettings.getBoolean(CB2CENT,true),
        "5" to mSettings.getBoolean(CB5CENT,true), "10" to mSettings.getBoolean(CB10CENT,true),"20" to mSettings.getBoolean(CB20CENT,true),
        "50" to mSettings.getBoolean(CB50CENT,true),"100" to mSettings.getBoolean(CB1EURO,true),"200" to mSettings.getBoolean(CB2EURO,true))
        for (entry in CentsMap) {
            if (!entry.value) {
                hasFalseValue = true
                break
            }
        }
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        textPaintValue.color = Color.WHITE
        textPaintValue.style = Paint.Style.FILL
        textPaintValue.textSize = 80f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.bounding_box_color)
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        value = 0.0f
        for (result in results) {
            if (hasFalseValue && CentsMap.containsKey(result.categories[0].label) && CentsMap[result.categories[0].label] == false) {
                continue
            }
            val boundingBox = result.boundingBox

            val top = boundingBox.top * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor
            val left = boundingBox.left * scaleFactor
            val right = boundingBox.right * scaleFactor

            // Draw bounding box around detected objects
            val drawableRect = RectF(left, top, right, bottom)
            canvas.drawRect(drawableRect, boxPaint)

            // Create text to display alongside detected objects
            val drawableText =
                result.categories[0].label + " " +
                        String.format("%.2f", result.categories[0].score)
            // Draw rect behind display text
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawRect(
                left,
                top,
                left + textWidth + Companion.BOUNDING_RECT_TEXT_PADDING,
                top + textHeight + Companion.BOUNDING_RECT_TEXT_PADDING,
                textBackgroundPaint
            )

            // Draw text for detected object
            canvas.drawText(drawableText, left, top + bounds.height(), textPaint)

            value += result.categories[0].label.toFloat()
        }
        var drawableText ="EUR: " + String.format("%.2f", value/100f)
        canvas.drawText(drawableText, 0.0f, 60.0f, textPaintValue)

        var mSettings = context!!.getSharedPreferences(PREFS_NAME, 0)
        if (mSettings.contains(DATA_TAG)) {
            val savedRate = mSettings.getString(DATA_TAG, "")
            if (rates != null && rates?.base != "" && savedRate != "") {
                val result:Double = rates!!.changeRate("EUR", savedRate!!, value.toDouble())
                drawableText = savedRate + " " + String.format("%.2f", result.toFloat()/100f) + " " + savedRate
            }else{
                drawableText = "-----------"
            }
        }else{
            drawableText = "-----------"
        }
        canvas.drawText(drawableText, 0.0f, 130.0f, textPaintValue)
    }

    fun setResults(
      detectionResults: MutableList<Detection>,
      imageHeight: Int,
      imageWidth: Int,
    ) {
        results = detectionResults

        // PreviewView is in FILL_START mode. So we need to scale up the bounding box to match with
        // the size that the captured images will be displayed.
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}
