package com.trinitywizards.Test.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.trinitywizards.Test.models.Login
import com.trinitywizards.Test.models.Splash
import com.trinitywizards.Test.repositories.CacheRepo
import com.trinitywizards.Test.repositories.ContactsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SplashViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()

                return SplashViewModel(
                    savedStateHandle
                ) as T
            }
        }
    }

    var mLiveData : MutableLiveData<Splash> = MutableLiveData()
    var mErrorData : MutableLiveData<Error> = MutableLiveData()
    private var mJob: Job? = null

    fun initialize(context: Context) {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                ContactsRepo.all(context)
                mLiveData.postValue(Splash(Splash.Companion.Status.INIT_COMPLETE))
            } catch (e: Exception) {
                mErrorData.postValue(Error(e))
            }
        }
    }

    fun checkCached(context: Context) {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            val id = CacheRepo.userId(context)
            if (!id.isNullOrEmpty())
                mLiveData.postValue(Splash(Splash.Companion.Status.CACHED))
            else
                mLiveData.postValue(Splash(Splash.Companion.Status.NOT_CACHED))
        }
    }

    fun cancel() {
        mJob?.cancel()
    }

}