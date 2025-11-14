package com.love.compatibility.ui.choose_image.adapter

import android.content.Context
import com.love.compatibility.core.base.BaseAdapter
import com.love.compatibility.core.extensions.loadImageGlide
import com.love.compatibility.data.model.choose_image.AllImageModel
import com.love.compatibility.databinding.ItemAllImageBinding

class AllImageAdapter(val context: Context) : BaseAdapter<AllImageModel, ItemAllImageBinding>(ItemAllImageBinding::inflate) {
    var onItemClick: ((AllImageModel, Int) -> Unit) = { _, _ -> }


    override fun onBind(binding: ItemAllImageBinding, item: AllImageModel, position: Int) {
        binding.apply {
            loadImageGlide(root, item.imageFirst, binding.imvImage)

            txtNameFolder.text = item.nameFolder
            txtQuantityImage.text = item.size.toString()

            root.setOnClickListener { onItemClick.invoke(item, position) }
        }
    }

}
