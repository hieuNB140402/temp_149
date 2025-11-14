package com.love.compatibility.ui.home

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.rateApp
import com.love.compatibility.core.helper.LanguageHelper
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.core.utils.state.RateState
import com.love.compatibility.databinding.ActivityHomeBinding
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.showToast
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.utils.key.RequestKey
import com.love.compatibility.ui.home.adapter.HomeAdapter
import com.love.compatibility.ui.home.fragment.HomeFragment
import com.love.compatibility.ui.home.fragment.StatusFragment
import com.love.compatibility.ui.permission.PermissionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.system.exitProcess

class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels()
    private val permissionViewModel: PermissionViewModel by viewModels()
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(this) }


    override fun setViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        countAccess()
        initVpg()
        viewModel.setStatusFragment(ValueKey.TYPE_HOME)
    }

    override fun dataObservable() {

        // statusFragment
        lifecycleScope.launch {
            viewModel.statusFragment.collect { status ->
                if (status == -1) return@collect
                val fragment = if (status == ValueKey.TYPE_HOME) {
                    binding.tvHome.setTextColor(getColor(R.color.purple_FD))
                    binding.imvHome.setColorFilter(getColor(R.color.purple_FD))
                    binding.tvStatus.setTextColor(getColor(R.color.gray_67))
                    binding.imvStatus.setColorFilter(getColor(R.color.gray_67))
                    ValueKey.TYPE_HOME
                } else {
                    binding.tvHome.setTextColor(getColor(R.color.gray_67))
                    binding.imvHome.setColorFilter(getColor(R.color.gray_67))
                    binding.tvStatus.setTextColor(getColor(R.color.purple_FD))
                    binding.imvStatus.setColorFilter(getColor(R.color.purple_FD))
                    ValueKey.TYPE_STATUS
                }
                if (fragment != binding.vpgHome.currentItem) {
                    binding.vpgHome.setCurrentItem(fragment, true)
                }
            }
        }
    }

    override fun viewListener() {
        binding.apply {
            btnHome.setOnSingleClick { viewModel.setStatusFragment(ValueKey.TYPE_HOME) }
            btnStatus.setOnSingleClick { viewModel.setStatusFragment(ValueKey.TYPE_STATUS) }
        }
    }


    override fun initActionBar() {}

    @SuppressLint("MissingSuperCall", "GestureBackNavigation")
    override fun onBackPressed() {
        if (!sharePreference.getIsRate(this) && sharePreference.getCountBack() % 2 == 0) {
            rateApp(sharePreference) { state ->
                when (state) {
                    RateState.LESS3 -> {
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(1000)
                            exitProcess(0)
                        }
                    }

                    RateState.GREATER3 -> {}
                    RateState.CANCEL -> {
                        lifecycleScope.launch {
                            sharePreference.setCountBack(sharePreference.getCountBack() + 1)
                            withContext(Dispatchers.Main) {
                                delay(1000)
                                exitProcess(0)
                            }
                        }
                    }
                }
            }
        } else {
            exitProcess(0)
        }
    }


    private fun initVpg() {
        binding.vpgHome.apply {
            adapter = homeAdapter
            isUserInputEnabled = false
            setCurrentItem(0, true)
        }
    }

    private fun updateText() {
        binding.apply {
            tvHome.text = strings(R.string.home)
            tvStatus.text = strings(R.string.status)
        }
    }

    override fun onRestart() {
        super.onRestart()
        LanguageHelper.setLocale(this)
        updateText()
    }

    private fun countAccess() {
        sharePreference.setCountBack(sharePreference.getCountBack() + 1)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val granted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        if (requestCode == RequestKey.STORAGE_PERMISSION_CODE && granted) {
            permissionViewModel.updateStorageGranted(sharePreference, true)
            showToast(R.string.granted_storage)
        } else {
            permissionViewModel.updateStorageGranted(sharePreference, false)
        }
    }
}