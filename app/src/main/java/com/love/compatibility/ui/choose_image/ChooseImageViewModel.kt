package com.love.compatibility.ui.choose_image

import androidx.lifecycle.ViewModel
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.data.model.choose_image.AllImageModel
import com.love.compatibility.data.model.choose_image.ImageModel
import com.love.compatibility.data.model.choose_image.ImageSelectedModel
import com.love.compatibility.ui.choose_image.adapter.AllImageAdapter
import com.love.compatibility.ui.choose_image.adapter.ImageSubAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.ArrayList

class ChooseImageViewModel : ViewModel() {

    val allImageList = ArrayList<AllImageModel>()
    val subImageList =  ArrayList<ImageModel>()

    private val _folderNameSelected = MutableStateFlow<String>("")
    val folderNameSelected = _folderNameSelected.asStateFlow()

    private val _isOpenMore = MutableStateFlow<Boolean>(false)
    val isOpenMore = _isOpenMore.asStateFlow()

    var positionAllImage = 0

    var typeChooseImage = ValueKey.TYPE_OUTSTANDING_HOME

    var widthRatio = 0
    var heightRatio = 0
    var albumSave = ValueKey.OUTSTANDING_HOME_ALBUM

    fun setAllImageList(list: ArrayList<AllImageModel>, adapter: AllImageAdapter) {
        allImageList.clear()
        allImageList.addAll(list)
        adapter.submitList(allImageList)
    }
    
    fun setSubImageList(list: ArrayList<ImageModel>, adapter: ImageSubAdapter){
        subImageList.clear()
        subImageList.addAll(list)
        adapter.submitList(subImageList)
    }

    fun setFolderNameSelected(folderName: String){
        _folderNameSelected.value = folderName
    }

    fun setOpenMore() {
        _isOpenMore.value = !_isOpenMore.value
    }

    fun setOpenMore(isMore: Boolean) {
        _isOpenMore.value = isMore
    }

    fun updatePositionAllImage(position: Int) { positionAllImage = position}

    fun updateTypeChooseImage(type: Int){
        typeChooseImage = type

        albumSave = when(type){
            ValueKey.TYPE_AVATAR_ME -> ValueKey.AVATAR_ME_ALBUM
            ValueKey.TYPE_AVATAR_YOU -> ValueKey.AVATAR_YOU_ALBUM
            ValueKey.TYPE_OUTSTANDING_HOME -> ValueKey.OUTSTANDING_HOME_ALBUM
            else -> ValueKey.TEMPLATE_ALBUM
        }
    }

    fun updateRatio(ratio: String){
        val ratioSpit = ratio.split(ValueKey.TYPE_SPIT)
        val (w, h) = ratioSpit.first() to ratioSpit.last()
        widthRatio = w.toInt()
        heightRatio = h.toInt()
    }
}