package com.khtn.freebies.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.khtn.freebies.R
import com.khtn.freebies.databinding.FragmentLoginBinding
import com.khtn.freebies.helper.UiState
import com.khtn.freebies.helper.hide
import com.khtn.freebies.helper.show
import com.khtn.freebies.helper.toast
import com.khtn.freebies.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener{
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
    }

    private fun observer(){
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.btnLogin.text = ""
                    binding.progressLogin.show()
                }
                is UiState.Failure -> {
                    binding.btnLogin.text = getText(R.string.login)
                    binding.progressLogin.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnLogin.text = getText(R.string.login)
                    binding.progressLogin.hide()
                    toast(state.data)
                    //findNavController().navigate(R.id.action_loginFragment_to_home_navigation)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_exit_login -> activity?.finish()

            R.id.btn_sign_up -> findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }


}