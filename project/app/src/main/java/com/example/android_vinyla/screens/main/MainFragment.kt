package com.example.android_vinyla.screens.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android_vinyla.R
import com.example.android_vinyla.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: FragmentMainBinding

    private var vinylaUrl = "https://vinyla.azurewebsites.net/"

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

        binding.photosGrid.visibility = View.VISIBLE
        binding.mainEmptyCollectionText.visibility = View.GONE

        binding.photosGrid.adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
            viewModel.displayPropertyDetails(it)
            if (viewModel.selectedArtists.value!!.isNotEmpty()) {
                binding.mainCreateStationTextview.setTextColor(Color.BLACK)
                binding.mainCreateStationTextview.isClickable = true
                binding.mainCreateStationButton.isClickable = true
            } else {
                binding.mainCreateStationTextview.setTextColor(Color.LTGRAY)
                binding.mainCreateStationTextview.isClickable = false
                binding.mainCreateStationButton.isClickable = false
            }
        })

        //binding.photosGrid.notifyDataSetChanged()

        viewModel.emptyCollection.observe(viewLifecycleOwner, Observer {
            if (viewModel.emptyCollection.value == true) {
                binding.photosGrid.visibility = View.GONE
                binding.mainEmptyCollectionText.visibility = View.VISIBLE
            }
        }
        )

        binding.mainRefreshButton.setOnClickListener {
            binding.photosGrid.visibility = View.VISIBLE
            binding.mainEmptyCollectionText.visibility = View.GONE
            // TODO RESET DATA
            viewModel.refresh()
        }

        binding.mainVinylaWebButton.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(vinylaUrl)
            startActivity(i)
        }

        binding.mainWebTextview.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(vinylaUrl)
            startActivity(i)
        }

        return binding.root
    }
}