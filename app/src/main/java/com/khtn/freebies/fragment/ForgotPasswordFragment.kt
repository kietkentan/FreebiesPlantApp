package com.khtn.freebies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.khtn.freebies.R
import com.khtn.freebies.databinding.FragmentForgotPasswordBinding
import com.khtn.freebies.helper.*
import com.khtn.freebies.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment(), View.OnClickListener {
    private lateinit var binding : FragmentForgotPasswordBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        binding.ibExitForgotPassword.setOnClickListener(this@ForgotPasswordFragment)
        binding.tvGoToSignup1.setOnClickListener(this@ForgotPasswordFragment)
        binding.tvBackToLogin.setOnClickListener(this@ForgotPasswordFragment)
        binding.btnSendEmailReset.setOnClickListener(this@ForgotPasswordFragment)

        binding.txtInputEmailForgot.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.txtInputEmailForgot.error = null
            else if (binding.txtInputEmailForgot.editText?.text.isNullOrEmpty())
                binding.txtInputEmailForgot.error = getText(R.string.email_not_empty)
        }
    }

    private fun observer(){
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.btnSendEmailReset.text = ""
                    binding.btnSendEmailReset.isEnabled = false
                    binding.progressForgot.show()
                }

                is UiState.Failure -> {
                    binding.btnSendEmailReset.text = getText(R.string.change_password)
                    binding.btnSendEmailReset.isEnabled = true
                    binding.progressForgot.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.btnSendEmailReset.text = getText(R.string.change_password)
                    binding.btnSendEmailReset.isEnabled = true
                    binding.progressForgot.hide()
                    toast(state.data)
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.txtInputEmailForgot.editText?.text.isNullOrEmpty()) {
            isValid = false
            binding.txtInputEmailForgot.error = getText(R.string.email_not_empty)
        } else {
            if (!binding.txtInputEmailForgot.editText?.text.toString().isValidEmail()) {
                isValid = false
                binding.txtInputEmailForgot.error = getText(R.string.email_invalid)
            }
        }

        return isValid
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_exit_forgot_password, R.id.tv_back_to_login -> findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)

            R.id.tv_go_to_signup_1 -> findNavController().navigate(R.id.action_forgotPasswordFragment_to_signUpFragment)

            R.id.btn_send_email_reset -> if (validation()) viewModel.forgotPassword(binding.txtInputEmailForgot.editText?.text.toString())
        }
    }
}