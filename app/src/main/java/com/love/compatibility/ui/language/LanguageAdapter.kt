package com.love.compatibility.ui.language

import android.annotation.SuppressLint
import android.content.Context
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseAdapter
import com.love.compatibility.core.extensions.gone
import com.love.compatibility.core.extensions.loadImageGlide
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.data.model.LanguageModel
import com.love.compatibility.databinding.ItemLanguageBinding

class LanguageAdapter(val context: Context) : BaseAdapter<LanguageModel, ItemLanguageBinding>(
    ItemLanguageBinding::inflate
) {
    var onItemClick: ((String) -> Unit) = {}
    override fun onBind(
        binding: ItemLanguageBinding, item: LanguageModel, position: Int
    ) {
        binding.apply {
            loadImageGlide(root, item.flag, imvFlag, false)
            tvLang.text = item.name


            val cardElevation = if (item.activate) {
                loadImageGlide(root, R.drawable.ic_tick_lang, btnRadio, false)
                vFocus.visible()
                0f
            } else {
                loadImageGlide(root, R.drawable.ic_not_tick_lang, btnRadio, false)
                vFocus.gone()
                10f
            }

            cvItem.cardElevation = cardElevation

            root.setOnSingleClick {
                onItemClick.invoke(item.code)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitItem(position: Int) {
        items.forEach { it.activate = false }
        items[position].activate = true
        notifyDataSetChanged()
    }
}