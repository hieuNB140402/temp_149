package com.love.compatibility.dialog

import android.app.Activity
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseDialog
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.helper.AssetHelper
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.utils.key.AssetsKey
import com.love.compatibility.databinding.DialogEditChooseBinding
import com.love.compatibility.databinding.DialogTemplateBinding
import com.love.compatibility.dialog.template.TemplateAdapter

class EditChooseDialog(val context: Activity) : BaseDialog<DialogEditChooseBinding>(context, maxWidth = true, maxHeight = true) {
    override val layoutId: Int = R.layout.dialog_edit_choose
    override val isCancelOnTouchOutside: Boolean = false
    override val isCancelableByBack: Boolean = false
    var onNameClick: (() -> Unit) = {}
    var onAvatarClick: (() -> Unit) = {}
    var onDismissClick: (() -> Unit) = {}

    override fun initView() {
        context.hideNavigation()
    }

    override fun initAction() {
        binding.apply {
            btnName.setOnSingleClick { onNameClick.invoke() }

            btnAvatar.setOnSingleClick { onAvatarClick.invoke() }

            flOutSide.setOnSingleClick { onDismissClick.invoke() }
        }
    }

    override fun onDismissListener() {}
}