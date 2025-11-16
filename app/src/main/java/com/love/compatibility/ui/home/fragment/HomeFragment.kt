package com.love.compatibility.ui.home.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseFragment
import com.love.compatibility.core.extensions.checkPermissions
import com.love.compatibility.core.extensions.eLog
import com.love.compatibility.core.extensions.goToSettings
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.extensions.loadImageGlide
import com.love.compatibility.core.extensions.requestPermission
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.startIntent
import com.love.compatibility.core.extensions.startIntentRightToLeft
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.helper.AssetHelper
import com.love.compatibility.core.helper.LanguageHelper
import com.love.compatibility.core.helper.UnitHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.RequestKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.core.utils.state.GetStringState
import com.love.compatibility.databinding.FragmentHomeBinding
import com.love.compatibility.dialog.ChooseQuantityQuestionDialog
import com.love.compatibility.ui.SettingsActivity
import com.love.compatibility.ui.age_test.DateOfBirthActivity
import com.love.compatibility.ui.choose_image.ChooseImageActivity
import com.love.compatibility.ui.finger_test.FingerTestActivity
import com.love.compatibility.ui.home.HomeActivity
import com.love.compatibility.ui.home.HomeViewModel
import com.love.compatibility.ui.name_test.NameTestActivity
import com.love.compatibility.ui.permission.PermissionViewModel
import com.love.compatibility.ui.question_test.QuestionTestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.jvm.java

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val permissionViewModel: PermissionViewModel by activityViewModels()
    private val viewModel: HomeViewModel by activityViewModels()

    override fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {}

    override fun viewListener() {
        val homeActivity = (activity as HomeActivity)

        binding.apply {
            btnChooseImage.setOnSingleClick { checkStoragePermission() }
            btnSettings.setOnSingleClick { homeActivity.startIntentRightToLeft(SettingsActivity::class.java) }
            btnLoveTest.setOnSingleClick { homeActivity.startIntentRightToLeft(NameTestActivity::class.java, true) }
            btnNameTest.setOnSingleClick { homeActivity.startIntentRightToLeft(NameTestActivity::class.java, false) }
            btnDateTest.setOnSingleClick { homeActivity.startIntentRightToLeft(DateOfBirthActivity::class.java, false) }
            btnFingerprintTest.setOnSingleClick {
                homeActivity.startIntentRightToLeft(
                    FingerTestActivity::class.java,
                    false
                )
            }
            btnQuestionTest.setOnSingleClick { handleChooseQuestionTest() }
        }
    }

    private fun checkStoragePermission() {
        val homeActivity = (activity as HomeActivity)
        if (homeActivity.checkPermissions(permissionViewModel.getStoragePermissions())) {
            handleChooseImage()
        } else if (permissionViewModel.needGoToSettings(homeActivity.sharePreference, true)) {
            homeActivity.goToSettings()
        } else {
            homeActivity.requestPermission(
                permissionViewModel.getStoragePermissions(), RequestKey.STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun handleChooseImage() {
        val homeActivity = (activity as HomeActivity)
        val intent = Intent(homeActivity, ChooseImageActivity::class.java)
        intent.putExtra(IntentKey.INTENT_KEY, ValueKey.TYPE_OUTSTANDING_HOME)
        intent.putExtra(IntentKey.RATIO_KEY, UnitHelper.getRatioString(binding.imvOutStanding))
        val options = ActivityOptions.makeCustomAnimation(homeActivity, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }

    override fun dataObservable() {
        val homeActivity = (activity as HomeActivity)
//        imageOutStanding
        lifecycleScope.launch {
            viewModel.imageOutStanding.collect { state ->
                when (state) {
                    is GetStringState.Loading -> {}
                    is GetStringState.Error -> {
                        homeActivity.eLog("imageOutStanding: ${state.exception}")
                    }

                    is GetStringState.Success -> {
                        loadImageGlide(homeActivity, state.path, binding.imvOutStanding, false)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateText()
        viewModel.loadImageOutStanding(requireActivity())
    }

    private fun updateText() {
        val homeActivity = (activity as HomeActivity)
        binding.apply {
            tvTitle.text = homeActivity.strings(R.string.love_test)
            tvBtn1.text = homeActivity.strings(R.string.love_test)
            tvBtn2.text = homeActivity.strings(R.string.name_test)
            tvBtn3.text = homeActivity.strings(R.string.fingerprint_test)
            tvBtn4.text = homeActivity.strings(R.string.date_of_birth_test)
            tvBtn5.text = homeActivity.strings(R.string.question_test)
            tvBtn6.text = homeActivity.strings(R.string.test_now)
        }
    }

    private fun handleChooseQuestionTest() {
        val homeActivity = (activity as HomeActivity)
        val dialog = ChooseQuantityQuestionDialog(homeActivity)
        LanguageHelper.setLocale(homeActivity)
        dialog.show()
        dialog.onDismissClick = {
            dialog.dismiss()
            homeActivity.hideNavigation(true)
        }
        dialog.onChooseQuantity = { quantity ->
            dialog.dismiss()
            lifecycleScope.launch {
                homeActivity.showLoading()
                viewModel.loadQuestionsList(homeActivity, homeActivity.sharePreference, quantity)
                homeActivity.dismissLoading(true)
                homeActivity.startIntentRightToLeft(QuestionTestActivity::class.java)
            }
        }
    }

}