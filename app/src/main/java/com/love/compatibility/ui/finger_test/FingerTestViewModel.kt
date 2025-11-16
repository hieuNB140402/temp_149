package com.love.compatibility.ui.finger_test

import android.animation.ObjectAnimator
import androidx.lifecycle.ViewModel

class FingerTestViewModel : ViewModel() {
    var leftAnimator: ObjectAnimator? = null
    var rightAnimator: ObjectAnimator? = null

    var isLeftHold = false
    var isRightHold = false

    var isLoveTest = false

    fun updateIsLoveTest(status: Boolean){
        isLoveTest = status
    }

    // Gọi ở bất cứ đâu trong class
    fun pauseScan() {
        leftAnimator?.pause()
        rightAnimator?.pause()
    }

    fun pauseLeftScan() {
        leftAnimator?.pause()
    }

    fun pauseRightScan() {
        rightAnimator?.pause()
    }

    fun resumeScan() {
        leftAnimator?.resume()
        rightAnimator?.resume()
    }

    fun updateIsLeftHold(isHold: Boolean){
        isLeftHold = isHold
    }

    fun updateIsRightHold(isHold: Boolean){
        isRightHold = isHold
    }


}