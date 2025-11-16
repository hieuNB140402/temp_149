package com.love.compatibility.ui.show_result_love_test

import androidx.lifecycle.ViewModel
import com.love.compatibility.core.utils.key.ValueKey

class ShowResultLoveTestViewModel : ViewModel() {

    var percentName = 1
    var percentDate = 1
    var percentFinger = 1

    fun updatePercent(percentAll: String) {
        val split = percentAll.split(ValueKey.TYPE_SPIT)
        percentName = split.first().toInt()
        percentDate = split[1].toInt()
        percentFinger = split.last().toInt()
    }
}