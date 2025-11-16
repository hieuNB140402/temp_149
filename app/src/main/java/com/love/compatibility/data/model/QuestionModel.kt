package com.love.compatibility.data.model

data class QuestionModel(
    val question: String,
    val answerList: ArrayList<String> = arrayListOf(),
    var positionMyAnswer: Int = -1,
    var positionYouAnswer: Int = -1
)