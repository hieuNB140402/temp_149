package com.love.compatibility.ui.edit

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.checkPermissions
import com.love.compatibility.core.extensions.goToSettings
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.hideNavigation
import com.love.compatibility.core.extensions.hideSoftKeyboard
import com.love.compatibility.core.extensions.loadImageGlide
import com.love.compatibility.core.extensions.requestPermission
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.showToast
import com.love.compatibility.core.extensions.startIntent
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.DateHelper
import com.love.compatibility.core.helper.LanguageHelper
import com.love.compatibility.core.helper.StringHelper
import com.love.compatibility.core.helper.UnitHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.RequestKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.core.utils.state.GetStatusLoveState
import com.love.compatibility.databinding.ActivityEditBinding
import com.love.compatibility.dialog.EditChooseDialog
import com.love.compatibility.dialog.EditNameDialog
import com.love.compatibility.ui.choose_image.ChooseImageActivity
import com.love.compatibility.ui.home.HomeActivity
import com.love.compatibility.ui.home.HomeViewModel
import com.love.compatibility.ui.permission.PermissionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.YearMonth
import java.util.Calendar
import kotlin.getValue

class EditActivity : BaseActivity<ActivityEditBinding>() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val viewModel: EditViewModel by viewModels()
    private val permissionViewModel: PermissionViewModel by viewModels()

    override fun setViewBinding(): ActivityEditBinding {
        return ActivityEditBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        viewModel.updateIsFirstEdit(sharePreference.getIsFirstEdit())
        homeViewModel.updateValueStatusLoveTest(this)
        initData()
    }

    override fun dataObservable() {
        binding.apply {
            lifecycleScope.launch {
                homeViewModel.statusLoveTest.collect { state ->
                    when (state) {
                        is GetStatusLoveState.Loading -> {
                            showLoading()
                        }

                        is GetStatusLoveState.Error -> {
                            dismissLoading(true)
                            showErrorDialog()
                        }

                        is GetStatusLoveState.Success -> {
                            dismissLoading(true)
                            withContext(Dispatchers.Main) {
                                val model = state.value
                                viewModel.updateTogetherModel(model.together)

                                loadImageGlide(this@EditActivity, model.together.avatarPathMe, imvMe)
                                loadImageGlide(this@EditActivity, model.together.avatarPathYou, imvYou)

                                edtTitle.setText(if (model.countLove.shotSlogan == "") "" else model.countLove.shotSlogan)

                                tvNameMe.text = model.together.myName
                                tvNameYou.text = model.together.yourName

                                tvAgeMe.text =
                                    if (model.together.myAge != -1L) DateHelper.calculateYearsFromLong(model.together.myAge)
                                        .toString() else getString(R.string.age)
                                tvAgeYou.text =
                                    if (model.together.yourAge != -1L) DateHelper.calculateYearsFromLong(model.together.yourAge)
                                        .toString() else getString(R.string.age)

                                edtTitle.setText(model.countLove.shotSlogan)
                                calendarView.date = model.countLove.startDate
                            }
                        }
                    }
                }
            }
        }
    }

    override fun viewListener() {
        binding.apply {
            actionBar.apply {
                btnActionBarRight.setOnSingleClick { handleSave() }
            }
            main.setOnClickListener { hideSoftKeyboard() }
            lnlMain.setOnClickListener { hideSoftKeyboard() }

            imvMe.setOnSingleClick { handleChooseType(ValueKey.ME) }
            imvYou.setOnSingleClick { handleChooseType(ValueKey.YOU) }

            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val c = Calendar.getInstance()
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, month)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                c.set(Calendar.HOUR_OF_DAY, 0)
                c.set(Calendar.MINUTE, 0)
                c.set(Calendar.SECOND, 0)
                c.set(Calendar.MILLISECOND, 0)

                homeViewModel.updateDateStart(this@EditActivity, c.timeInMillis)
            }

            edtTitle.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                    viewModel.titleChangeJob?.cancel()
                    viewModel.titleChangeJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        homeViewModel.updateSortSlogan(this@EditActivity, StringHelper.sanitizeFileName(binding.edtTitle.text.toString().trim()))
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            })
        }
    }

    override fun initActionBar() {
        binding.actionBar.apply {
            tvCenter.text = strings(R.string.edit)
            tvCenter.visible()
            tvCenter.select()

            btnActionBarRight.setImageResource(R.drawable.ic_done)
            btnActionBarRight.visible()
        }
    }

    private fun initData() {
        if (viewModel.isFirstEdit) {
            viewModel.setStatusLoveTestDefault(this)
        }
        homeViewModel.updateValueStatusLoveTest(this)
    }

    private fun handleSave() {
        hideSoftKeyboard()
        if (viewModel.isFirstEdit) {
            sharePreference.setIsFirstEdit(false)
            startIntent(HomeActivity::class.java)
            finishAffinity()
        } else {
            handleBackLeftToRight()
        }
    }

    private fun checkStoragePermission() {
        if (checkPermissions(permissionViewModel.getStoragePermissions())) {
            handleChangeAvatar()
        } else if (permissionViewModel.needGoToSettings(sharePreference, true)) {
            goToSettings()
        } else {
            requestPermission(
                permissionViewModel.getStoragePermissions(), RequestKey.STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun handleChooseType(who: Int) {
        viewModel.updateWhoEdit(who)
        val dialog = EditChooseDialog(this)
        LanguageHelper.setLocale(this)
        dialog.show()
        dialog.onDismissClick = {
            dialog.dismiss()
            hideNavigation(true)
        }

        dialog.onAvatarClick = {
            dialog.dismiss()
            hideNavigation(true)
            checkStoragePermission()
        }

        dialog.onNameClick = {
            dialog.dismiss()
            hideNavigation(true)
            handleEditName()
        }
    }

    private fun handleChangeAvatar() {
        val intent = Intent(this, ChooseImageActivity::class.java)
        intent.putExtra(
            IntentKey.INTENT_KEY,
            if (viewModel.whoEdit == ValueKey.ME) ValueKey.TYPE_AVATAR_ME else ValueKey.TYPE_AVATAR_YOU
        )
        intent.putExtra(IntentKey.RATIO_KEY, UnitHelper.getRatioString(binding.imvMe))
        val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }

    private fun handleEditName() {
        val (name, age) = if (viewModel.whoEdit == ValueKey.ME) {
            viewModel.togetherModel.myName to viewModel.togetherModel.myAge
        } else {
            viewModel.togetherModel.yourName to viewModel.togetherModel.yourAge
        }
        val dialog = EditNameDialog(this, name, age)
        LanguageHelper.setLocale(this)
        dialog.show()
        dialog.onDismissClick = {
            dialog.dismiss()
            hideNavigation(true)
        }

        dialog.onDoneClick = { name, age ->
            dialog.dismiss()
            hideNavigation(true)
            homeViewModel.updateNameAndAge(this@EditActivity, viewModel.whoEdit, name, age)
        }
    }

    @SuppressLint("MissingSuperCall", "GestureBackNavigation")
    override fun onBackPressed() {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val granted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        if (requestCode == RequestKey.STORAGE_PERMISSION_CODE && granted) {
            permissionViewModel.updateStorageGranted(sharePreference, true)
            handleChangeAvatar()
        } else {
            permissionViewModel.updateStorageGranted(sharePreference, false)
        }
    }

    override fun onRestart() {
        super.onRestart()
        homeViewModel.updateValueStatusLoveTest(this)
    }
}