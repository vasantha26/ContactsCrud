package com.example.contactscrud.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactscrud.data.ContactRepository
import com.example.contactscrud.data.Result
import com.example.contactscrud.data.model.Contact
import kotlinx.coroutines.launch

class ContactViewModel(
    private val repository: ContactRepository = ContactRepository()
) : ViewModel() {

    private val _contacts = MutableLiveData<Result<List<Contact>>>()
    val contacts: LiveData<Result<List<Contact>>> = _contacts

    private val _actionResult = MutableLiveData<Result<*>?>()
    val actionResult: LiveData<Result<*>?> = _actionResult

    fun loadContacts() {
        _contacts.value = Result.loading()
        viewModelScope.launch {
            _contacts.value = repository.getAll()
        }
    }

    fun saveContact(id: Long?, contact: Contact) {
        viewModelScope.launch {
            _actionResult.value =
                if (id == null) repository.create(contact)
                else repository.update(id, contact)
        }
    }

    fun deleteContact(id: Long) {
        viewModelScope.launch {
            val result = repository.delete(id)
            _actionResult.value = result
            if (result.status == Result.Status.SUCCESS) loadContacts()
        }
    }

    fun clearActionResult() {
        _actionResult.value = null
    }
}
