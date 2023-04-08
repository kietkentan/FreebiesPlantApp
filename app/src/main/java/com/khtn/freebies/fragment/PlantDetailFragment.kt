package com.khtn.freebies.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.khtn.freebies.R
import com.khtn.freebies.adapter.ImageListAdapter
import com.khtn.freebies.databinding.FragmentPlantDetailBinding
import com.khtn.freebies.helper.addChip
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.module.Plant
import com.squareup.picasso.Picasso

class PlantDetailFragment : Fragment() {
    private lateinit var binding: FragmentPlantDetailBinding
    private var objPlant: Plant? = null
    private var imageUris: MutableList<Uri> = arrayListOf()
    private val adapterImage by lazy {
        ImageListAdapter(
            onItemClicked = {uri -> Picasso.get().load(uri).into(binding.ivItemReview)}
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()
    }

    @Suppress("DEPRECATION")
    private fun updateUI() {
        objPlant = arguments?.getParcelable("plant")
        binding.recListReview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recListReview.adapter = adapterImage

        binding.ivSeeMore.alpha = 0.8F

        objPlant?.let { it ->
            setupImageReview(it.images)
            addTags(it.tags)

            binding.tvNamePlantDetail.text = it.name
            binding.tvDescriptionPlantDetail.text = it.description

            if (it.kingdom.isNotEmpty())
                binding.tvKingdomDetail.text = it.kingdom
            else binding.layoutKingdomDetail.hide()

            if (it.family.isNotEmpty())
                binding.tvFamilyDetail.text = it.family
            else binding.layoutFamilyDetail.hide()
        }

        binding.ibExitPlantDetail.setOnClickListener {
            requireActivity().onContentChanged()
            requireActivity().onBackPressed()
        }
        binding.ivSeeMore.setOnClickListener {
            if (binding.recListReview.visibility == View.VISIBLE) {
                binding.ivSeeMore.setImageResource(R.drawable.ic_see_more_open)
                binding.recListReview.hide()
            } else {
                binding.ivSeeMore.setImageResource(R.drawable.ic_see_more_close)
                binding.recListReview.show()
            }
        }
        binding.ivFavorite.setOnClickListener {
            //
        }
    }

    private fun addTags(tags: MutableList<String>) {
        if (tags.size > 0) {
            binding.tagsPlantDetail.apply {
                removeAllViews()
                tags.forEachIndexed { _, tag -> addChip(tag, true) }
            }
        }
    }

    private fun setupImageReview(list: List<String>) {
        imageUris = list.map { it.toUri() }.toMutableList()
        imageUris.reverse()
        adapterImage.updateList(imageUris)
        val manager: LinearLayoutManager = binding.recListReview.layoutManager as LinearLayoutManager
        manager.scrollToPositionWithOffset(imageUris.size - 1, 0)
        imageUris[0].let { Picasso.get().load(it).into(binding.ivItemReview) }
        binding.ivSeeMore.visibility = if (imageUris.size > 1) View.VISIBLE else View.GONE
    }
}