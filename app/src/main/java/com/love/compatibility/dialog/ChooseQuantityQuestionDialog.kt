package com.love.compatibility.dialog

import android.R.attr.description
import android.app.Activity
import com.love.compatibility.core.extensions.gone
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseDialog
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.databinding.DialogChooseQuantityQuestionBinding
import com.love.compatibility.databinding.DialogConfirmBinding


class ChooseQuantityQuestionDialog(val context: Activity) :
    BaseDialog<DialogChooseQuantityQuestionBinding>(context, maxWidth = true, maxHeight = true) {
    override val layoutId: Int = R.layout.dialog_choose_quantity_question
    override val isCancelOnTouchOutside: Boolean = false
    override val isCancelableByBack: Boolean = false

    var onChooseQuantity: ((quantity: Int) -> Unit) = {}
    var onDismissClick: (() -> Unit) = {}

    override fun initView() {
        initText()
        context.hideNavigation()
    }

    override fun initAction() {
        binding.apply {
            tv10Question.setOnSingleClick { onChooseQuantity.invoke(10) }
            tv15Question.setOnSingleClick { onChooseQuantity.invoke(15) }
            tv20Question.setOnSingleClick { onChooseQuantity.invoke(20) }

            flOutSide.setOnSingleClick { onDismissClick.invoke() }
        }
    }

    override fun onDismissListener() {

    }

    private fun initText() {
        binding.apply {
            tv10Question.text = context.getString(R.string.value_questions, "10")
            tv15Question.text = context.getString(R.string.value_questions, "15")
            tv20Question.text = context.getString(R.string.value_questions, "20")
        }
    }

}