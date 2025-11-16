package com.love.compatibility.ui.show_result_question_test

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.helper.StringHelper
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.data.model.QuestionModel
import com.love.compatibility.databinding.ActivityShowResultQuestionTestBinding
import com.love.compatibility.ui.question_test.QuestionTestAdapter
import com.love.compatibility.ui.question_test.QuestionTestViewModel

class ShowResultQuestionTestActivity : BaseActivity<ActivityShowResultQuestionTestBinding>() {
    private val questionTestViewModel: QuestionTestViewModel by viewModels()
    private val questionAdapter by lazy { QuestionTestAdapter(this) }
    override fun setViewBinding(): ActivityShowResultQuestionTestBinding {
        return ActivityShowResultQuestionTestBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        initRcv()
        questionTestViewModel.updateQuestionList(
            MediaHelper.readListFromFile<QuestionModel>(
            this@ShowResultQuestionTestActivity,
            ValueKey.QUESTION_FILE
        ))
        questionAdapter.submitList(questionTestViewModel.questionList, ValueKey.STATE_3)
        val (valueCorrect, valueIncorrect) = questionTestViewModel.getResult()
        binding.tvLeft.text = valueCorrect
        binding.tvRight.text = valueIncorrect
    }

    override fun viewListener() {
        binding.apply {
            actionBar.btnActionBarLeft.setOnSingleClick { handleBackLeftToRight() }
        }
    }

    override fun initActionBar() {
        binding.actionBar.apply {
            binding.actionBar.apply {
                btnActionBarLeft.setImageResource(R.drawable.ic_back)
                btnActionBarLeft.visible()

                tvCenter.text = StringHelper.capitalizeWords(strings(R.string.show_result))
                tvCenter.visible()
                tvCenter.select()
            }
        }
    }

    private fun initRcv(){
        binding.rcvQuestionTest.apply {
            adapter = questionAdapter
            itemAnimator = null
        }
    }
}