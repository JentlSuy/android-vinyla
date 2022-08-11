package com.example.android_vinyla.screens.main

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android_vinyla.R
import com.example.android_vinyla.database.UserSettingsDatabase
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

        val application = requireNotNull(this.activity).application

        val dataSource = UserSettingsDatabase.getInstance(application).userSettingsDao

        //viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val viewModelFactory = MainViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.mainViewModel = viewModel

        binding.lifecycleOwner = this

        binding.photosGrid.visibility = View.VISIBLE
        binding.mainEmptyCollectionText.visibility = View.GONE

        binding.photosGrid.adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
            viewModel.displayPropertyDetails(it)
            if (viewModel.selectedArtists.value!!.isNotEmpty()) {
                binding.mainCreateStationTextview.setTextColor(Color.BLACK)
                binding.mainCreateStationTextview.setOnClickListener {
                    binding.mainRedirectLayout.visibility = View.VISIBLE
                }
                binding.mainCreateStationButton.setOnClickListener {
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
                binding.mainCreateStationTextview.setOnClickListener {}
                binding.mainCreateStationButton.setOnClickListener {}
            }
        })

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
                binding.mainRedirectLayout.visibility = View.VISIBLE

                toggleBackgroundButtons(true)
            }
        }

        setSettingsClickListeners()

        binding.mainSettingsButton.setOnClickListener {
            binding.mainSettingsLayout.visibility = View.VISIBLE
            toggleBackgroundButtons(true)
        }

        binding.mainSettingsCloseImageButton.setOnClickListener {
            binding.photosGrid.visibility = View.VISIBLE
            binding.mainSettingsLayout.visibility = View.GONE
            toggleBackgroundButtons(false)
        }

        binding.mainSettingsLayout.setOnClickListener {
            binding.photosGrid.visibility = View.VISIBLE
            binding.mainSettingsLayout.visibility = View.GONE
            toggleBackgroundButtons(false)
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
            binding.mainCreateStationTextview.setTextColor(Color.BLACK)
            viewModel.overrideEmptySelectionColorBug(true)
        } else if (!disable) {
            binding.mainRefreshButton.setOnClickListener {
                binding.photosGrid.visibility = View.VISIBLE
                binding.mainEmptyCollectionText.visibility = View.GONE
                // TODO RESET DATA
                viewModel.refresh()
            }
            //TODO
            binding.mainSettingsButton.setOnClickListener {
                binding.mainSettingsLayout.visibility = View.VISIBLE
                toggleBackgroundButtons(true)
            }
            //TODO
            binding.mainLogoutButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_welcomeFragment)
            }
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

            // only set create station color to grey when the selected artists list is empty.
            if (viewModel.selectedArtists.value!!.isEmpty()) {
                viewModel.overrideEmptySelectionColorBug(false)
                binding.mainCreateStationTextview.setTextColor(Color.LTGRAY)
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

    private fun setSettingsClickListeners() {
        var streamingServiceButtons: ArrayList<ImageButton> = ArrayList()
        streamingServiceButtons.add(binding.spotifyImageButton)
        streamingServiceButtons.add(binding.youtubeMusicImageButton)
        streamingServiceButtons.add(binding.soundcloudImageButton)
        streamingServiceButtons.add(binding.appleMusicImageButton)
        streamingServiceButtons.add(binding.tidalImageButton)
        streamingServiceButtons.add(binding.amazonMusicImageButton)
        streamingServiceButtons.add(binding.deezerImageButton)

        val streamingServicePackageString = viewModel.streamingServicePackage.value.toString()

        for (button in streamingServiceButtons) {
            button.setOnClickListener {
                toggleStreamingService(
                    button,
                    streamingServiceButtons,
                    ""
                )
            }
        }

        // TO SELECT CORRECT IMAGE BUTTON AT STARTUP
        when (streamingServicePackageString) {
            "com.spotify.music" -> {
                toggleStreamingService(
                    binding.spotifyImageButton,
                    streamingServiceButtons,
                    streamingServicePackageString
                )
            }
            "com.google.android.apps.youtube.music" -> {
                toggleStreamingService(
                    binding.youtubeMusicImageButton,
                    streamingServiceButtons,
                    streamingServicePackageString
                )
            }
            "com.soundcloud.android" -> {
                toggleStreamingService(
                    binding.soundcloudImageButton,
                    streamingServiceButtons,
                    streamingServicePackageString
                )
            }
            "com.apple.android.music" -> {
                toggleStreamingService(
                    binding.appleMusicImageButton,
                    streamingServiceButtons,
                    streamingServicePackageString
                )
            }
            "com.aspiro.tidal" -> {
                toggleStreamingService(
                    binding.tidalImageButton,
                    streamingServiceButtons,
                    streamingServicePackageString
                )
            }
            "com.amazon.mp3" -> {
                toggleStreamingService(
                    binding.amazonMusicImageButton,
                    streamingServiceButtons,
                    streamingServicePackageString
                )
            }
            "deezer.android.app" -> {
                toggleStreamingService(
                    binding.deezerImageButton,
                    streamingServiceButtons,
                    streamingServicePackageString
                )
            }
            else -> {
                Log.i("MainFragment", "Error when loading default streaming service.")
            }
        }

    }

    private fun toggleStreamingService(
        buttonToAddBorder: ImageButton,
        buttonsToRemoveBorder: ArrayList<ImageButton>,
        streamingServicePackageString: String
    ) {

        // empty when selecting new streaming service, check streaming service by id in image button.
        if (streamingServicePackageString.isEmpty()) {
            if (buttonToAddBorder.toString().contains("spotify"))
                viewModel.setStreamingService("com.spotify.music")
            else if (buttonToAddBorder.toString().contains("apple"))
                viewModel.setStreamingService("com.apple.android.music")
            else if (buttonToAddBorder.toString().contains("youtube"))
                viewModel.setStreamingService("com.google.android.apps.youtube.music")
            else if (buttonToAddBorder.toString().contains("soundcloud"))
                viewModel.setStreamingService("com.soundcloud.android")
            else if (buttonToAddBorder.toString().contains("tidal"))
                viewModel.setStreamingService("com.aspiro.tidal")
            else if (buttonToAddBorder.toString().contains("deezer"))
                viewModel.setStreamingService("deezer.android.app")
            else if (buttonToAddBorder.toString().contains("amazon"))
                viewModel.setStreamingService("com.amazon.mp3")
        } else viewModel.setStreamingService(streamingServicePackageString)

        for (buttonToRemoveBorder in buttonsToRemoveBorder) {
            buttonToRemoveBorder.setBackground(
                activity?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.imagebutton_border_removed
                    )
                }
            )
        }

        buttonToAddBorder.setBackground(
            activity?.let { ContextCompat.getDrawable(it, R.drawable.imagebutton_border) }
        )
    }
}