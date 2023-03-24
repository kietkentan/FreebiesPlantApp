package com.khtn.freebies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.khtn.freebies.R
import com.khtn.freebies.databinding.FragmentSignUpBinding
import com.khtn.freebies.helper.*
import com.khtn.freebies.module.User
import com.khtn.freebies.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var binding : FragmentSignUpBinding
    private val viewModel: AuthViewModel by viewModels()

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
        binding.ibExitSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.btnSignUp.text = ""
                    binding.progressSignUp.show()
                }
                is UiState.Failure -> {
                    binding.btnSignUp.text = getText(R.string.sign_up)
                    binding.progressSignUp.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnSignUp.text = getText(R.string.sign_up)
                    binding.progressSignUp.hide()
                    toast(state.data)
                    //findNavController().navigate(R.id.action_registerFragment_to_home_navigation)
                }
            }
        }
    }

    fun getUserObj(): User {
        return User(
            id = "",
            name = binding.txtInputName.editText?.text.toString(),
            email = binding.txtInputEmail.editText?.text.toString(),
            avatar = ""
        )
    }

    fun validation(): Boolean {
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
            if (!binding.txtInputPasswordAgain.editText?.text?.equals(binding.txtInputPassword.editText?.text)!!) {
                isValid = false
                binding.txtInputPasswordAgain.error = getText(R.string.password_not_same)
            }
        }
        return isValid
    }
}