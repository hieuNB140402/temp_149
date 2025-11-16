package com.love.compatibility.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.common.collect.Multimaps.index
import com.love.compatibility.core.helper.AssetHelper
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.helper.SharePreferenceHelper
import com.love.compatibility.core.utils.DataLocal
import com.love.compatibility.core.utils.key.AssetsKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.core.utils.state.GetStatusLoveState
import com.love.compatibility.core.utils.state.GetStringState
import com.love.compatibility.data.model.QuestionModel
import com.love.compatibility.data.model.status.StatusLoveTestModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class HomeViewModel : ViewModel() {
    // Home ------------------------------------------------------------------------------------------------------------
    private val _statusFragment = MutableStateFlow<Int>(-1)
    val statusFragment = _statusFragment.asStateFlow()

    private val _imageOutStanding = MutableStateFlow<GetStringState>(GetStringState.Loading)
    val imageOutStanding = _imageOutStanding.asStateFlow()


    fun setStatusFragment(status: Int){
        _statusFragment.value = status
    }

    fun loadImageOutStanding(context: Context){
        _imageOutStanding.value = GetStringState.Loading
        try{
            val imageInFolder = MediaHelper.getImageInternal(context, ValueKey.OUTSTANDING_HOME_ALBUM)
            if (imageInFolder.isEmpty()){
                _imageOutStanding.value = GetStringState.Success(AssetsKey.OUTSTANDING_DEFAULT_ASSET)
            }else{
                _imageOutStanding.value = GetStringState.Success(imageInFolder.first())
            }
        }catch (e: Exception){
            GetStringState.Error(e)
        }
    }

    fun loadQuestionsList(context: Context, sharePreference: SharePreferenceHelper, quantity: Int){
        val keyLang = sharePreference.getPreLanguage()
        val questionAllLanguageList = AssetHelper.getSubfoldersAsset(context, AssetsKey.QUESTION_ASSET, true)
        val positionQuestionLanguage = questionAllLanguageList.indexOfFirst { it.split(".").first() == keyLang }
        val allQuestionInJson = AssetHelper.readJsonAsset<ArrayList<QuestionModel>>(context, "${AssetsKey.QUESTION_ASSET}/${questionAllLanguageList[positionQuestionLanguage]}") ?: arrayListOf()
        val questionList = ArrayList<QuestionModel>()
        val questionPositionList = ArrayList<Int>()
        do {
            var position = Random.nextInt(0, ValueKey.MAX_QUANTITY_QUESTION)
            if (questionPositionList.isNotEmpty()){
                while(questionPositionList.contains(position)){
                    position = Random.nextInt(0, ValueKey.MAX_QUANTITY_QUESTION)
                }
            }
            questionPositionList.add(position)
        }while (questionPositionList.size < quantity)

        questionPositionList.forEach { index ->
            questionList.add(allQuestionInJson[index])
        }
        MediaHelper.writeListToFile(context, ValueKey.QUESTION_FILE, questionList)
    }

    // Status ----------------------------------------------------------------------------------------------------------
    private val _statusLoveTest = MutableStateFlow<GetStatusLoveState>(GetStatusLoveState.Loading)
    val statusLoveTest = _statusLoveTest.asStateFlow()

    fun updateValueStatusLoveTest(context: Context){
        _statusLoveTest.value = GetStatusLoveState.Loading
        _statusLoveTest.value = GetStatusLoveState.Success(MediaHelper.readModelFromFile<StatusLoveTestModel>(context, ValueKey.STATUS_FILE) ?: StatusLoveTestModel())
    }

    fun updateTemplatePath(context: Context, path: String){
        val currentStatusLove = MediaHelper.readModelFromFile<StatusLoveTestModel>(context, ValueKey.STATUS_FILE) ?: StatusLoveTestModel()
        currentStatusLove.templatePath = path
        MediaHelper.writeModelToFile(context, ValueKey.STATUS_FILE, currentStatusLove)
    }

    fun updateAvatarMePath(context: Context, path: String){
        val currentStatusLove = MediaHelper.readModelFromFile<StatusLoveTestModel>(context, ValueKey.STATUS_FILE) ?: StatusLoveTestModel()
        currentStatusLove.together.avatarPathMe = path
        MediaHelper.writeModelToFile(context, ValueKey.STATUS_FILE, currentStatusLove)
    }

    fun updateAvatarYouPath(context: Context, path: String){
        val currentStatusLove = MediaHelper.readModelFromFile<StatusLoveTestModel>(context, ValueKey.STATUS_FILE) ?: StatusLoveTestModel()
        currentStatusLove.together.avatarPathYou = path
        MediaHelper.writeModelToFile(context, ValueKey.STATUS_FILE, currentStatusLove)
    }

    fun updateNameAndAge(context: Context, who: Int, name: String, age: Long){
        val currentStatusLove = MediaHelper.readModelFromFile<StatusLoveTestModel>(context, ValueKey.STATUS_FILE) ?: StatusLoveTestModel()
        if (who == ValueKey.ME){
            currentStatusLove.together.myName = name
            currentStatusLove.together.myAge = age
        }else{
            currentStatusLove.together.yourName = name
            currentStatusLove.together.yourAge = age
        }
        MediaHelper.writeModelToFile(context, ValueKey.STATUS_FILE, currentStatusLove)
        updateValueStatusLoveTest(context)
    }

    fun updateDateStart(context: Context, date: Long){
        val currentStatusLove = MediaHelper.readModelFromFile<StatusLoveTestModel>(context, ValueKey.STATUS_FILE) ?: StatusLoveTestModel()
        currentStatusLove.countLove.startDate = date
        MediaHelper.writeModelToFile(context, ValueKey.STATUS_FILE, currentStatusLove)
    }

    fun updateSortSlogan(context: Context, shortSlogan: String){
        val currentStatusLove = MediaHelper.readModelFromFile<StatusLoveTestModel>(context, ValueKey.STATUS_FILE) ?: StatusLoveTestModel()
        currentStatusLove.countLove.shotSlogan = shortSlogan
        MediaHelper.writeModelToFile(context, ValueKey.STATUS_FILE, currentStatusLove)
    }

    fun updateLongSlogan(context: Context){
        val currentStatusLove = MediaHelper.readModelFromFile<StatusLoveTestModel>(context, ValueKey.STATUS_FILE) ?: StatusLoveTestModel()
        currentStatusLove.longSlogan = DataLocal.getLongSlogan()
        MediaHelper.writeModelToFile(context, ValueKey.STATUS_FILE, currentStatusLove)
    }
    // ------------------------------------------------------------------------------------------------------------------

}