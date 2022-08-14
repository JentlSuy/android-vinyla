package com.example.android_vinyla.screens.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        binding.registerViewModel = viewModel

        binding.lifecycleOwner = this

        // DEVELOPMENT PURPOSES!!!
        //binding.registerEmailInput.setText("testtest@test.com")
        binding.registerEmailInput.setText("suy.jentl@gmail.com")
        binding.registerPasswordInput.setText("P@ssword1999")
        binding.registerFirstnameInput.setText("FirstName")
        binding.registerLastnameInput.setText("LastName")
        // TODO

        binding.registerBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_welcomeFragment)
        }

        binding.registerNextButton.setOnClickListener {

            loading(true)

            if (!viewModel.checkEmail(binding.registerEmailInput.text.toString())) {
                binding.registerEmailInput.setError("Incorrect email!")
                step1ValidationCorrect = false
                loading(false)
            } else {

                viewModel.checkEmailInUse()

                viewModel.emailAvailable.observe(viewLifecycleOwner, Observer {
                    if (viewModel.emailAvailable.value.contentEquals("false")) {
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            binding.registerEmailInput.setError("The given email address is already in use!")
                            loading(false)
                        }, 1000)
                        step1ValidationCorrect = false
                    } else if (viewModel.emailAvailable.value.contentEquals("true")) {
                        step1ValidationCorrect = true
                        binding.registerEmailInput.setError(null)
                    }
                    if (!viewModel.checkPassword(binding.registerPasswordInput.text.toString())) {
                        binding.registerPasswordInput.setError("Password must meet the requirements of at least 8 characters, 1 lowercase, 1 uppercase, 1 numeric & 1 special character!")
                        binding.registerEmailInput.setError(null)
                        step1ValidationCorrect = false
                        loading(false)
                    }

                    //Log.i("RegisterFragment", "step1ValidationCorrect " + step1ValidationCorrect)

                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        if (step1ValidationCorrect) {
                            loading(false)
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
                    }, 1000)

                })
            }
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
            binding.registerEmailInput.setError(null)
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

                viewModel.signUp()

                findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
            }
        }
        return binding.root
    }

    private fun loading(startLoading: Boolean) {
        if (startLoading) {
            binding.registerProgressBar.visibility = View.VISIBLE
            binding.registerNextButton.visibility = View.GONE
        } else if (!startLoading) {
            binding.registerProgressBar.visibility = View.GONE
            binding.registerNextButton.visibility = View.VISIBLE
        }
    }
}