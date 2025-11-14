package com.love.compatibility.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.helper.DateHelper
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.core.utils.state.GetStatusLoveState
import com.love.compatibility.databinding.ActivitySplashBinding
import com.love.compatibility.ui.home.HomeViewModel
import com.love.compatibility.ui.intro.IntroActivity
import com.love.compatibility.ui.language.LanguageActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    var intentActivity: Intent? = null
    val homeViewHolder: HomeViewModel by viewModels()

    override fun setViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != null && intent.action.equals(
                Intent.ACTION_MAIN
            )
        ) {
            finish(); return
        }

        intentActivity = if (sharePreference.getIsFirstLang()) {
            Intent(this, LanguageActivity::class.java)
        } else {
            Intent(this, IntroActivity::class.java)
        }

        lifecycleScope.launch {
            if (!sharePreference.getIsFirstEdit()){
                homeViewHolder.updateLongSlogan(this@SplashActivity)
                startActivity(intentActivity)
            }
            delay(1500)
            startActivity(intentActivity)
        }
    }

    override fun dataObservable() {}

    override fun viewListener() {
    }

    override fun initText() {}

    override fun initActionBar() {}

    @SuppressLint("GestureBackNavigation", "MissingSuperCall")
    override fun onBackPressed() {
    }
}