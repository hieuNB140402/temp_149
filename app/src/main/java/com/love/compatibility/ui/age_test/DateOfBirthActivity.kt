package com.love.compatibility.ui.age_test

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.hideSoftKeyboard
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.showToast
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.StringHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.databinding.ActivityDateOfBirthBinding
import com.love.compatibility.ui.finger_test.FingerTestActivity
import com.love.compatibility.ui.result.ResultActivity
import java.util.Calendar
import kotlin.jvm.java

class DateOfBirthActivity : BaseActivity<ActivityDateOfBirthBinding>() {
    private val viewModel: DateOfBirthViewModel by viewModels()

    private val startActivityReset =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK){
                val data = result.data?.getStringExtra(IntentKey.INTENT_KEY)
                if (data != "" || data != null) {
                    resetActivity()
                }
            }
        }

    override fun setViewBinding(): ActivityDateOfBirthBinding {
        return ActivityDateOfBirthBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        viewModel.updateIsLoveTest(intent.getBooleanExtra(IntentKey.INTENT_KEY, false))
    }

    override fun viewListener() {
        binding.apply {
            actionBar.btnActionBarLeft.setOnClickListener { handleBackLeftToRight() }
            btnDeleteRow1.setOnSingleClick { edtMyBirthDay.setText("") }
            btnDeleteRow2.setOnSingleClick { edtYourBirthDay.setText("") }
            btnAnalyze.setOnSingleClick { handleAnalyze() }
            tvMyBirthDay.setOnSingleClick { chooseBirth(ValueKey.ME, edtMyBirthDay, viewModel.myBirthDay) }
            tvYourBirthDay.setOnSingleClick { chooseBirth(ValueKey.YOU, edtYourBirthDay, viewModel.yourBirthDay) }
        }
    }

    override fun initActionBar() {
        binding.actionBar.apply {
            btnActionBarLeft.setImageResource(R.drawable.ic_back)
            btnActionBarLeft.visible()

            tvCenter.text = StringHelper.capitalizeWords(strings(R.string.date_of_birth_test))
            tvCenter.visible()
            tvCenter.select()
        }
    }

    private fun handleAnalyze() {
        binding.apply {

            val me = StringHelper.sanitizeFileName(edtMyBirthDay.text.toString().trim())
            val you = StringHelper.sanitizeFileName(edtYourBirthDay.text.toString().trim())

            if (me == "" || you == "") {
                showToast(R.string.please_do_not_leave_the_entry_blank)
                return
            }

            if (!viewModel.isLoveTest) {
                handleResult(me, you)
            } else {
                handleNextStep()
            }
        }
    }

    private fun handleResult(myDate: String, yourDate: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(IntentKey.INTENT_KEY, "${myDate.replace("_", "/")}${ValueKey.TYPE_SPIT}${yourDate.replace("_", "/")}")
        intent.putExtra(IntentKey.TYPE_KEY, ValueKey.TEST_DATE_TYPE)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivityReset.launch(intent, options)
    }

    private fun handleNextStep() {
        val intent = Intent(this, FingerTestActivity::class.java)
        intent.putExtra(IntentKey.INTENT_KEY, true)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }
    private fun resetActivity() {
        binding.edtMyBirthDay.setText("")
        binding.edtYourBirthDay.setText("")
    }
    private fun chooseBirth(who: Int, editText: EditText, age: Long){
        val c = Calendar.getInstance()
        if (age != -1L) {
            c.timeInMillis = age
        }
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, y, m, d ->
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
            editText.setText(formatted)

            if (who == ValueKey.ME){
                viewModel.updateMyBirthDay(selectedTimeInMillis)
            }else{
                viewModel.updateYourBirthDay(selectedTimeInMillis)

            }
        }, year, month, day)

        // Giới hạn ngày lớn nhất là hôm nay
        datePicker.datePicker.maxDate = System.currentTimeMillis()

        datePicker.show()
    }
}