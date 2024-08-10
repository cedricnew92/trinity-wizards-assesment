package com.trinitywizards.Test.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.trinitywizards.Test.fragments.ProfileFragment
import com.trinitywizards.Test.models.Contact
import com.trinitywizards.Test.models.Login
import com.trinitywizards.Test.models.Profile
import com.trinitywizards.Test.repositories.CacheRepo
import com.trinitywizards.Test.repositories.ContactsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(
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

                return ProfileViewModel(
                    savedStateHandle
                ) as T
            }
        }
    }

    var mLiveData : MutableLiveData<Profile> = MutableLiveData()
    var mErrorData : MutableLiveData<Error> = MutableLiveData()
    private var mContact : Contact? = null
    private var mJob: Job? = null

    fun initialize(context: Context) {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = CacheRepo.userId(context)
                mContact = ContactsRepo.get(context, id!!)
                mLiveData.postValue(Profile(Profile.Companion.Status.INIT_COMPLETE, mContact))
            } catch (e: Exception) {
                mErrorData.postValue(Error(e))
            }
        }
    }

    fun logout(context: Context) {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = CacheRepo.userId(context)!!
                CacheRepo.delete(context, id)
                mLiveData.postValue(Profile(Profile.Companion.Status.LOGGED_OUT))
            } catch (e: Exception) {
                mErrorData.postValue(Error(e))
            }
        }
    }

    fun cancel() {
        mJob?.cancel()
    }

    fun update() {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                mLiveData.postValue(Profile(Profile.Companion.Status.UPDATE, mContact))
            } catch (e: Exception) {
                mErrorData.postValue(Error(e))
            }
        }
    }

}