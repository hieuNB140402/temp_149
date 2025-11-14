package com.love.compatibility.data.model.status

data class StatusLoveTestModel(
    var together: TogetherModel = TogetherModel( "Me", "You", "", "",-1, -1),
    var countLove: CountLoveModel = CountLoveModel("" ,-1),
    var templatePath: String = "",
    var longSlogan: Int = -1
)
