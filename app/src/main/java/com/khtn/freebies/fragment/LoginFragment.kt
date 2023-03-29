package com.khtn.freebies.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.khtn.freebies.R
import com.khtn.freebies.activity.MainActivity
import com.khtn.freebies.databinding.FragmentLoginBinding
import com.khtn.freebies.helper.*
import com.khtn.freebies.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment(), View.OnClickListener{
    private lateinit var binding : FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        binding.ibExitLogin.setOnClickListener(this@LoginFragment)
        binding.btnLogin.setOnClickListener(this@LoginFragment)
        binding.tvForgotPassword.setOnClickListener(this@LoginFragment)
        binding.tvGoToSignup2.setOnClickListener(this@LoginFragment)

        binding.txtInputUsername.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.txtInputUsername.error = null
            else if (binding.txtInputUsername.editText?.text.isNullOrEmpty())
                binding.txtInputUsername.error = getText(R.string.email_not_empty)
        }
        binding.txtInputPassword.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.txtInputPassword.error = null
            else if (binding.txtInputPassword.editText?.text.isNullOrEmpty())
                binding.txtInputPassword.error = getText(R.string.password_not_empty)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            if (user != null) startMainActivity()
        }
    }

    private fun observer(){
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.btnLogin.text = ""
                    binding.btnLogin.isEnabled = false
                    binding.progressLogin.show()
                }

                is UiState.Failure -> {
                    binding.btnLogin.text = getText(R.string.login)
                    binding.btnLogin.isEnabled = true
                    binding.progressLogin.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.btnLogin.text = getText(R.string.login)
                    binding.btnLogin.isEnabled = true
                    binding.progressLogin.hide()
                    startMainActivity()
                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.txtInputUsername.editText?.text.isNullOrEmpty()) {
            isValid = false
            binding.txtInputUsername.error = getText(R.string.email_not_empty)
        } else {
            if (!binding.txtInputUsername.editText?.text.toString().isValidEmail()) {
                isValid = false
                binding.txtInputUsername.error = getText(R.string.email_invalid)
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

        return isValid
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_exit_login -> activity?.finish()

            R.id.btn_login -> if (validation()) viewModel.login(
                email = binding.txtInputUsername.editText?.text.toString(),
                password = binding.txtInputPassword.editText?.text.toString()
            )

            R.id.tv_forgot_password -> findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)

            R.id.tv_go_to_signup_2 -> findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun startMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}