package com.love.compatibility.data.model.choose_image

data class AllImageModel(
    val imageFirst: String,
    val size: Int,
    val nameFolder: String,
    val listImage: ArrayList<ImageModel> = arrayListOf()
)
