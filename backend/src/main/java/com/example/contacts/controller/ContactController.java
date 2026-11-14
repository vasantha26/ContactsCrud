package com.example.contacts.controller;

import com.example.contacts.model.Contact;
import com.example.contacts.repository.ContactRepository;
import com.example.contacts.web.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactRepository repository;

    public ContactController(ContactRepository repository) {
        this.repository = repository;
    }

    // GET /api/contacts -> list all
    @GetMapping
    public List<Contact> getAll() {
        return repository.findAll();
    }

    // GET /api/contacts/{id} -> one, or 404
    @GetMapping("/{id}")
    public Contact getOne(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact " + id + " not found"));
    }

    // POST /api/contacts -> create, 201
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contact create(@Valid @RequestBody Contact contact) {
        contact.setId(null); // ignore any client-supplied id
        return repository.save(contact);
    }

    // PUT /api/contacts/{id} -> update, or 404
    @PutMapping("/{id}")
    public Contact update(@PathVariable Long id, @Valid @RequestBody Contact incoming) {
        Contact existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact " + id + " not found"));
        existing.setName(incoming.getName());
        existing.setEmail(incoming.getEmail());
        existing.setPhone(incoming.getPhone());
        return repository.save(existing);
    }

    // DELETE /api/contacts/{id} -> 204, or 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Contact " + id + " not found");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
