package com.trinitywizards.Test.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.trinitywizards.Test.models.Contact
import com.trinitywizards.Test.models.Contacts
import com.trinitywizards.Test.repositories.CacheRepo
import com.trinitywizards.Test.repositories.ContactsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ContactsViewModel(
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

                return ContactsViewModel(
                    savedStateHandle
                ) as T
            }
        }
    }

    var mLiveData : MutableLiveData<Contacts> = MutableLiveData()
    var mErrorData : MutableLiveData<Error> = MutableLiveData()
    private var mContacts = ArrayList<Contact>()
    private var mJob: Job? = null

    fun reset(context: Context) {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                ContactsRepo.reset(context)
                initialize(context)
            } catch (e: Exception) {
                mErrorData.postValue(Error(e))
            }
        }
    }

    fun initialize(context: Context) {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                mContacts = ArrayList()
                val contacts = ContactsRepo.all(context)
                val meId = CacheRepo.userId(context)
                if (contacts.isNotEmpty()) {
                    var lastUnique = contacts[0].firstname.first().toString()
                    mContacts.add(Contact(Contact.HEADER, lastUnique))
                    if (meId == contacts[0].id)
                        contacts[0].you = true
                    mContacts.add(contacts[0])
                    for (i in 1 until contacts.size) {
                        val contact = contacts[i]
                        val currentUnique = contact.firstname.first().toString()
                        if (currentUnique != lastUnique) {
                            lastUnique = currentUnique
                            mContacts.add(Contact(Contact.HEADER, lastUnique))
                        }
                        if (meId == contact.id)
                            contact.you = true
                        mContacts.add(contact)
                    }
                }
                mLiveData.postValue(Contacts(Contacts.Companion.Status.INIT_COMPLETE, mContacts))
            } catch (e: Exception) {
                mErrorData.postValue(Error(e))
            }
        }
    }

    fun get(position: Int) {
        mJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val contact = mContacts[position]
                mLiveData.postValue(Contacts(Contacts.Companion.Status.EDIT, contact))
            } catch (e: Exception) {
                mErrorData.postValue(Error(e))
            }
        }
    }

    fun cancel() {
        mJob?.cancel()
    }

}