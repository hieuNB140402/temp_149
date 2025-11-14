package com.love.compatibility.dialog

import android.app.Activity
import com.love.compatibility.core.extensions.gone
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseDialog
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.databinding.DialogConfirmBinding


class ConfirmDialog(val context: Activity, val title: Int, val description: Int, val isError: Boolean = false) :
    BaseDialog<DialogConfirmBinding>(context, maxWidth = true, maxHeight = true) {
    override val layoutId: Int = R.layout.dialog_confirm
    override val isCancelOnTouchOutside: Boolean = false
    override val isCancelableByBack: Boolean = false

    var onNoClick: (() -> Unit) = {}
    var onYesClick: (() -> Unit) = {}
    var onDismissClick: (() -> Unit) = {}

    override fun initView() {
        initText()
        if (isError) binding.btnNo.gone()
        context.hideNavigation()
    }

    override fun initAction() {
        binding.apply {
            btnNo.setOnSingleClick { onNoClick.invoke() }

            btnYes.setOnSingleClick { onYesClick.invoke() }

            flOutSide.setOnSingleClick { onDismissClick.invoke() }
        }
    }

    override fun onDismissListener() {

    }

    private fun initText() {
        binding.apply {
            tvTitle.text = context.strings(title)
            tvDescription.text = context.strings(description)
        }
    }

}