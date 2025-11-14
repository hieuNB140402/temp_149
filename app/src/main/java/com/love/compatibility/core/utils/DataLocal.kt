package com.love.compatibility.core.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.love.compatibility.R
import com.love.compatibility.data.model.IntroModel
import com.love.compatibility.data.model.LanguageModel
import com.facebook.shimmer.Shimmer
import com.love.compatibility.core.utils.key.AssetsKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.data.model.status.CountLoveModel
import com.love.compatibility.data.model.status.StatusLoveTestModel
import com.love.compatibility.data.model.status.TogetherModel
import kotlin.random.Random

object DataLocal {
    val shimmer =
        Shimmer.AlphaHighlightBuilder().setDuration(1800).setBaseAlpha(0.7f).setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT).setAutoStart(true).build()

    var lastClickTime = 0L
    var currentDate = ""
    var isConnectInternet = MutableLiveData<Boolean>()
    var isFailBaseURL = false

    fun getLanguageList(): ArrayList<LanguageModel> {
        return arrayListOf(
            LanguageModel("hi", "Hindi", R.drawable.ic_flag_hindi),
            LanguageModel("es", "Spanish", R.drawable.ic_flag_spanish),
            LanguageModel("fr", "French", R.drawable.ic_flag_french),
            LanguageModel("en", "English", R.drawable.ic_flag_english),
            LanguageModel("pt", "Portuguese", R.drawable.ic_flag_portugeese),
            LanguageModel("in", "Indonesian", R.drawable.ic_flag_indo),
            LanguageModel("de", "German", R.drawable.ic_flag_germani),
        )
    }

    val itemIntroList = listOf(
        IntroModel(R.drawable.img_intro_1, R.string.title_1),
        IntroModel(R.drawable.img_intro_2, R.string.title_2),
        IntroModel(R.drawable.img_intro_3, R.string.title_3)
    )

    fun getStatusLoveTestDefault(context: Context) : StatusLoveTestModel {
        return StatusLoveTestModel(
            together = TogetherModel(
                avatarPathMe = AssetsKey.AVATAR_DEFAULT_ASSET,
                avatarPathYou = AssetsKey.AVATAR_DEFAULT_ASSET,
                myName = context.getString(R.string.me),
                yourName = context.getString(R.string.you),
                myAge = -1L,
                yourAge = -1L
            ),
            countLove = CountLoveModel(
                shotSlogan = "",
                startDate = System.currentTimeMillis()
            ),
            templatePath = AssetsKey.BACKGROUND_DEFAULT_ASSET,
            longSlogan = getLongSlogan()
        )
    }

    fun getLongSlogan(): Int {
        val longSlogan = arrayListOf<Int>(
            R.string.long_slogan_1,
            R.string.long_slogan_2,
            R.string.long_slogan_3,
            R.string.long_slogan_4,
            R.string.long_slogan_5,
            R.string.long_slogan_6,
            R.string.long_slogan_7,
            R.string.long_slogan_8,
            R.string.long_slogan_9,
            R.string.long_slogan_10
        )
        val positionRandom = Random.nextInt(0,longSlogan.size -1)
        return longSlogan[positionRandom]
    }
}