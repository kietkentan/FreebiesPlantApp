package com.khtn.freebies.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.khtn.freebies.databinding.DialogImageResourceSheetBinding
import com.khtn.freebies.helper.ImageOptions
import com.khtn.freebies.helper.setTransparentBackground

interface SheetListener {
    fun selectedItem(index: Int)
}

class DialogImageResourceSheet: BottomSheetDialogFragment() {
    private lateinit var binding: DialogImageResourceSheetBinding
    private lateinit var listener: SheetListener

    companion object{
        fun newInstance(bundle: Bundle): DialogImageResourceSheet {
            val fragment = DialogImageResourceSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    fun addListener(listener: SheetListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogImageResourceSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setTransparentBackground()

        binding.tvCamera.setOnClickListener {
            listener.selectedItem(index = ImageOptions.TAKE_PHOTO)
            dismiss()
        }

        binding.tvGallery.setOnClickListener {
            listener.selectedItem(index = ImageOptions.CHOSE_GALLERY)
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            listener.selectedItem(index = ImageOptions.CANCEL)
            dismiss()
        }
    }
}