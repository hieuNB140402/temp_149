package com.love.compatibility.dialog

import android.app.Activity
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseDialog
import com.love.compatibility.core.extensions.setBackgroundConnerSmooth
import com.love.compatibility.databinding.DialogLoadingBinding

class WaitingDialog(val context: Activity) :
    BaseDialog<DialogLoadingBinding>(context, maxWidth = true, maxHeight = true) {
    override val layoutId: Int = R.layout.dialog_loading
    override val isCancelOnTouchOutside: Boolean = false
    override val isCancelableByBack: Boolean = false

    override fun initView() {
    }

    override fun initAction() {}

    override fun onDismissListener() {}

}