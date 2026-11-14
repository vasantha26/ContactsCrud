package com.example.contactscrud.data.model


data class Contact(
    val id: Long? = null,
    val name: String,
    val email: String,
    val phone: String?
)
