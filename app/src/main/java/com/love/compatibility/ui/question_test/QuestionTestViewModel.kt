package com.love.compatibility.ui.question_test

import android.content.Context
import androidx.lifecycle.ViewModel
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.data.model.QuestionModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuestionTestViewModel : ViewModel() {
    private val _stateQuestion = MutableStateFlow<Int>(-1)
    val stateQuestion = _stateQuestion.asStateFlow()

    private val _countAnswerSelected = MutableStateFlow<Int>(0)
    val countAnswerSelected = _countAnswerSelected.asStateFlow()

    val questionList = ArrayList<QuestionModel>()

    fun setStateQuestion(state: Int){
        _stateQuestion.value = state
    }

    fun setCountAnswerSelected(count: Int){
        _countAnswerSelected.value = count
    }

    fun updateQuestionList(list: List<QuestionModel>){
        questionList.clear()
        questionList.addAll(list)
    }

    fun updateMyAnswer(positionQuestion: Int, positionAnswer: Int){
        questionList[positionQuestion].positionMyAnswer = positionAnswer
    }

    fun updateYourAnswer(positionQuestion: Int, positionAnswer: Int){
        questionList[positionQuestion].positionYouAnswer = positionAnswer
    }

    fun saveAnswer(context: Context){
        MediaHelper.writeListToFile(context, ValueKey.QUESTION_FILE, questionList)
    }

    fun getResult() : Pair<String, String>{
        var countAnswerCorrect = 0
        questionList.forEachIndexed { index, model ->
            if (model.positionMyAnswer == model.positionYouAnswer){
                countAnswerCorrect++
            }
        }
        val valueCorrect = "${countAnswerCorrect}/${questionList.size}"
        val valueIncorrect = "${questionList.size - countAnswerCorrect}/${questionList.size}"
        return valueCorrect to valueIncorrect
    }
}