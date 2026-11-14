package com.example.contactscrud.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactscrud.data.Result
import com.example.contactscrud.data.model.Contact
import com.example.contactscrud.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ContactViewModel by viewModels()

    private val adapter by lazy {
        ContactAdapter(
            onEdit = { openForm(it) },
            onDelete = { confirmDelete(it) }
        )
    }

    // Re-load the list whenever the form reports a successful save.
    private val formLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) viewModel.loadContacts()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.fabAdd.setOnClickListener { openForm(null) }
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadContacts() }
        binding.buttonRetry.setOnClickListener { viewModel.loadContacts() }

        observeViewModel()
        viewModel.loadContacts()
    }

    private fun observeViewModel() {
        viewModel.contacts.observe(this) { result ->
            when (result.status) {
                Result.Status.LOADING -> {
                    binding.swipeRefresh.isRefreshing = true
                    binding.errorView.visibility = View.GONE
                }
                Result.Status.SUCCESS -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.errorView.visibility = View.GONE
                    val list = result.data.orEmpty()
                    adapter.submitList(list)
                    binding.textEmpty.visibility =
                        if (list.isEmpty()) View.VISIBLE else View.GONE
                }
                Result.Status.ERROR -> {
                    binding.swipeRefresh.isRefreshing = false
                    // Only take over the screen with the error view when we have
                    // nothing to show; otherwise keep the list and just toast.
                    if (adapter.currentList.isEmpty()) {
                        binding.textEmpty.visibility = View.GONE
                        binding.textError.text = result.message ?: "Cannot reach the server."
                        binding.errorView.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewModel.actionResult.observe(this) { result ->
            if (result == null) return@observe
            if (result.status == Result.Status.ERROR) {
                Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
            }
            viewModel.clearActionResult()
        }
    }

    private fun openForm(contact: Contact?) {
        val intent = Intent(this, ContactFormActivity::class.java)
        if (contact != null) {
            intent.putExtra(ContactFormActivity.EXTRA_ID, contact.id ?: -1L)
            intent.putExtra(ContactFormActivity.EXTRA_NAME, contact.name)
            intent.putExtra(ContactFormActivity.EXTRA_EMAIL, contact.email)
            intent.putExtra(ContactFormActivity.EXTRA_PHONE, contact.phone)
        }
        formLauncher.launch(intent)
    }

    private fun confirmDelete(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Delete contact?")
            .setMessage("Remove \"${contact.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                contact.id?.let { viewModel.deleteContact(it) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
