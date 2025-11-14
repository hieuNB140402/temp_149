package com.love.compatibility.ui.name_test

import androidx.lifecycle.ViewModel

class NameTestViewModel : ViewModel() {
    var isLoveTest = false

    fun updateIsLoveTest(status: Boolean){
        isLoveTest = status
    }
}