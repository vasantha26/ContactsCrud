import 'package:flutter/foundation.dart';

import '../models/contact.dart';
import '../services/contact_api.dart';

enum ViewState { idle, loading, error }


class ContactViewModel extends ChangeNotifier {
  final ContactApi _api;
  ContactViewModel({ContactApi? api}) : _api = api ?? ContactApi();

  ViewState state = ViewState.idle;
  List<Contact> contacts = [];
  String? errorMessage;

  bool get isLoading => state == ViewState.loading;
  bool get hasError => state == ViewState.error;

  Future<void> loadContacts() async {
    state = ViewState.loading;
    notifyListeners();

    try {
      contacts = await _api.getAll();
      state = ViewState.idle;
    } catch (e) {
      errorMessage = e.toString();
      state = ViewState.error;
    }
    notifyListeners();
  }

  Future<String?> saveContact(int? id, Contact contact) async {
    try {
      if (id == null) {
        await _api.create(contact);
      } else {
        await _api.update(id, contact);
      }
      await loadContacts(); // refresh the list so observers update
      return null;
    } catch (e) {
      return e.toString();
    }
  }

  Future<void> deleteContact(int id) async {
    try {
      await _api.delete(id);
      await loadContacts();
    } catch (e) {
      errorMessage = e.toString();
      state = ViewState.error;
      notifyListeners();
    }
  }
}
