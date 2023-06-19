package com.khtn.freebies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.khtn.freebies.R
import com.khtn.freebies.databinding.FragmentSignUpBinding
import com.khtn.freebies.helper.*
import com.khtn.freebies.module.User
import com.khtn.freebies.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(), View.OnClickListener {
    private lateinit var binding : FragmentSignUpBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        binding.ibExitSignUp.setOnClickListener(this@SignUpFragment)
        binding.tvGoToLogin.setOnClickListener(this@SignUpFragment)
        binding.btnSignUp.setOnClickListener(this@SignUpFragment)

        binding.txtInputEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.txtInputEmail.error = null
            else if (binding.txtInputEmail.editText?.text.isNullOrEmpty())
                binding.txtInputEmail.error = getText(R.string.email_not_empty)
        }
        binding.txtInputName.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.txtInputName.error = null
            else if (binding.txtInputName.editText?.text.isNullOrEmpty())
                binding.txtInputName.error = getText(R.string.name_not_empty)
        }
        binding.txtInputPassword.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.txtInputPassword.error = null
            else if (binding.txtInputPassword.editText?.text.isNullOrEmpty())
                binding.txtInputPassword.error = getText(R.string.password_not_empty)
        }
        binding.txtInputPasswordAgain.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.txtInputPasswordAgain.error = null
            else if (binding.txtInputPasswordAgain.editText?.text.isNullOrEmpty())
                binding.txtInputPasswordAgain.error = getText(R.string.password_not_empty)
        }
    }

    private fun observer() {
        authViewModel.register.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.btnSignUp.text = ""
                    binding.btnSignUp.isEnabled = false
                    binding.progressSignUp.show()
                }

                is UiState.Failure -> {
                    binding.btnSignUp.text = getText(R.string.sign_up)
                    binding.btnSignUp.isEnabled = true
                    binding.progressSignUp.hide()
                    requireContext().toast(state.error)
                }

                is UiState.Success -> {
                    binding.btnSignUp.text = getText(R.string.sign_up)
                    binding.btnSignUp.isEnabled = true
                    binding.progressSignUp.hide()
                    requireContext().toast(state.data)
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                }
            }
        }
    }

    private fun getUserObj(): User {
        return User(
            id = "",
            name = binding.txtInputName.editText?.text.toString(),
            email = binding.txtInputEmail.editText?.text.toString(),
            avatar = ""
        )
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.txtInputName.editText?.text.isNullOrEmpty()) {
            isValid = false
            binding.txtInputName.error = getText(R.string.name_not_empty)
        }

        if (binding.txtInputEmail.editText?.text.isNullOrEmpty()) {
            isValid = false
            binding.txtInputEmail.error = getText(R.string.email_not_empty)
        } else {
            if (!binding.txtInputEmail.editText?.text.toString().isValidEmail()) {
                isValid = false
                binding.txtInputEmail.error = getText(R.string.email_invalid)
            }
        }

        if (binding.txtInputPassword.editText?.text.isNullOrEmpty()) {
            isValid = false
            binding.txtInputPassword.error = getText(R.string.password_not_empty)
        } else {
            if (!binding.txtInputPassword.editText?.text.toString().isValidPassword()) {
                isValid = false
                binding.txtInputPassword.error = getText(R.string.password_invalid)
            }
        }

        if (binding.txtInputPasswordAgain.editText?.text.isNullOrEmpty()) {
            isValid = false
            binding.txtInputPasswordAgain.error = getText(R.string.password_not_empty)
        } else {
            val str1 : String = binding.txtInputPassword.editText?.text.toString()
            val str2 : String = binding.txtInputPasswordAgain.editText?.text.toString()
            if (str1 != str2) {
                isValid = false
                binding.txtInputPasswordAgain.error = getText(R.string.password_not_same)
            }
        }

        return isValid
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_exit_sign_up, R.id.tv_go_to_login -> findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)

            R.id.btn_sign_up -> if (validation()) authViewModel.register(
                password = binding.txtInputPassword.editText?.text.toString(),
                getUserObj()
            )
        }
    }
}