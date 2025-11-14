package com.love.compatibility.ui.choose_image.adapter

import android.content.Context
import com.love.compatibility.core.base.BaseAdapter
import com.love.compatibility.core.extensions.gone
import com.love.compatibility.core.extensions.loadImageGlide
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.UnitHelper
import com.love.compatibility.data.model.choose_image.ImageModel
import com.love.compatibility.databinding.ItemImageSubBinding

class ImageSubAdapter(val context: Context) : BaseAdapter<ImageModel, ItemImageSubBinding>(ItemImageSubBinding::inflate) {
    var onItemClick: ((ImageModel, Int) -> Unit) = { _, _ -> }


    override fun onBind(binding: ItemImageSubBinding, item: ImageModel, position: Int) {
        binding.apply {
            loadImageGlide(root, item.image, binding.imgImage)

            if (item.isSelected) {
                imvTick.visible()
                layoutTick.visible()
                cvImage.strokeWidth = UnitHelper.pxToDpInt(context, 2)
            } else {
                imvTick.gone()
                layoutTick.gone()
                cvImage.strokeWidth = UnitHelper.pxToDpInt(context, 0)
            }

            root.setOnClickListener { onItemClick.invoke(item, position) }
        }
    }

    fun submitItem(position: Int, selected: Boolean) {
        if (position in items.indices) {
            items[position].isSelected = selected
            notifyItemChanged(position)
        } else {
            System.err.println("Invalid position: $position in submitItem.")
        }
    }
}