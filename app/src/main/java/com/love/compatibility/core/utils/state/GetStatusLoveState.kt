package com.love.compatibility.core.utils.state

import com.love.compatibility.data.model.status.StatusLoveTestModel

sealed class GetStatusLoveState {
    data class Success(val value: StatusLoveTestModel) : GetStatusLoveState()
    data class Error(val exception: Exception) : GetStatusLoveState()
    object Loading : GetStatusLoveState()
}