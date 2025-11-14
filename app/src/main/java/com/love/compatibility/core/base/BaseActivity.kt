package com.love.compatibility.core.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity

import androidx.viewbinding.ViewBinding
import com.love.compatibility.R
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.helper.LanguageHelper
import com.love.compatibility.core.helper.SharePreferenceHelper
import com.love.compatibility.core.helper.SoundHelper
import com.love.compatibility.dialog.ConfirmDialog
import com.love.compatibility.dialog.WaitingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected abstract fun setViewBinding(): T

    protected abstract fun initView()

    protected abstract fun viewListener()

    open fun dataObservable() {}

    open fun initText() {}

    protected abstract fun initActionBar()

    open fun initAds() {}

    protected val loadingDialog: WaitingDialog by lazy {
        WaitingDialog(this)
    }
    val sharePreference: SharePreferenceHelper by lazy {
        SharePreferenceHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.setLocale(this)
        _binding = setViewBinding()
        setContentView(binding.root)
        setUpUI()

    }

    private fun setUpUI() {
        initView()
        if (!SoundHelper.isSoundNotNull(R.raw.touch)) {
            SoundHelper.loadSound(this, R.raw.touch)
        }
        initAds()
        dataObservable()
        viewListener()
        initText()
        initActionBar()
    }

    override fun onResume() {
        super.onResume()
        hideNavigation(true)
    }

    @SuppressLint("MissingSuperCall", "GestureBackNavigation")
    override fun onBackPressed() {
        handleBackLeftToRight()
    }

    suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            if (loadingDialog.isShowing.not()) {
                LanguageHelper.setLocale(this@BaseActivity)
                loadingDialog.show()
            }
        }
    }

    suspend fun dismissLoading(isBlack: Boolean = false) {
        withContext(Dispatchers.Main) {
            if (loadingDialog.isShowing) {
                loadingDialog.dismiss()
                hideNavigation(isBlack)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    suspend fun showErrorDialog(){
        withContext(Dispatchers.Main){
            val dialog = ConfirmDialog(this@BaseActivity, R.string.error, R.string.an_error_occurred, isError = true)
            LanguageHelper.setLocale(this@BaseActivity)
            dialog.show()
            dialog.onYesClick = {
                dialog.dismiss()
                finish()
            }
        }
    }
}
