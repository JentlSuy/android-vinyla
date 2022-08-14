package com.example.android_vinyla.screens.welcome

import android.graphics.drawable.Drawable
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
import com.example.android_vinyla.database.UserSettingsDatabase
import com.example.android_vinyla.databinding.FragmentWelcomeBinding
import com.example.android_vinyla.screens.main.MainViewModel
import com.example.android_vinyla.screens.main.MainViewModelFactory
import com.example.android_vinyla.screens.register.RegisterViewModel

class WelcomeFragment : Fragment() {

    private lateinit var viewModel: WelcomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentWelcomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_welcome, container, false
        )
        val application = requireNotNull(this.activity).application

        val dataSource = UserSettingsDatabase.getInstance(application).userSettingsDao

        val viewModelFactory = WelcomeViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory)[WelcomeViewModel::class.java]

        // set random background TODO
        // binding.welcomeBackground.setImageResource(R.drawable.vinyla_logo_square);

        checkAlreadyLoggedIn()

        binding.welcomeLoginButton.setOnClickListener {
            Log.i("WelcomeFragment", "Login button was pressed!")
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }

        binding.welcomeRegisterButton.setOnClickListener {
            Log.i("WelcomeFragment", "Register button was pressed!")
            findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
        }
        return binding.root
    }

    private fun checkAlreadyLoggedIn() {
        try {
            if (!viewModel.bearerToken.value.isNullOrBlank()) {
                Log.i("WelcomeFragment", "Already logged in!")
                findNavController().navigate(R.id.action_welcomeFragment_to_mainFragment)
            } else
                Log.i("WelcomeFragment", "Not logged in yet!")
        } catch (e: IllegalArgumentException) {

        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("WelcomeFragment", "onPause Called")
    }

    override fun onResume() {
        super.onResume()
        checkAlreadyLoggedIn()
    }


}