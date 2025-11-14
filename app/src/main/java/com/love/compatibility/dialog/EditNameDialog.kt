package com.love.compatibility.dialog

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseDialog
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.showToast
import com.love.compatibility.core.helper.DateHelper
import com.love.compatibility.core.helper.StringHelper
import com.love.compatibility.databinding.DialogEditNameBinding
import java.util.Calendar

class EditNameDialog(val context: Activity, var name: String, var age: Long) :
    BaseDialog<DialogEditNameBinding>(context, maxWidth = true, maxHeight = true) {
    override val layoutId: Int = R.layout.dialog_edit_name
    override val isCancelOnTouchOutside: Boolean = false
    override val isCancelableByBack: Boolean = false

    var onDoneClick: ((name: String, age: Long) -> Unit) = {_,_ ->}
    var onDismissClick: (() -> Unit) = {}

    override fun initView() {
        initText()
        context.hideNavigation()
    }

    override fun initAction() {
        binding.apply {

            tvAgeTop.setOnClickListener {
                val c = Calendar.getInstance()
                if (age != -1L) {
                    c.timeInMillis = age
                }
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val datePicker = DatePickerDialog(context, { _, y, m, d ->
                    // Tạo Calendar với ngày người dùng chọn
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, y)
                    selectedDate.set(Calendar.MONTH, m)
                    selectedDate.set(Calendar.DAY_OF_MONTH, d)
                    selectedDate.set(Calendar.HOUR_OF_DAY, 0)
                    selectedDate.set(Calendar.MINUTE, 0)
                    selectedDate.set(Calendar.SECOND, 0)
                    selectedDate.set(Calendar.MILLISECOND, 0)

                    // Lấy giá trị Long
                    val selectedTimeInMillis = selectedDate.timeInMillis

                    // Nếu muốn, vẫn có thể set text hiển thị
                    val formatted = String.format("%02d/%02d/%04d", m + 1, d, y) // MM/dd/yyyy
                    edtAge.setText(formatted)

                    age = selectedTimeInMillis
                }, year, month, day)

                // Giới hạn ngày lớn nhất là hôm nay
                datePicker.datePicker.maxDate = System.currentTimeMillis()

                datePicker.show()
            }

            btnDone.setOnSingleClick {
                val input = edtName.text.toString().trim()
                val sanitized = StringHelper.sanitizeFileName(input)
                if (sanitized == ""){
                    context.showToast(context.getString(R.string.please_do_not_leave_name_blank))
                }else{
                    onDoneClick(sanitized, age)
                }
            }

            flOutSide.setOnSingleClick { onDismissClick.invoke() }
        }
    }

    override fun onDismissListener() {

    }

    private fun initText() {
        binding.apply {
            edtName.setText(name)
            if (age != -1L){
                edtAge.text = DateHelper.formatLongToDate(age)
            }
        }
    }


}