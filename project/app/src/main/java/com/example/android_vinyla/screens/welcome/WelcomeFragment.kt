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
import com.example.android_vinyla.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    companion object {
        fun newInstance() = WelcomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentWelcomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_welcome, container, false
        )

        // set random background TODO
        // binding.welcomeBackground.setImageResource(R.drawable.vinyla_logo_square);

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

}