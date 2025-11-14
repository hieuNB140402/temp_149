package com.love.compatibility.ui.intro

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.utils.DataLocal
import com.love.compatibility.databinding.ActivityIntroBinding
import com.love.compatibility.ui.home.HomeActivity
import com.love.compatibility.ui.permission.PermissionActivity
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.ui.edit.EditActivity
import kotlin.system.exitProcess

class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    private val introAdapter by lazy { IntroAdapter(this) }

    override fun setViewBinding(): ActivityIntroBinding {
        return ActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        initVpg()
    }

    override fun viewListener() {
        binding.btnNext.setOnSingleClick { handleNext() }

    }

    override fun initText() {}

    override fun initActionBar() {}

    private fun initVpg() {
        binding.apply {
            binding.vpgTutorial.adapter = introAdapter
            binding.dotsIndicator.attachTo(binding.vpgTutorial)
            introAdapter.submitList(DataLocal.itemIntroList)
        }
    }

    private fun handleNext() {
        binding.apply {
            val nextItem = binding.vpgTutorial.currentItem + 1
            if (nextItem < DataLocal.itemIntroList.size) {
                vpgTutorial.setCurrentItem(nextItem, true)
            } else {
                val intent =
                    if (sharePreference.getIsFirstPermission()) {
                        Intent(this@IntroActivity, PermissionActivity::class.java)
                    } else if (sharePreference.getIsFirstEdit()) {
                        Intent(this@IntroActivity, EditActivity::class.java)
                    } else {
                        Intent(this@IntroActivity, HomeActivity::class.java)
                    }
                startActivity(intent)
                finishAffinity()
            }
        }
    }

    @SuppressLint("MissingSuperCall", "GestureBackNavigation")
    override fun onBackPressed() {
        exitProcess(0)
    }
}