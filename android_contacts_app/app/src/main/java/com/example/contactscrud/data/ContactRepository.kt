package com.example.contactscrud.data

import com.example.contactscrud.data.api.BaseRemoteDataSource
import com.example.contactscrud.data.api.ContactApi
import com.example.contactscrud.data.api.RetrofitClient
import com.example.contactscrud.data.model.Contact


class ContactRepository(
    private val api: ContactApi = RetrofitClient.api
) : BaseRemoteDataSource() {

    suspend fun getAll(): Result<List<Contact>> = getResult { api.getAll() }

    suspend fun create(contact: Contact): Result<Contact> = getResult { api.create(contact) }

    suspend fun update(id: Long, contact: Contact): Result<Contact> =
        getResult { api.update(id, contact) }

    suspend fun delete(id: Long): Result<Unit> = getResult { api.delete(id) }
}
