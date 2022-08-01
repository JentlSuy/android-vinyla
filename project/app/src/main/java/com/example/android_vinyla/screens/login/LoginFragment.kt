package com.example.android_vinyla.screens.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.set
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android_vinyla.R
import com.example.android_vinyla.databinding.FragmentLoginBinding
import com.example.android_vinyla.databinding.FragmentWelcomeBinding

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    private lateinit var binding: FragmentLoginBinding
    private var _correctPassword = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // INITIALIZING BINDING
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.loginViewModel = viewModel

        binding.lifecycleOwner = this

        // DEV TODO
        binding.loginEmailInput.setText("suy.jentl@gmail.com")
        binding.loginPasswordInput.setText("P@ssword1999")
        // DEV

        binding.loginBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_welcomeFragment)
        }

        binding.loginButton.setOnClickListener {
            //Log.i("LoginFragment: ", "TRYING TO LOGIN")
            if (viewModel.logIn(
                    binding.loginEmailInput.text.toString(),
                    binding.loginPasswordInput.text.toString()
                )
            ) {
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            } else {
                binding.loginPasswordInput.setError("The email address or password is incorrect.")
            }
        }
        return binding.root
    }
}