package com.example.android_vinyla.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_vinyla.network.VinylaApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class VinylaApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {
    /*  // The internal MutableLiveData that stores the status of the most recent request
      private val _status = MutableLiveData<VinylaApiStatus>()

      // The external immutable LiveData for the request status
      val status: LiveData<VinylaApiStatus>
          get() = _status

      // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
      // with new values
      private val _properties = MutableLiveData<List<VinylaProperty>>()

      // The external LiveData interface to the property is immutable, so only this class can modify
      val properties: LiveData<List<VinylaProperty>>
          get() = _properties

      // Internally, we use a MutableLiveData to handle navigation to the selected property
      private val _navigateToSelectedProperty = MutableLiveData<VinylaProperty>()

      // The external immutable LiveData for the navigation property
      val navigateToSelectedProperty: LiveData<VinylaProperty>
          get() = _navigateToSelectedProperty*/


    // SELF SELF
    private val _response = MutableLiveData<String>()
    val response: LiveData<String> get() = _response

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getVinylaProperties()
    }

    private fun getVinylaProperties() {

        coroutineScope.launch {
            var getPropertiesDeferred = VinylaApi.retrofitService.getAll(VinylaApi.getEmail())
            try {
                var result = getPropertiesDeferred.await()
                _response.value = result
            } catch (t: Throwable) {
                _response.value = "Failure: " + t.message
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}