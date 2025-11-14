package com.love.compatibility.data.model.status

data class TogetherModel(
    var avatarPathMe: String = "Me",
    var avatarPathYou: String = "You",
    var myName: String = "",
    var yourName: String = "",
    var myAge: Long = -1,
    var yourAge: Long = -1
)
