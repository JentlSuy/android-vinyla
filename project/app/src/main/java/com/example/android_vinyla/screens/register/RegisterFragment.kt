package com.example.android_vinyla.screens.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android_vinyla.R
import com.example.android_vinyla.databinding.FragmentLoginBinding
import com.example.android_vinyla.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    private lateinit var binding: FragmentRegisterBinding

    private var step1ValidationCorrect = true

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

        // DEVELOPMENT PURPOSES!!!
        binding.registerEmailInput.setText("suy.jentl@gmail.com")
        binding.registerPasswordInput.setText("P@ssword1999")
        binding.registerFirstnameInput.setText("FirstName")
        binding.registerLastnameInput.setText("LastName")
        // TODO

        // SETTING ONCLICKLISTENERS
        binding.registerBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_welcomeFragment)
        }
        binding.registerNextButton.setOnClickListener {


            if (!viewModel.checkEmail(binding.registerEmailInput.text.toString())) {
                binding.registerEmailInput.setError("Incorrect email!")
                step1ValidationCorrect = false
            }

            viewModel.checkEmailInUse()

            viewModel.emailAvailable.observe(viewLifecycleOwner, Observer {
                if (null != it) {
                    Log.i("RegisterFragment", "ContentEquals")
                    if (viewModel.emailAvailable.value.contentEquals("false")) {
                        binding.registerEmailInput.setError("The given email address is already in use!")
                        step1ValidationCorrect = false
                        // 2
                        Log.i("RegisterFragment", "Fragment STOP - MAIL IN USE")
                    } else {
                        step1ValidationCorrect = true
                        binding.registerEmailInput.setError(null)
                    }
                    if (!viewModel.checkPassword(binding.registerPasswordInput.text.toString())) {
                        binding.registerPasswordInput.setError("Password must meet the requirements of at least 8 characters, 1 lowercase, 1 uppercase, 1 numeric & 1 special character!")
                        step1ValidationCorrect = false
                    }

                    Log.i("RegisterFragment", "step1ValidationCorrect " + step1ValidationCorrect)

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
                        // !!!! step1ValidationCorrect = false
                    }
                }
            })


        }
        binding.registerBackButtonStep2.setOnClickListener {
            step1ValidationCorrect = false
            binding.registerBackButton.visibility = View.VISIBLE
            binding.registerEmailInput.visibility = View.VISIBLE
            binding.registerPasswordInput.visibility = View.VISIBLE
            binding.registerFirstnameInput.visibility = View.GONE
            binding.registerLastnameInput.visibility = View.GONE
            binding.registerNextButton.visibility = View.VISIBLE
            binding.registerSignupButton.visibility = View.GONE
            binding.registerBackButtonStep2.visibility = View.GONE
            binding.registerPasswordResetWarning.visibility = View.VISIBLE
            step1ValidationCorrect = false
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