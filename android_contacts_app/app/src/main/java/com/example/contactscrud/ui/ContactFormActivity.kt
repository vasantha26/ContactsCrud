package com.example.contactscrud.ui

import android.app.Activity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.contactscrud.data.Result
import com.example.contactscrud.data.model.Contact
import com.example.contactscrud.databinding.ActivityContactFormBinding


class ContactFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactFormBinding
    private val viewModel: ContactViewModel by viewModels()

    private var contactId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getLongExtra(EXTRA_ID, -1L)
        val isEdit = id != -1L
        contactId = if (isEdit) id else null

        title = if (isEdit) "Edit contact" else "New contact"

        if (isEdit) {
            binding.editName.setText(intent.getStringExtra(EXTRA_NAME))
            binding.editEmail.setText(intent.getStringExtra(EXTRA_EMAIL))
            binding.editPhone.setText(intent.getStringExtra(EXTRA_PHONE))
            binding.buttonSave.text = "Update"
        }

        binding.buttonSave.setOnClickListener { save() }

        viewModel.actionResult.observe(this) { result ->
            if (result == null) return@observe
            setSaving(false)
            when (result.status) {
                Result.Status.SUCCESS -> {
                    viewModel.clearActionResult()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                Result.Status.ERROR -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                    viewModel.clearActionResult()
                }
                Result.Status.LOADING -> Unit
            }
        }
    }

    private fun save() {
        val name = binding.editName.text?.toString()?.trim().orEmpty()
        val email = binding.editEmail.text?.toString()?.trim().orEmpty()
        val phone = binding.editPhone.text?.toString()?.trim().orEmpty()

        binding.inputName.error = null
        binding.inputEmail.error = null

        var valid = true
        if (name.isEmpty()) {
            binding.inputName.error = "Name is required"
            valid = false
        }
        if (email.isEmpty()) {
            binding.inputEmail.error = "Email is required"
            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.error = "Enter a valid email"
            valid = false
        }
        if (!valid) return

        setSaving(true)
        viewModel.saveContact(contactId, Contact(contactId, name, email, phone))
    }

    private fun setSaving(saving: Boolean) {
        binding.buttonSave.isEnabled = !saving
        binding.progressBar.visibility =
            if (saving) android.view.View.VISIBLE else android.view.View.GONE
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PHONE = "extra_phone"
    }
}
