import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/contact.dart';
import '../viewmodels/contact_viewmodel.dart';
import 'contact_form_screen.dart';


class ContactListScreen extends StatelessWidget {
  const ContactListScreen({super.key});

  Future<void> _openForm(BuildContext context, {Contact? existing}) async {
    await Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => ContactFormScreen(existing: existing)),
    );

  }

  Future<void> _confirmDelete(BuildContext context, Contact c) async {
    final vm = context.read<ContactViewModel>();
    final confirm = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Delete contact?'),
        content: Text('Remove "${c.name}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
    if (confirm == true && c.id != null) {
      vm.deleteContact(c.id!);
    }
  }

  @override
  Widget build(BuildContext context) {
    // context.watch() subscribes this widget to the ViewModel:
    // whenever notifyListeners() fires, build() runs again.
    final vm = context.watch<ContactViewModel>();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Contacts'),
        actions: [
          IconButton(
            onPressed: vm.loadContacts,
            icon: const Icon(Icons.refresh),
            tooltip: 'Refresh',
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _openForm(context),
        icon: const Icon(Icons.add),
        label: const Text('Add'),
      ),
      body: RefreshIndicator(
        onRefresh: vm.loadContacts,
        child: _buildBody(context, vm),
      ),
    );
  }

  Widget _buildBody(BuildContext context, ContactViewModel vm) {
    if (vm.isLoading && vm.contacts.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    if (vm.hasError) {
      return ListView(
        children: [
          const SizedBox(height: 100),
          const Icon(Icons.error_outline, size: 48, color: Colors.red),
          const SizedBox(height: 12),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 24),
            child: Text(vm.errorMessage ?? 'Something went wrong',
                textAlign: TextAlign.center),
          ),
          const SizedBox(height: 12),
          Center(
            child: FilledButton.icon(
              onPressed: vm.loadContacts,
              icon: const Icon(Icons.refresh),
              label: const Text('Retry'),
            ),
          ),
        ],
      );
    }

    if (vm.contacts.isEmpty) {
      return ListView(
        children: const [
          SizedBox(height: 120),
          Center(child: Text('No contacts yet. Tap "Add".')),
        ],
      );
    }

    return ListView.separated(
      itemCount: vm.contacts.length,
      separatorBuilder: (_, __) => const Divider(height: 1),
      itemBuilder: (_, i) {
        final c = vm.contacts[i];
        return ListTile(
          leading: CircleAvatar(
            child: Text(c.name.isNotEmpty ? c.name[0].toUpperCase() : '?'),
          ),
          title: Text(c.name),
          subtitle: Text('${c.email}\n${c.phone}'),
          isThreeLine: true,
          onTap: () => _openForm(context, existing: c),
          trailing: IconButton(
            icon: const Icon(Icons.delete_outline),
            onPressed: () => _confirmDelete(context, c),
          ),
        );
      },
    );
  }
}
