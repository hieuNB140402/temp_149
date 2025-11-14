package com.love.compatibility.ui.home.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseFragment
import com.love.compatibility.core.extensions.animateBurstOut
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.extensions.loadImageGlide
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.DateHelper
import com.love.compatibility.core.helper.LanguageHelper
import com.love.compatibility.core.helper.UnitHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.core.utils.state.GetStatusLoveState
import com.love.compatibility.data.model.status.StatusLoveTestModel
import com.love.compatibility.databinding.FragmentStatusBinding
import com.love.compatibility.dialog.template.TemplateDialog
import com.love.compatibility.ui.choose_image.ChooseImageActivity
import com.love.compatibility.ui.edit.EditActivity
import com.love.compatibility.ui.home.HomeActivity
import com.love.compatibility.ui.home.HomeViewModel
import com.love.compatibility.ui.home.adapter.StatusAdapter
import kotlinx.coroutines.launch

class StatusFragment : BaseFragment<FragmentStatusBinding>() {
    private val viewModel: HomeViewModel by activityViewModels()
    private val tabAdapter: StatusAdapter by lazy { StatusAdapter(requireActivity()) }

    override fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentStatusBinding {
        return FragmentStatusBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        binding.imvHeart.animateBurstOut()
        initVpg()
    }

    override fun dataObservable() {
        binding.apply {
            lifecycleScope.launch {
                viewModel.statusLoveTest.collect { state ->
                    when (state) {
                        is GetStatusLoveState.Loading -> {}
                        is GetStatusLoveState.Error -> {}
                        is GetStatusLoveState.Success -> {
                            val model = state.value
                            Glide.with(requireActivity()).load(state.value.templatePath).into(imvBackground)


                            Glide.with(requireActivity()).load(model.together.avatarPathMe).into(imvMe)
                            Glide.with(requireActivity()).load(model.together.avatarPathYou).into(imvYou)

                            tvNameMe.text = model.together.myName
                            tvNameYou.text = model.together.yourName

                            tvAgeMe.text =
                                if (model.together.myAge != -1L) DateHelper.calculateYearsFromLong(model.together.myAge)
                                    .toString() else getString(R.string.age)
                            tvAgeYou.text =
                                if (model.together.yourAge != -1L) DateHelper.calculateYearsFromLong(model.together.yourAge)
                                    .toString() else getString(R.string.age)

                            tabAdapter.submitList(model)
                        }
                    }
                }
            }
        }
    }

    override fun viewListener() {
        binding.apply {
            actionBar.apply {
                btnActionBarNextToRight.setOnSingleClick { handleChooseTemplate() }
                btnActionBarRight.setOnSingleClick { handleEdit() }
            }
        }
    }

    override fun initActionBar() {
        binding.actionBar.apply {
            btnActionBarRight.setImageResource(R.drawable.ic_edit)
            btnActionBarRight.visible()

            btnActionBarNextToRight.setImageResource(R.drawable.ic_choose_image)
            btnActionBarNextToRight.visible()
        }
    }

    private fun initVpg() {
        binding.vpgTab.apply {
            adapter = tabAdapter
        }
        binding.dotsIndicator.attachTo(binding.vpgTab)
    }

    private fun handleChooseTemplate() {
        val homeActivity = (activity as HomeActivity)
        val dialog = TemplateDialog(homeActivity)
        LanguageHelper.setLocale(homeActivity)
        dialog.show()

        dialog.onDismissClick = {
            dialog.dismiss()
            homeActivity.hideNavigation(true)
        }

        dialog.onItemClick = { path ->
            viewModel.updateTemplatePath(requireActivity(), path)
            viewModel.updateValueStatusLoveTest(requireActivity())
        }

        dialog.onChooseImageClick = { handleChooseImage() }
    }



    private fun handleChooseImage() {
        val homeActivity = (activity as HomeActivity)
        val intent = Intent(homeActivity, ChooseImageActivity::class.java)
        intent.putExtra(IntentKey.INTENT_KEY, ValueKey.TYPE_TEMPLATE)
        intent.putExtra(IntentKey.RATIO_KEY, UnitHelper.getRatioString(binding.imvBackground))
        val options = ActivityOptions.makeCustomAnimation(homeActivity, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }
    private fun handleEdit(){
        val homeActivity = (activity as HomeActivity)
        val intent = Intent(homeActivity, EditActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(homeActivity, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }
    override fun onStart() {
        super.onStart()
        viewModel.updateValueStatusLoveTest(requireActivity())
    }
}