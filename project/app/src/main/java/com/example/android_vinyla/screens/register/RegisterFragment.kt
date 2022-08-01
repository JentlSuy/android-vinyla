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

            var step1ValidationCorrect = true

            if (!viewModel.checkEmail(binding.registerEmailInput.text.toString())) {
                binding.registerEmailInput.setError("Incorrect email!")
                step1ValidationCorrect = false
            }


            if (!viewModel.checkPassword(binding.registerPasswordInput.text.toString())) {
                binding.registerPasswordInput.setError("Password must meet the requirements of at least 8 characters, 1 lowercase, 1 uppercase, 1 numeric & 1 special character!")
                step1ValidationCorrect = false
            }

            // DEVELOPMENT PURPOSES!!!
            step1ValidationCorrect = true
            // TODO

            if (step1ValidationCorrect) {
                binding.registerBackButton.visibility = View.GONE
                binding.registerEmailInput.visibility = View.GONE
                binding.registerPasswordInput.visibility = View.GONE
                binding.registerFirstnameInput.visibility = View.VISIBLE
                binding.registerLastnameInput.visibility = View.VISIBLE
                binding.registerNextButton.visibility = View.GONE
                binding.registerBackButtonStep2.visibility = View.VISIBLE
                binding.registerSignupButton.visibility = View.VISIBLE
                binding.registerPasswordResetWarning.visibility = View.GONE
            }
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
            binding.registerPasswordResetWarning.visibility = View.VISIBLE
        }
        binding.registerSignupButton.setOnClickListener {
            var step2ValidationCorrect = true

            if (!viewModel.checkName(binding.registerFirstnameInput.text.toString(), true)) {
                step2ValidationCorrect = false
                binding.registerFirstnameInput.setError("Cannot be empty!")
            }
            if (!viewModel.checkName(binding.registerLastnameInput.text.toString(), false)) {
                step2ValidationCorrect = false
                binding.registerLastnameInput.setError("Cannot be empty!")
            }

            // DEVELOPMENT PURPOSES!!!
            step2ValidationCorrect = true
            // TODO

            if (step2ValidationCorrect) {
                // TODO
                Log.i(
                    "RegisterFragment",
                    "SignUp button was pressed! Creating account...\n${viewModel.email.value}\n${viewModel.password.value}\n${viewModel.firstname.value}\n${viewModel.lastname.value}"
                )
                findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
            }
        }
        return binding.root
    }
}