package com.love.compatibility.ui.choose_image

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseActivity
import com.love.compatibility.core.extensions.checkPermissions
import com.love.compatibility.core.extensions.eLog
import com.love.compatibility.core.extensions.goToSettings
import com.love.compatibility.core.extensions.gone
import com.love.compatibility.core.extensions.handleBackLeftToRight
import com.love.compatibility.core.extensions.requestPermission
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.BitmapHelper
import com.love.compatibility.core.helper.MediaHelper
import com.love.compatibility.core.utils.key.IntentKey
import com.love.compatibility.core.utils.key.PermissionKey
import com.love.compatibility.core.utils.key.RequestKey
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.core.utils.state.LoadImageState
import com.love.compatibility.core.utils.state.SaveState
import com.love.compatibility.data.model.choose_image.AllImageModel
import com.love.compatibility.data.model.choose_image.ImageModel
import com.love.compatibility.databinding.ActivityChooseImageBinding
import com.love.compatibility.ui.choose_image.adapter.AllImageAdapter
import com.love.compatibility.ui.choose_image.adapter.ImageSubAdapter
import com.love.compatibility.ui.home.HomeViewModel
import com.love.compatibility.ui.permission.PermissionViewModel
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.collections.get
import kotlin.getValue

class ChooseImageActivity : BaseActivity<ActivityChooseImageBinding>() {
    private val permissionViewModel: PermissionViewModel by viewModels()
    private val viewModel: ChooseImageViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val adapterAllImage by lazy { AllImageAdapter(this) }
    private val adapterImageSub by lazy { ImageSubAdapter(this) }


    override fun setViewBinding() = ActivityChooseImageBinding.inflate(LayoutInflater.from(this))

    override fun initView() {
        initRcv()
        viewModel.updateTypeChooseImage(intent.getIntExtra(IntentKey.INTENT_KEY, ValueKey.TYPE_OUTSTANDING_HOME))
        viewModel.updateRatio(intent.getStringExtra(IntentKey.RATIO_KEY) ?: "36:16.7")
        checkStoragePermission()

    }

    override fun dataObservable() {
//        folderNameSelected
        lifecycleScope.launch {
            viewModel.folderNameSelected.collect { folderName ->
                binding.tvFolder.text = folderName
            }
        }

//        isOpenMore
        lifecycleScope.launch {
            viewModel.isOpenMore.collect { isOpen ->
                if (isOpen) {
                    binding.layoutAllImage.visible()
                    binding.btnMore.rotation = 180f
                } else {
                    binding.layoutAllImage.gone()
                    binding.btnMore.rotation = 0f
                }
            }
        }
    }

    override fun viewListener() {
        binding.apply {
            btnBack.setOnSingleClick { handleBackLeftToRight() }
            tvFolder.setOnClickListener { viewModel.setOpenMore() }
            btnMore.setOnClickListener { viewModel.setOpenMore() }
        }
        adapterAllImage.onItemClick = { folder, position -> handleFolderSelected(folder, position) }
        adapterImageSub.onItemClick = { image, position -> handleCutout(image.image) }
    }

    override fun initActionBar() {}

    override fun initText() {
        binding.apply {
            tvFolder.select()
        }
    }

    private fun checkStoragePermission() {
        if (checkPermissions(permissionViewModel.getStoragePermissions())) {
            initData()
        } else if (permissionViewModel.needGoToSettings(sharePreference, true)) {
            goToSettings()
        } else {
            requestPermission(permissionViewModel.getStoragePermissions(), RequestKey.STORAGE_PERMISSION_CODE)
        }
    }

    private fun initData() {
//        getAllImageFoldersWithImages
        lifecycleScope.launch(Dispatchers.IO) {
            MediaHelper.getAllImageFoldersWithImages(this@ChooseImageActivity).collect { state ->
                when (state) {
                    is LoadImageState.Loading -> {
                        showLoading()
                    }

                    is LoadImageState.Error -> {
                        eLog("getAllImageFoldersWithImages: ${state.exception}")
                        dismissLoading(true)
                        showErrorDialog()
                    }

                    is LoadImageState.Success -> {
                        withContext(Dispatchers.Main) {
                            viewModel.setAllImageList(state.list, adapterAllImage)
                            viewModel.setSubImageList(state.list[viewModel.positionAllImage].listImage, adapterImageSub)
                            viewModel.setFolderNameSelected(state.list[viewModel.positionAllImage].nameFolder)
                            binding.tvNoItem.isVisible = state.list.isEmpty()
                            dismissLoading(true)
                        }
                    }
                }
            }
        }
    }

    private fun initRcv() {
        binding.apply {
            rcvAllImage.apply {
                adapter = adapterAllImage
                itemAnimator = null
            }
            rcvImageSub.apply {
                adapter = adapterImageSub
                itemAnimator = null
            }
        }
    }

    private fun handleFolderSelected(folder: AllImageModel, position: Int) {
        binding.apply {
            viewModel.setOpenMore(false)
            viewModel.setFolderNameSelected(folder.nameFolder)
            viewModel.updatePositionAllImage(position)
            viewModel.setSubImageList(folder.listImage, adapterImageSub)
        }
    }

    override fun onRestart() {
        super.onRestart()
        checkStoragePermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val granted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        if (requestCode == RequestKey.STORAGE_PERMISSION_CODE && granted) {
            permissionViewModel.updateStorageGranted(sharePreference, true)
            initData()
        } else {
            permissionViewModel.updateStorageGranted(sharePreference, false)
        }
    }

    private fun handleCutout(path: String) {
        val sourceUri: Uri = Uri.fromFile(File(path))

        val destinationUri = StringBuilder(UUID.randomUUID().toString()).append(".png").toString()

        val options = UCrop.Options().apply {
            setToolbarColor(getColor(this@ChooseImageActivity, R.color.transparent))
            setStatusBarColor(getColor(this@ChooseImageActivity, R.color.black))
            setHideBottomControls(false)    // Hiển thị thanh công cụ chỉnh tỷ lệ
            setFreeStyleCropEnabled(false)   // Cho phép người dùng chỉnh khung cắt tự do
            setCropGridColor(getColor(R.color.black))
            setCropFrameColor(getColor(R.color.purple_86))
            setCropGridStrokeWidth(1)
            setCropFrameStrokeWidth(5)
            setLogoColor(getColor(this@ChooseImageActivity, R.color.purple_86))
            setActiveControlsWidgetColor(getColor(this@ChooseImageActivity, R.color.purple_86))
        }

        UCrop.of(sourceUri, Uri.fromFile(File(cacheDir, destinationUri)))
            .withAspectRatio(viewModel.widthRatio.toFloat(), viewModel.heightRatio.toFloat())
            .withMaxResultSize(1000, 1000)
            .withOptions(options).start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                handleSaveImage(resultUri)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            eLog("cropError: $cropError")
            cropError?.printStackTrace()
        }
    }

    private fun handleSaveImage(resultUri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = BitmapHelper.uriToBitmap(this@ChooseImageActivity, resultUri)
            if (bitmap == null) {
                return@launch
            }
            MediaHelper.saveBitmapToInternalStorage(this@ChooseImageActivity, viewModel.albumSave, bitmap)
                .collect { state ->
                    when (state) {
                        is SaveState.Loading -> {
                            showLoading()
                        }

                        is SaveState.Error -> {
                            dismissLoading(true)
                            showErrorDialog()
                        }

                        is SaveState.Success -> {
                            when(viewModel.typeChooseImage){
                                ValueKey.TYPE_AVATAR_ME -> {
                                    homeViewModel.updateAvatarMePath(this@ChooseImageActivity, state.path)
                                }
                                ValueKey.TYPE_AVATAR_YOU -> {
                                    homeViewModel.updateAvatarYouPath(this@ChooseImageActivity, state.path)
                                }
                                ValueKey.TYPE_TEMPLATE -> {
                                    homeViewModel.updateTemplatePath(this@ChooseImageActivity, state.path)
                                }
                                else -> {}
                            }
                            deleteTempFolder(state.path)
                            dismissLoading(true)
                            handleBackLeftToRight()
                        }
                    }
                }
        }
    }

    suspend fun deleteTempFolder(exception: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dataTemp = MediaHelper.getImageInternal(this@ChooseImageActivity, viewModel.albumSave)
            if (dataTemp.isNotEmpty()) {
                dataTemp.forEach {
                    val file = File(it)
                    if (file.absolutePath != exception) {
                        file.delete()
                    }
                }
            }
        }
    }
}
