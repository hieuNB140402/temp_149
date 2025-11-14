package com.love.compatibility.ui.intro

import android.content.Context
import com.love.compatibility.core.base.BaseAdapter
import com.love.compatibility.core.extensions.loadImageGlide
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.data.model.IntroModel
import com.love.compatibility.databinding.ItemIntroBinding

class IntroAdapter(val context: Context) : BaseAdapter<IntroModel, ItemIntroBinding>(
    ItemIntroBinding::inflate
) {
    override fun onBind(binding: ItemIntroBinding, item: IntroModel, position: Int) {
        binding.apply {
            loadImageGlide(root, item.image, imvImage, false)
            tvContent.text = context.strings(item.content)
            tvContent.select()
        }
    }
}