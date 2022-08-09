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
import android.widget.Toast
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
                binding.mainCreateStationTextview.setOnClickListener {
                    binding.photosGrid.visibility = View.GONE
                    binding.mainRedirectLayout.visibility = View.VISIBLE
                }
                binding.mainCreateStationButton.setOnClickListener {
                    binding.photosGrid.visibility = View.GONE
                    binding.mainRedirectLayout.visibility = View.VISIBLE
                    toggleBackgroundButtons(true)
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        launchIntent()
                        handler.postDelayed({
                            binding.photosGrid.visibility = View.VISIBLE
                            binding.mainRedirectLayout.visibility = View.GONE
                            toggleBackgroundButtons(false)
                        }, 500)

                    }, 1500)
                }
            } else {
                binding.mainCreateStationTextview.setTextColor(Color.LTGRAY)
                binding.mainCreateStationTextview.setOnClickListener {

                }
                binding.mainCreateStationButton.setOnClickListener {

                }
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

        toggleBackgroundButtons(false)

        binding.mainRedirectCloseImageButton.setOnClickListener {
            binding.photosGrid.visibility = View.VISIBLE
            binding.mainRedirectLayout.visibility = View.GONE
            toggleBackgroundButtons(false)
        }

        binding.mainRedirectLayout.setOnClickListener {
            binding.photosGrid.visibility = View.VISIBLE
            binding.mainRedirectLayout.visibility = View.GONE
            toggleBackgroundButtons(false)
        }

        binding.mainCreateStationTextview.setOnClickListener {
            if (viewModel.selectedArtists.value!!.isNotEmpty()) {
                binding.photosGrid.visibility = View.GONE
                binding.mainRedirectLayout.visibility = View.VISIBLE

                toggleBackgroundButtons(true)
            }
        }
        return binding.root
    }

    private fun toggleBackgroundButtons(disable: Boolean) {
        if (disable) {
            binding.mainRefreshButton.setOnClickListener { }
            binding.mainSettingsButton.setOnClickListener { }
            binding.mainLogoutButton.setOnClickListener { }
            binding.mainWebTextview.setOnClickListener { }
            binding.mainVinylaWebButton.setOnClickListener { }
        } else if (!disable) {
            binding.mainRefreshButton.setOnClickListener {
                binding.photosGrid.visibility = View.VISIBLE
                binding.mainEmptyCollectionText.visibility = View.GONE
                // TODO RESET DATA
                viewModel.refresh()
            }
            //TODO
            binding.mainSettingsButton.setOnClickListener { }
            //TODO
            binding.mainLogoutButton.setOnClickListener { }
            binding.mainWebTextview.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(vinylaUrl)
                startActivity(i)
            }
            binding.mainVinylaWebButton.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(vinylaUrl)
                startActivity(i)
            }
        }
    }

    private fun launchIntent() {
        var launchIntent =
            context?.getPackageManager()
                ?.getLaunchIntentForPackage(viewModel.streamingServicePackage.value!!);
        if (launchIntent != null)
            startActivity(launchIntent);//null pointer check in case package name was not found
        else
            Toast.makeText(
                context,
                "Unable to open streaming service. Do you have the app installed?",
                Toast.LENGTH_LONG
            ).show();
    }
}