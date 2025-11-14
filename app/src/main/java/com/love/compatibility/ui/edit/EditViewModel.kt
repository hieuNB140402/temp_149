package com.love.compatibility.ui.edit

import android.content.Context
import androidx.lifecycle.ViewModel
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.utils.DataLocal
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.data.model.status.TogetherModel
import kotlinx.coroutines.Job

class EditViewModel: ViewModel() {
    var isFirstEdit = false

    var whoEdit = ValueKey.ME

    var togetherModel = TogetherModel()

    var titleChangeJob: Job? = null

    fun updateIsFirstEdit(isFirst: Boolean){
        isFirstEdit = isFirst
    }

    fun setStatusLoveTestDefault(context: Context){
        val statusLoveTestDefault = DataLocal.getStatusLoveTestDefault(context)
        MediaHelper.writeModelToFile(context, ValueKey.STATUS_FILE,statusLoveTestDefault)
    }

    fun updateWhoEdit(who: Int) {
        whoEdit = who
    }

    fun updateTogetherModel(together: TogetherModel){
        togetherModel = together
    }
}