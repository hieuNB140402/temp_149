package com.love.compatibility.ui.age_test

import androidx.lifecycle.ViewModel

class DateOfBirthViewModel : ViewModel() {
    var isLoveTest = false

    var myBirthDay = -1L
    var yourBirthDay = -1L

    fun updateIsLoveTest(status: Boolean){
        isLoveTest = status
    }

    fun updateMyBirthDay(date: Long){
        myBirthDay = date
    }

    fun updateYourBirthDay(date: Long){
        yourBirthDay = date
    }
}