package com.love.compatibility.core.utils.state

import com.love.compatibility.data.model.choose_image.AllImageModel

sealed class LoadImageState {
    data class Success(val list: ArrayList<AllImageModel>) : LoadImageState()
    data class Error(val exception: Exception) : LoadImageState()
    object Loading : LoadImageState()
}