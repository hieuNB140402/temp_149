package com.love.compatibility.dialog.template

import android.app.Activity
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseDialog
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.helper.AssetHelper
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.utils.key.AssetsKey
import com.love.compatibility.databinding.DialogTemplateBinding

class TemplateDialog(val context: Activity) : BaseDialog<DialogTemplateBinding>(context, maxWidth = true, maxHeight = true) {
    override val layoutId: Int = R.layout.dialog_template
    override val isCancelOnTouchOutside: Boolean = false
    override val isCancelableByBack: Boolean = false

    private val templateAdapter by lazy { TemplateAdapter() }

    var onChooseImageClick: (() -> Unit) = {}
    var onItemClick: ((String) -> Unit) = {}
    var onDismissClick: (() -> Unit) = {}

    override fun initView() {
        initRcv()
        context.hideNavigation()
    }

    override fun initAction() {
        binding.apply {
            templateAdapter.onChooseImageClick = {
                onChooseImageClick.invoke()
            }

            templateAdapter.onItemClick = { path ->
                onItemClick.invoke(path)
            }

            flOutSide.setOnSingleClick { onDismissClick.invoke() }
        }
    }

    override fun onDismissListener() {}

    private fun initRcv(){
        binding.rcvTemplate.apply {
            adapter = templateAdapter
            itemAnimator = null
        }
        val allTemplate = AssetHelper.getSubfoldersAsset(context, AssetsKey.BACKGROUND_ASSET)
        templateAdapter.submitList(allTemplate)
    }
}