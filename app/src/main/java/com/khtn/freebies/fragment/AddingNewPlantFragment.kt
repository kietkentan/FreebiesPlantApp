package com.khtn.freebies.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ImageListAdapter
import com.khtn.freebies.databinding.FragmentAddingNewPlantBinding
import com.khtn.freebies.helper.AppConstant
import com.khtn.freebies.helper.ImageUtils
import com.khtn.freebies.helper.dpToPx
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show

class AddingNewPlantFragment : Fragment() {
    private lateinit var binding: FragmentAddingNewPlantBinding
    private var imageUris: MutableList<String> = arrayListOf()
    private var positionPictureSelected = -1
    var screen = 0
    var maxWidth: Int = 0

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

    private fun observe() {

    }

    @Suppress("DEPRECATION")
    private fun updateUI() {
        imageUris = arguments?.getStringArrayList(AppConstant.LIST_IMAGE)?.toMutableList() ?: mutableListOf()
        setupImageReview()

        binding.recListReviewProducts.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recListReviewProducts.adapter = adapterImage
        binding.ivSeeMore.alpha = 0.8F

        binding.ivSeeMore.setOnClickListener {
            if (binding.recListReviewProducts.visibility == View.VISIBLE) {
                binding.ivSeeMore.setImageResource(R.drawable.ic_see_more_open)
                binding.recListReviewProducts.hide()
            } else {
                binding.ivSeeMore.setImageResource(R.drawable.ic_see_more_close)
                binding.recListReviewProducts.show()
            }
        }

        binding.btnCancel.setOnClickListener {
            requireActivity().onContentChanged()
            requireActivity().onBackPressed()
        }

        binding.ibEditReviewProduct.setOnClickListener {
            showPopupMenuPicture()
        }

        binding.ivSelectPhoto.setOnClickListener {
            ImageUtils.askPermission(this, ImageUtils.IN_ADDING_NEW)
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
    }

    private fun handleImageReviewCliked(uri: String, position: Int) {
        ImageUtils.loadImage(binding.ivItemReviewProduct, uri)
        positionPictureSelected = position
    }

    private fun setupImageReview() {
        positionPictureSelected = if (imageUris.size > 0) 0 else -1
        checkImageView()
    }

    private fun checkSeeMoreLayout() {
        if (imageUris.size > 1) {
            binding.layoutSeeMorePicture.show()
            val it = 86.dpToPx * imageUris.size
            val layoutParam = binding.recListReviewProducts.layoutParams
            layoutParam.width = if (it > maxWidth) maxWidth else it
            binding.recListReviewProducts.layoutParams = layoutParam
        }
        else binding.layoutSeeMorePicture.hide()
    }

    private fun checkImageView() {
        adapterImage.updateList(imageUris)
        checkSeeMoreLayout()
        if (positionPictureSelected < 0) {
            binding.ibEditReviewProduct.hide()
            binding.ivSelectPhoto.show()
            binding.ivItemReviewProduct.setImageResource(R.color.background_shimmer)
        } else {
            binding.ibEditReviewProduct.show()
            binding.ivSelectPhoto.hide()
            imageUris[positionPictureSelected].let {
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
        imageUris.removeAt(positionPictureSelected)
        positionPictureSelected = if (imageUris.size > 0) 0 else -1
        checkImageView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeAllPicture() {
        imageUris.clear()
        adapterImage.notifyDataSetChanged()
        positionPictureSelected = -1
        checkImageView()
    }
}