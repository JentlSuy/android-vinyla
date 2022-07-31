package com.example.android_vinyla.screens.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.android_vinyla.R
import com.example.android_vinyla.databinding.FragmentLoginBinding
import com.example.android_vinyla.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // INITIALIZING BINDING
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false
        )
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding.registerViewModel = viewModel

        binding.lifecycleOwner = this

        // SETTING ONCLICKLISTENERS
        binding.registerBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_welcomeFragment)
        }
        binding.registerNextButton.setOnClickListener {
            //            email = binding.
            //            binding.registerEmailInput.setError()
            viewModel.checkEmail(binding.registerEmailInput.text.toString())
            viewModel.checkPassword(binding.registerPasswordInput.text.toString())
            binding.registerBackButton.visibility = View.GONE
            binding.registerEmailInput.visibility = View.GONE
            binding.registerPasswordInput.visibility = View.GONE
            binding.registerFirstnameInput.visibility = View.VISIBLE
            binding.registerLastnameInput.visibility = View.VISIBLE
            binding.registerNextButton.visibility = View.GONE
            binding.registerBackButtonStep2.visibility = View.VISIBLE
            binding.registerSignupButton.visibility = View.VISIBLE
        }
        binding.registerBackButtonStep2.setOnClickListener {
            binding.registerBackButton.visibility = View.VISIBLE
            binding.registerEmailInput.visibility = View.VISIBLE
            binding.registerPasswordInput.visibility = View.VISIBLE
            binding.registerFirstnameInput.visibility = View.GONE
            binding.registerLastnameInput.visibility = View.GONE
            binding.registerNextButton.visibility = View.VISIBLE
            binding.registerSignupButton.visibility = View.GONE
            binding.registerBackButtonStep2.visibility = View.GONE
        }
        binding.registerSignupButton.setOnClickListener {
            Log.i("RegisterFragment", "SignUp button was pressed! Creating account...")
        }
        return binding.root
    }
}