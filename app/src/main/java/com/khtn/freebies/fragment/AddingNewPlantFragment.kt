package com.khtn.freebies.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ImageListAdapter
import com.khtn.freebies.databinding.FragmentAddingNewPlantBinding
import com.khtn.freebies.helper.AppConstant
import com.khtn.freebies.helper.ImageUtils
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.addChipAddingPlant
import com.khtn.freebies.helper.createBottomDialog
import com.khtn.freebies.helper.dpToPx
import com.khtn.freebies.helper.forEachChildView
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.hideKeyBoardNotClearFocus
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.listener.ChipGroupListener
import com.khtn.freebies.viewmodel.AddingNewPlantViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddingNewPlantFragment: Fragment(), ChipGroupListener {
    private lateinit var binding: FragmentAddingNewPlantBinding
    private val viewModel: AddingNewPlantViewModel by viewModels()
    private var positionPictureSelected = -1
    private var screen = 0
    private var maxWidth: Int = 0

    private val adapterImage by lazy {
        ImageListAdapter(
            onItemClicked = { uri, position ->
                handleImageReviewCliked(uri, position)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddingNewPlantBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        screen = requireContext().applicationContext.resources.displayMetrics.widthPixels
        maxWidth = (screen*0.7 - 65.dpToPx).toInt()

        updateUI()
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val list = mutableListOf<String>()
            if (requestCode == ImageUtils.TAKE_PHOTO_ADDING_NEW) {
                val uri = ImageUtils.getPhotoUri(data).toString()
                if (uri.isNotEmpty())
                    list.add(uri)
            } else if (requestCode == ImageUtils.FROM_GALLERY_ADDING_NEW) {
                if (data.clipData != null) {
                    val count: Int = data.clipData!!.itemCount

                    for (i in 0 until count) {
                        val tempUri = data.clipData!!.getItemAt(i).uri.toString()
                        list.add(tempUri)
                    }
                } else if (data.data != null) {
                    val tempUri = data.data.toString()
                    if (tempUri.isNotEmpty())
                        list.add(tempUri)
                }
            }
            viewModel.updateImageUris(list)
            if (viewModel.getImageUris().isNotEmpty())
                viewModel.setErrorListImage("")
            setupImageReview()
        }
    }

    private fun observe() {
        viewModel.imageUris.observe(viewLifecycleOwner) {
            adapterImage.updateList(it)
        }

        viewModel.listSpecies.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val list = it.map { spe -> spe.second }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, list)
                binding.autoCompleteSpecies.setAdapter(adapter)
                val firstItem = list.firstOrNull() ?: ""
                viewModel.speciesId.value = it.firstOrNull()?.first ?: ""
                viewModel.species.value = firstItem
                binding.autoCompleteSpecies.setText(firstItem, false)
            }
        }

        viewModel.listKingdom.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, it)
                binding.autoCompleteKingdom.setAdapter(adapter)
                val firstItem = it.firstOrNull() ?: ""
                viewModel.kingdom.value = firstItem
                binding.autoCompleteKingdom.setText(firstItem, false)
            }
        }

        viewModel.listFamily.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, it)
                binding.autoCompleteFamily.setAdapter(adapter)
                val firstItem = it.firstOrNull() ?: ""
                viewModel.family.value = firstItem
                binding.autoCompleteFamily.setText(firstItem, false)
            }
        }

        viewModel.errorListImage.observe(viewLifecycleOwner) {
            if (it.isNullOrBlank())
                binding.errorListImage.hide()
            else {
                binding.errorListImage.show()
                binding.errorListImage.text = it
            }
        }

        viewModel.addNewPlant.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    this.view?.forEachChildView { it.isEnabled = false }
                    binding.layoutLoading.show()
                }

                is UiState.Success -> {
                    this.view?.forEachChildView { it.isEnabled = true }
                    binding.layoutLoading.hide()
                    handleBack()
                }

                is UiState.Failure -> {
                    requireActivity().toast(getString(R.string.error_when_add_plant))
                    this.view?.forEachChildView { it.isEnabled = true }
                    binding.layoutLoading.hide()
                }
            }
        }
    }

    private fun updateUI() {
        viewModel.updateImageUris(arguments?.getStringArrayList(AppConstant.LIST_IMAGE)?.toMutableList() ?: mutableListOf())
        viewModel.getListSpecies()
        viewModel.getListKingdom()
        viewModel.getListFamily()
        setupImageReview()

        binding.recListReviewProducts.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recListReviewProducts.adapter = adapterImage
        binding.ivSeeMore.alpha = 0.8F

        binding.txtInputPlantName.apply {
            editText!!.addTextChangedListener {
                this.error =
                    if (editText!!.text.toString().isNotEmpty()) ""
                    else getString(R.string.name_not_empty)
                viewModel.namePlant.value = editText!!.text.toString()
            }
        }

        binding.txtInputDes.apply {
            editText!!.addTextChangedListener {
                this.error =
                    if (editText!!.text.toString().isNotEmpty()) ""
                    else getString(R.string.name_not_empty)
                viewModel.description.value = editText!!.text.toString()
            }
        }

        binding.ivSeeMore.setOnClickListener {
            if (binding.recListReviewProducts.visibility == View.VISIBLE) {
                binding.ivSeeMore.setImageResource(R.drawable.ic_see_more_open)
                binding.recListReviewProducts.hide()
            } else {
                binding.ivSeeMore.setImageResource(R.drawable.ic_see_more_close)
                binding.recListReviewProducts.show()
            }
        }

        binding.autoCompleteSpecies.apply {
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.species.value = viewModel.listSpecies.value?.get(position)?.second ?: ""
                viewModel.speciesId.value = viewModel.listSpecies.value?.get(position)?.first ?: ""
                binding.menuSpecies.error = ""
            }
            setOnFocusChangeListener { _, b ->
                if (b) requireActivity().hideKeyBoardNotClearFocus()
            }
        }

        binding.autoCompleteKingdom.apply {
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.kingdom.value = viewModel.listKingdom.value?.get(position) ?: ""
            }
            setOnFocusChangeListener { _, b ->
                if (b) requireActivity().hideKeyBoardNotClearFocus()
            }
        }

        binding.autoCompleteFamily.apply {
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.family.value = viewModel.listFamily.value?.get(position) ?: ""
            }
            setOnFocusChangeListener { _, b ->
                if (b) requireActivity().hideKeyBoardNotClearFocus()
            }
        }

        binding.btnCancel.setOnClickListener {
            handleBack()
        }

        binding.ibEditReviewProduct.setOnClickListener {
            showPopupMenuPicture()
        }

        binding.ivSelectPhoto.setOnClickListener {
            ImageUtils.askPermission(this, ImageUtils.IN_ADDING_NEW)
        }

        binding.btnAddSave.setOnClickListener {
            validation()
        }

        binding.recListReviewProducts.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val width = binding.recListReviewProducts.width
                if (width > 0) {
                    binding.recListReviewProducts.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    if (width > maxWidth) {
                        val layoutParam = binding.recListReviewProducts.layoutParams
                        layoutParam.width = maxWidth
                        binding.recListReviewProducts.layoutParams = layoutParam
                        val manager: LinearLayoutManager = binding.recListReviewProducts.layoutManager as LinearLayoutManager
                        manager.scrollToPositionWithOffset(0, 0)
                    }
                }
            }
        })

        chipGroup()
    }

    @Suppress("DEPRECATION")
    private fun handleBack() {
        requireActivity().onContentChanged()
        requireActivity().onBackPressed()
    }

    private fun validation() {
        var isValid = true
        if (binding.txtInputPlantName.editText?.text.toString().isBlank()) {
            binding.txtInputPlantName.error = getString(R.string.name_not_empty)
            isValid = false
        }

        if (binding.txtInputDes.editText?.text.toString().isBlank()) {
            binding.txtInputDes.error = getString(R.string.des_not_empty)
            isValid = false
        }

        if (viewModel.getImageUris().isEmpty()) {
            viewModel.setErrorListImage(getString(R.string.list_image_not_empty))
            isValid = false
        }

        if (isValid) {
            handleUploadNewPlant()
        }
    }

    private fun chipGroup() {
        binding.tagsAddingPlant.addChipAddingPlant("New tag", this)
    }

    private fun handleUploadNewPlant() {
        viewModel.uploadPlant(requireContext())
    }

    private fun handleImageReviewCliked(uri: String, position: Int) {
        ImageUtils.loadImage(binding.ivItemReviewProduct, uri)
        positionPictureSelected = position
    }

    private fun setupImageReview() {
        positionPictureSelected = if (viewModel.getImageUris().isNotEmpty()) 0 else -1
        checkImageView()
    }

    private fun checkSeeMoreLayout() {
        if (viewModel.getImageUris().size > 1) {
            binding.layoutSeeMorePicture.show()
            val it = 86.dpToPx * viewModel.getImageUris().size
            val layoutParam = binding.recListReviewProducts.layoutParams
            layoutParam.width = if (it > maxWidth) maxWidth else it
            binding.recListReviewProducts.layoutParams = layoutParam
        }
        else binding.layoutSeeMorePicture.hide()
    }

    private fun checkImageView() {
        checkSeeMoreLayout()
        if (positionPictureSelected < 0) {
            binding.ibEditReviewProduct.hide()
            binding.ivSelectPhoto.show()
            binding.ivItemReviewProduct.setImageResource(R.color.background_shimmer)
        } else {
            binding.ibEditReviewProduct.show()
            binding.ivSelectPhoto.hide()
            viewModel.getImageUris()[positionPictureSelected].let {
                ImageUtils.loadImage(binding.ivItemReviewProduct, it)
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    fun showPopupMenuPicture() {
        val popupMenu = PopupMenu(requireContext(), binding.ibEditReviewProduct)
        popupMenu.menuInflater.inflate(R.menu.in_image_product_menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.m_add_picture -> { ImageUtils.askPermission(this, ImageUtils.IN_ADDING_NEW) }

                R.id.m_remove_picture -> removePicture()

                R.id.m_remove_all_picture -> removeAllPicture()
            }
            true
        }
    }

    private fun removePicture() {
        viewModel.removeAt(positionPictureSelected)
        positionPictureSelected = if (viewModel.getImageUris().isNotEmpty()) 0 else -1
        checkImageView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeAllPicture() {
        viewModel.clearImageUris()
        adapterImage.notifyDataSetChanged()
        positionPictureSelected = -1
        checkImageView()
    }

    override fun onNewClickListener() {
        openDialogEnterTag()
    }

    @Suppress("UNUSED_EXPRESSION", "DEPRECATION")
    private fun openDialogEnterTag() {
        val dialog = requireContext().createBottomDialog(R.layout.dialog_enter_new_chip)
        val edt = dialog.findViewById<EditText>(R.id.edt_enter_tags)

        edt.apply {
            onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }
            }
            dialog.setOnDismissListener {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
            dialog.setOnShowListener {
                this.requestFocus()
            }
            setOnKeyListener { view, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val tag = (view as EditText).text.toString()
                    if (tag.isNotEmpty()) {
                        viewModel.addChip(tag)
                        binding.tagsAddingPlant.addChipAddingPlant(tag, this@AddingNewPlantFragment)
                    }
                    dialog.dismiss()
                    true
                }
                false
            }
        }
        dialog.show()
    }

    override fun onRemoveClickListener(position: Int) {
        viewModel.removeChip(position)
    }
}