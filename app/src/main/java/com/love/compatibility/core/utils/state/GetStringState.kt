package com.love.compatibility.core.utils.state

sealed class GetStringState {
    data class Success(val path: String) : GetStringState()
    data class Error(val exception: Exception) : GetStringState()
    object Loading : GetStringState()
}