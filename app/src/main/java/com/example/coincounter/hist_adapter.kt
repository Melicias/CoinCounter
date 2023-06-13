package com.example.coincounter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.LinkedList


class hist_adapter(conversions: LinkedList<Conversion>) : RecyclerView.Adapter<hist_adapter.ViewHolder>() {

    // Hold a reference to the current animator so that it can be canceled
    // midway.
    private var currentAnimator: Animator? = null

    // The system "short" animation time duration in milliseconds. This duration
    // is ideal for subtle animations or animations that occur frequently.
    private var shortAnimationDuration: Int = 0

    private val PREFS_NAME = "YOUR_TAG"
    private val CONVERSION_TAG = "conversion"

    var conversions: LinkedList<Conversion> = conversions
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): hist_adapter.ViewHolder {
        context = parent.getContext()
        val inflater = LayoutInflater.from(context)

        val HistView: View = LayoutInflater.from(context).inflate(R.layout.hist_card, parent, false)

        return ViewHolder(HistView)
    }

    override fun onBindViewHolder(holder: hist_adapter.ViewHolder, position: Int) {
        var c:Conversion = this.conversions.get(position)
        var tvCoinHist: TextView = holder.tvCoinHist
        var tvRateHist: TextView = holder.tvRateHist
        var tvDateHist: TextView = holder.tvDateHist
        var ivscreenshotHist: ImageButton = holder.ivscreenshotHist
        var btDelete: Button = holder.btDelete
        var container : FrameLayout =  holder.container
        var expanded_image : ImageView = holder.expanded_image



        tvCoinHist.setText(c.value)
        tvRateHist.setText(c.rateValue)
        tvDateHist.setText(c.date)

        val imgFile = File(c.imagePath)
        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            ivscreenshotHist.setImageBitmap(bitmap)
        }

        btDelete.setOnClickListener(View.OnClickListener {
            removeUpdate(c)
        })

        ivscreenshotHist.setOnClickListener(View.OnClickListener {
            //zoomImageFromThumb(ivscreenshotHist, BitmapFactory.decodeFile(imgFile.absolutePath),container,expanded_image)
        })

        //shortAnimationDuration = context!!.resources.getInteger(android.R.integer.config_shortAnimTime)
    }

    override fun getItemCount(): Int {
        return conversions.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvCoinHist: TextView
        lateinit var tvRateHist: TextView
        lateinit var tvDateHist: TextView
        lateinit var ivscreenshotHist: ImageButton
        lateinit var btDelete: Button
        lateinit var container: FrameLayout
        lateinit var expanded_image: ImageView

        init {
            tvCoinHist = itemView.findViewById(R.id.tvCoinHist)
            tvRateHist = itemView.findViewById(R.id.tvRateHist)
            tvDateHist = itemView.findViewById(R.id.tvDateHist)
            ivscreenshotHist = itemView.findViewById(R.id.ivscreenshotHist)
            btDelete = itemView.findViewById(R.id.btDeleteHist)
            container = itemView.findViewById(R.id.container)
            expanded_image = itemView.findViewById(R.id.expanded_image)
            btDelete.setOnClickListener(View.OnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    //listener.onItemClick(pos)
                }
            })
            ivscreenshotHist.setOnClickListener(View.OnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    //listener.onItemClick(pos)
                }
            })


            /*btDetalhes = itemView.findViewById<View>(R.id.btDetalhes) as Button
            btDetalhes.setOnClickListener {
                val i = Intent(context, PopUpExercicio::class.java)
                i.putExtra(PopUpExercicio.PARAM_1, exercicios.get(adapterPosition))
                context.startActivity(i)
            }*/
        }
    }

    fun removeUpdate(conversion: Conversion){
        this.conversions.remove(conversion)

        if(context != null){
            var mSettings = context!!.getSharedPreferences(PREFS_NAME, 0)
            if(mSettings.contains(CONVERSION_TAG)) {
                val json2: String = Gson().toJson(conversions)
                val editor = mSettings.edit()
                editor.putString(CONVERSION_TAG, json2)
                editor.commit()
            }
        }

        notifyDataSetChanged();
    }

    private fun zoomImageFromThumb(thumbView: View, bitmap: android.graphics.Bitmap, container: FrameLayout, expanded_image: ImageView) {
        // If there's an animation in progress, cancel it immediately and
        // proceed with this one.
        currentAnimator?.cancel()

        // Load the high-resolution "zoomed-in" image.
        expanded_image.setImageBitmap(bitmap)

        // Calculate the starting and ending bounds for the zoomed-in image.
        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the
        // container view. Set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBoundsInt)
         container.getGlobalVisibleRect(finalBoundsInt, globalOffset)

        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val layoutParams = FrameLayout.LayoutParams(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels
        )
        layoutParams.width = displayMetrics.widthPixels
        layoutParams.height = displayMetrics.heightPixels

        container.layoutParams = layoutParams



        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

        val startBounds = RectF(startBoundsInt)
        val finalBounds = RectF(finalBoundsInt)

        // Using the "center crop" technique, adjust the start bounds to be the
        // same aspect ratio as the final bounds. This prevents unwanted
        // stretching during the animation. Calculate the start scaling factor.
        // The end scaling factor is always 1.0.
        val startScale: Float
        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
            // Extend start bounds horizontally.
            startScale = startBounds.height() / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically.
            startScale = startBounds.width() / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it positions the zoomed-in view in the place of the
        // thumbnail.
        thumbView.alpha = 0f

        animateZoomToLargeImage(startBounds, finalBounds, startScale,expanded_image)

        setDismissLargeImageAnimation(thumbView, startBounds, startScale,expanded_image)
    }

    private fun animateZoomToLargeImage(startBounds: RectF, finalBounds: RectF, startScale: Float, expandedImage: ImageView) {
        expandedImage.visibility = View.VISIBLE

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the
        // top-left corner of the zoomed-in view. The default is the center of
        // the view.
        expandedImage.pivotX = 0f
        expandedImage.pivotY = 0f

        // Construct and run the parallel animation of the four translation and
        // scale properties: X, Y, SCALE_X, and SCALE_Y.
        currentAnimator = AnimatorSet().apply {
            play(
                ObjectAnimator.ofFloat(
                    expandedImage,
                    View.X,
                    startBounds.left,
                    finalBounds.left)
            ).apply {
                with(ObjectAnimator.ofFloat(expandedImage, View.Y, startBounds.top, finalBounds.top))
                with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_Y, startScale, 1f))
            }
            duration = shortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    currentAnimator = null
                }
            })
            start()
        }
    }

    private fun setDismissLargeImageAnimation(thumbView: View, startBounds: RectF, startScale: Float, expandedImage: ImageView) {
        // When the zoomed-in image is tapped, it zooms down to the original
        // bounds and shows the thumbnail instead of the expanded image.
        expandedImage.setOnClickListener {
            currentAnimator?.cancel()

            // Animate the four positioning and sizing properties in parallel,
            // back to their original values.
            currentAnimator = AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(expandedImage, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(expandedImage, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_X, startScale))
                    with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_Y, startScale))
                }
                duration = shortAnimationDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        thumbView.alpha = 1f
                        expandedImage.visibility = View.GONE
                        currentAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        thumbView.alpha = 1f
                        expandedImage.visibility = View.GONE
                        currentAnimator = null
                    }
                })
                start()
            }
        }
    }

}