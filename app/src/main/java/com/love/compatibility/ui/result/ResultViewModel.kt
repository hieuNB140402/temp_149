package com.love.compatibility.ui.result

import androidx.lifecycle.ViewModel
import com.love.compatibility.core.utils.key.ValueKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResultViewModel : ViewModel() {

    private val _typeStart = MutableStateFlow<Int>(-1)
    val typeStart = _typeStart.asStateFlow()

    var dataTopText = ""

    fun setTypeStart(type: Int) {
        _typeStart.value = type
    }

    fun updateDataText(text: String) {
        dataTopText = text
    }
}