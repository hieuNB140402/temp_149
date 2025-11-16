package com.love.compatibility.ui.result

import android.content.Context
import android.health.connect.datatypes.units.Pressure
import android.util.Log
import androidx.lifecycle.ViewModel
import com.love.compatibility.R
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.data.model.QuestionModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random
import kotlin.random.nextInt

class ResultViewModel : ViewModel() {

    private val maxHeart = 85
    private val maxText = 100

    private val _typeStart = MutableStateFlow<Int>(-1)
    val typeStart = _typeStart.asStateFlow()

    var dataTopText = ""

    var percentHeart = -1f
    var percentText = -1

    var textComment = -1

    var typeShowResult = ValueKey.LOVE_TEST

    var percentLoveTest = "50:50:50"

    fun setTypeStart(type: Int) {
        _typeStart.value = type
    }

    fun updateDataText(text: String) {
        dataTopText = text
    }

    fun setPercentLoveTest(){
        percentHeart = Random.nextInt(20..maxHeart).toFloat()
        Log.d("nbhieu", "percentHeart: ${percentHeart}")
        percentText = (percentHeart * (maxText.toFloat() / maxHeart)).toInt()
        Log.d("nbhieu", "percentText: ${percentText}")

        setValueTextComment()

    }

    private fun setValueTextComment(){
        textComment = when (percentText.coerceIn(0, 100)) {
            in 0..25 -> R.string.evaluate_1
            in 26..50 -> R.string.evaluate_2
            in 51..75 -> R.string.evaluate_3
            else -> R.string.evaluate_4
        }
    }

    fun updateTypeShowResult(result: Int){
        typeShowResult = result
    }

    fun updatePercentLoveTest(inputTarget: Int){
        percentLoveTest = randomTripleString(inputTarget)
    }

    fun randomTripleString(inputTarget: Int): String {
        val target = if (inputTarget > 100 || inputTarget <= 0) 50 else inputTarget
        val sum = target * 3

        while (true) {
            val a = (1..100).random()
            val b = (1..100).random()
            val c = sum - a - b

            if (c in 1..100) {
                return "$a:$b:$c"
            }
        }
    }

    fun updatePercentQuestionTest(context: Context){
        val questionModel = MediaHelper.readListFromFile<QuestionModel>(context, ValueKey.QUESTION_FILE)
        var countAnswerCorrect = 0
        questionModel.forEachIndexed { index, model ->
            if (model.positionMyAnswer == model.positionYouAnswer){
                countAnswerCorrect++
            }
        }

        percentHeart = ((countAnswerCorrect.toFloat()/ questionModel.size) * maxHeart)
        percentText =  if (percentHeart != 0f) (percentHeart * (maxText.toFloat() / maxHeart)).toInt() else 0
    }
}