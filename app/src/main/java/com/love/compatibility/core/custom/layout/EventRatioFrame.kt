package com.love.compatibility.core.custom.layout

import android.widget.ImageView
import com.love.compatibility.core.custom.imageview.StrokeImageView

interface EventRatioFrame {
    fun onImageClick(image: StrokeImageView, btnEdit: ImageView)
}