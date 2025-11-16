package com.love.compatibility.ui.question_test

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.extensions.invisible
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.LanguageHelper
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.helper.StringHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.data.model.QuestionModel
import com.love.compatibility.databinding.ActivityQuestionTestBinding
import com.love.compatibility.dialog.ConfirmDialog
import com.love.compatibility.ui.result.ResultActivity
import kotlinx.coroutines.launch


class QuestionTestActivity : BaseActivity<ActivityQuestionTestBinding>() {
    private val viewModel: QuestionTestViewModel by viewModels()
    private val questionTestAdapter by lazy { QuestionTestAdapter(this) }

    private val viewState by lazy {
        arrayListOf(
            binding.vState1,
            binding.vState2,
            binding.vState3
        )
    }

    override fun setViewBinding(): ActivityQuestionTestBinding {
        return ActivityQuestionTestBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        initRcv()
        if (viewModel.stateQuestion.value == -1) {
            viewModel.setStateQuestion(ValueKey.STATE_1)
        } else {
            viewModel.setStateQuestion(viewModel.stateQuestion.value)
        }
    }

    override fun dataObservable() {
        binding.apply {
            lifecycleScope.launch {
                viewModel.stateQuestion.collect { state ->
                    when (state) {
                        ValueKey.STATE_1 -> { setUpState1UI() }
                        ValueKey.STATE_2 -> { setUpState2UI() }
                        else -> {}
                    }
                }
            }

            lifecycleScope.launch {
                viewModel.countAnswerSelected.collect { count ->
                    if (count == 0) {
                        actionBar.btnActionBarRight.invisible()
                    } else if (count == viewModel.questionList.size) {
                        actionBar.btnActionBarRight.visible()
                    }
                }
            }
        }
    }

    override fun viewListener() {
        binding.apply {
            actionBar.btnActionBarLeft.setOnSingleClick { handleBack() }
            actionBar.btnActionBarRight.setOnSingleClick { handleDone() }
        }
        questionTestAdapter.onChooseAnswer = { positionQuestion, positionAnswer ->
            handleChooseAnswer(positionQuestion, positionAnswer)
        }
    }

    override fun initActionBar() {
        binding.actionBar.apply {
            btnActionBarLeft.setImageResource(R.drawable.ic_back)
            btnActionBarLeft.visible()

            tvCenter.text = StringHelper.capitalizeWords(strings(R.string.question_test))
            tvCenter.visible()
            tvCenter.select()

            btnActionBarRight.setImageResource(R.drawable.ic_done)
        }
    }

    private fun initRcv() {
        binding.rcvQuestionTest.apply {
            adapter = questionTestAdapter
            itemAnimator = null
        }
    }

    private fun setUpState1UI() {
        binding.apply {
            viewModel.updateQuestionList(
                MediaHelper.readListFromFile<QuestionModel>(
                    this@QuestionTestActivity,
                    ValueKey.QUESTION_FILE
                )
            )
            viewState.forEachIndexed { index, viewState ->
                viewState.setBackgroundResource(if (index == 0) R.drawable.bg_6_solid_pink_ff else R.drawable.bg_6_solid_gray)
            }
            questionTestAdapter.submitList(viewModel.questionList, ValueKey.STATE_1)
        }
    }

    private fun setUpState2UI() {
        binding.apply {
            viewState.forEachIndexed { index, viewState ->
                viewState.setBackgroundResource(if (index == 1) R.drawable.bg_6_solid_pink_ff else R.drawable.bg_6_solid_gray)
            }
            questionTestAdapter.submitList(viewModel.questionList, ValueKey.STATE_2)
            rcvQuestionTest.smoothScrollToPosition(0)
        }
    }

    private fun handleChooseAnswer(positionQuestion: Int, positionAnswer: Int){
        when (viewModel.stateQuestion.value) {
            ValueKey.STATE_1 -> {
                if (viewModel.questionList[positionQuestion].positionMyAnswer == -1){
                    viewModel.setCountAnswerSelected(viewModel.countAnswerSelected.value + 1)
                }
                viewModel.updateMyAnswer(positionQuestion, positionAnswer)
            }

            ValueKey.STATE_2 -> {
                if (viewModel.questionList[positionQuestion].positionYouAnswer == -1){
                    viewModel.setCountAnswerSelected(viewModel.countAnswerSelected.value + 1)
                }
                viewModel.updateYourAnswer(positionQuestion, positionAnswer)
            }
            else -> {}
        }
    }

    private fun handleBack(){
        if (viewModel.countAnswerSelected.value == 0){
            handleBackLeftToRight()
        }else{
            val dialog = ConfirmDialog(this, R.string.exit,R.string.do_yout_want_to_exit)
            LanguageHelper.setLocale(this)
            dialog.show()
            dialog.onDismissClick ={
                dialog.dismiss()
                hideNavigation(true)
            }
            dialog.onNoClick = {
                dialog.dismiss()
                hideNavigation(true)
            }
            dialog.onYesClick = {
                dialog.dismiss()
                hideNavigation(true)
                handleBackLeftToRight()
            }
        }
    }

    private fun handleDone(){
        when (viewModel.stateQuestion.value) {
            ValueKey.STATE_1 -> {
                viewModel.setStateQuestion(ValueKey.STATE_2)
                viewModel.setCountAnswerSelected(0)
            }

            ValueKey.STATE_2 -> {
                lifecycleScope.launch {
                    showLoading()
                    viewModel.saveAnswer(this@QuestionTestActivity)
                    val intent = Intent(this@QuestionTestActivity, ResultActivity::class.java)
                    intent.putExtra(IntentKey.TYPE_KEY, ValueKey.QUESTION_TYPE)
                    val options = ActivityOptionsCompat.makeCustomAnimation(this@QuestionTestActivity, R.anim.slide_in_right, R.anim.slide_out_left)
                    dismissLoading(true)
                    startActivity(intent, options.toBundle())
                }
            }
            else -> {}
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            val data = it.getStringExtra(IntentKey.INTENT_KEY)
            if (data != "" || data != null) {
                viewModel.setStateQuestion(ValueKey.STATE_1)
            }
        }
    }
}