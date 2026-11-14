package com.example.contactscrud.data.api

import com.example.contactscrud.data.model.Contact
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ContactApi {

    @GET("contacts")
    suspend fun getAll(): Response<List<Contact>>

    @POST("contacts")
    suspend fun create(@Body contact: Contact): Response<Contact>

    @PUT("contacts/{id}")
    suspend fun update(@Path("id") id: Long, @Body contact: Contact): Response<Contact>

    @DELETE("contacts/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}
