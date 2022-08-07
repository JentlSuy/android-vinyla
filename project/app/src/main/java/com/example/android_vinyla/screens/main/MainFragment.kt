package com.example.android_vinyla.screens.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.android_vinyla.R
import com.example.android_vinyla.databinding.FragmentMainBinding
import com.example.android_vinyla.screens.register.RegisterViewModel

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // INITIALIZING BINDING
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false
        )
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.mainViewModel = viewModel

        binding.lifecycleOwner = this

        binding.photosGrid.adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
            viewModel.displayPropertyDetails(it)
        })

        return binding.root
    }
}