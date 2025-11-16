package com.love.compatibility.ui.name_test

import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.hideSoftKeyboard
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.showToast
import com.love.compatibility.core.extensions.startIntent
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.StringHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.databinding.ActivityNameTestBinding
import com.love.compatibility.ui.age_test.DateOfBirthActivity
import com.love.compatibility.ui.result.ResultActivity

class NameTestActivity : BaseActivity<ActivityNameTestBinding>() {
    private val viewModel: NameTestViewModel by viewModels()

    private val startActivityReset =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK){
                val data = result.data?.getStringExtra(IntentKey.INTENT_KEY)
                if (data != "" || data != null) {
                    resetActivity()
                }
            }
        }

    override fun setViewBinding(): ActivityNameTestBinding {
        return ActivityNameTestBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        viewModel.updateIsLoveTest(intent.getBooleanExtra(IntentKey.INTENT_KEY, false))
    }

    override fun viewListener() {
        binding.apply {
            actionBar.btnActionBarLeft.setOnClickListener { handleBackLeftToRight() }
            btnDeleteRow1.setOnSingleClick { edtMyName.setText("") }
            btnDeleteRow2.setOnSingleClick { edtYourName.setText("") }
            main.setOnClickListener { hideSoftKeyboard() }
            btnAnalyze.setOnSingleClick { handleAnalyze() }
        }
    }

    override fun initActionBar() {
        binding.actionBar.apply {
            btnActionBarLeft.setImageResource(R.drawable.ic_back)
            btnActionBarLeft.visible()

            tvCenter.text = StringHelper.capitalizeWords(strings(R.string.name_test))
            tvCenter.visible()
            tvCenter.select()
        }
    }

    private fun handleAnalyze() {
        binding.apply {

            val myName = StringHelper.sanitizeFileName(edtMyName.text.toString().trim())
            val yourName = StringHelper.sanitizeFileName(edtYourName.text.toString().trim())

            if (myName == "" || yourName == "") {
                showToast(R.string.please_do_not_leave_the_entry_blank)
                return
            }

            edtMyName.clearFocus()
            edtYourName.clearFocus()

            if (!viewModel.isLoveTest) {
                handleResult(myName, yourName)
            } else {
                handleNextStep()
            }
        }
    }

    private fun resetActivity() {
        binding.edtMyName.setText("")
        binding.edtYourName.setText("")

        binding.edtMyName.clearFocus()
        binding.edtYourName.clearFocus()
    }

    private fun handleResult(myName: String, yourName: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(IntentKey.INTENT_KEY, "${myName}${ValueKey.TYPE_SPIT}${yourName}")
        intent.putExtra(IntentKey.TYPE_KEY, ValueKey.TEST_NAME_TYPE)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivityReset.launch(intent, options)
    }

    private fun handleNextStep() {
        val intent = Intent(this, DateOfBirthActivity::class.java)
        intent.putExtra(IntentKey.INTENT_KEY, true)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            val data = it.getStringExtra(IntentKey.INTENT_KEY)
            if (data != "" || data != null) {
                resetActivity()
            }
        }
    }
}