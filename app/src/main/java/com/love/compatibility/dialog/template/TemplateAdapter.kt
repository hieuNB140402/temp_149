package com.love.compatibility.dialog.template

import com.love.compatibility.core.base.BaseAdapter
import com.love.compatibility.core.extensions.gone
import com.love.compatibility.core.extensions.loadImageGlide
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.databinding.ItemTemplateBinding

class TemplateAdapter : BaseAdapter<String, ItemTemplateBinding>(ItemTemplateBinding::inflate) {

    var onChooseImageClick: (() -> Unit) = {}
    var onItemClick: ((String) -> Unit) = {}

    override fun onBind(binding: ItemTemplateBinding, item: String, position: Int) {
        binding.apply {
            if(position == 0){
                btnChooseImage.visible()
                imvImage.gone()
                btnChooseImage.setOnSingleClick { onChooseImageClick.invoke() }

            }else{
                loadImageGlide(root, item, imvImage)
                btnChooseImage.gone()
                imvImage.visible()
                imvImage.setOnSingleClick { onItemClick.invoke(item) }
            }
        }
    }
}