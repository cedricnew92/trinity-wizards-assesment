package com.trinitywizards.Test.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.trinitywizards.Test.DetailsContactActivity
import com.trinitywizards.Test.models.Contact
import com.trinitywizards.Test.models.Contacts
import com.trinitywizards.Test.models.Detail
import com.trinitywizards.Test.repositories.ContactsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID

class DetailViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val TAG = javaClass.simpleName

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

                return DetailViewModel(
                    savedStateHandle
                ) as T
            }
        }
    }

    var mLiveData : MutableLiveData<Detail> = MutableLiveData()
    var mErrorData : MutableLiveData<Error> = MutableLiveData()
    private var mContact : Contact? = null
    private var mJob: Job? = null

    fun initialize(context: Context, intent: Intent) {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                if (intent.hasExtra(DetailsContactActivity.KEY_CONTACT)) {
                    mContact = intent.getSerializableExtra(DetailsContactActivity.KEY_CONTACT) as Contact
                    mLiveData.postValue(Detail(Detail.Companion.Status.INIT_COMPLETE_WITH_CONTACT, mContact))
                } else
                    mLiveData.postValue(Detail(Detail.Companion.Status.INIT_COMPLETE))
            } catch (e: Exception) {
                mErrorData.postValue(Error(e))
            }
        }
    }

    fun cancel() {
        mJob?.cancel()
    }

    fun addOrUpdate(context: Context, firstname: String, lastname: String, email: String?, dob: String?) {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            if (mContact == null) {
                mContact = Contact()
                mContact?.id = UUID.randomUUID().toString().replace("-", "").take(24)
            }
            mContact?.firstname = firstname
            mContact?.lastname = lastname
            mContact?.email = email
            mContact?.dob = dob
            try {
                ContactsRepo.delete(context, mContact!!.id)
            } catch (e: Exception) {
                Log.d(TAG, e.message!!)
            }
            try {
                ContactsRepo.insert(context, mContact!!)
                mLiveData.postValue(Detail(Detail.Companion.Status.SAVED_OR_UPDATED))
            } catch (e: Exception) {
                mErrorData.postValue(Error(e))
            }
        }
    }

    fun delete(context: Context) {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                ContactsRepo.delete(context, mContact!!.id)
                mLiveData.postValue(Detail(Detail.Companion.Status.DELETED))
            } catch (e: Exception) {
                mErrorData.postValue(Error(e))
            }
        }
    }

}