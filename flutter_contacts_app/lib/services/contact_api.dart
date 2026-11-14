import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:http/http.dart' as http;

import '../models/contact.dart';

/// Talks to the Spring Boot CRUD API. This app targets **Android only**.
///
/// Base URL during local development:
///  - Physical device over USB: run `adb reverse tcp:8080 tcp:8080` and use
///    `localhost` (the device's localhost is tunnelled to your PC). ← current
///  - Emulator: use `http://10.0.2.2:8080/api/contacts` (host loopback).
class ContactApi {
  static const String baseUrl = "http://localhost:8080/api/contacts";

  /// Every request gives up after this long. WITHOUT this, if the backend is
  /// stopped the request can hang forever and the UI spinner never stops.
  static const Duration _timeout = Duration(seconds: 8);

  final http.Client _client;
  ContactApi({http.Client? client}) : _client = client ?? http.Client();

  static const _jsonHeaders = {"Content-Type": "application/json"};

  Future<List<Contact>> getAll() async {
    try {
      final res =
          await _client.get(Uri.parse(baseUrl)).timeout(_timeout);
      if (res.statusCode == 200) {
        final List<dynamic> data = jsonDecode(res.body) as List<dynamic>;
        return data
            .map((e) => Contact.fromJson(e as Map<String, dynamic>))
            .toList();
      }
      throw _error("load contacts", res);
    } on TimeoutException {
      throw Exception(_serverDownMessage);
    } on SocketException {
      throw Exception(_serverDownMessage);
    }
  }

  Future<Contact> getOne(int id) async {
    try {
      final res =
          await _client.get(Uri.parse("$baseUrl/$id")).timeout(_timeout);
      if (res.statusCode == 200) {
        return Contact.fromJson(jsonDecode(res.body) as Map<String, dynamic>);
      }
      throw _error("load contact $id", res);
    } on TimeoutException {
      throw Exception(_serverDownMessage);
    } on SocketException {
      throw Exception(_serverDownMessage);
    }
  }

  Future<Contact> create(Contact contact) async {
    try {
      final res = await _client
          .post(
            Uri.parse(baseUrl),
            headers: _jsonHeaders,
            body: jsonEncode(contact.toJson()),
          )
          .timeout(_timeout);
      if (res.statusCode == 201 || res.statusCode == 200) {
        return Contact.fromJson(jsonDecode(res.body) as Map<String, dynamic>);
      }
      throw _error("create contact", res);
    } on TimeoutException {
      throw Exception(_serverDownMessage);
    } on SocketException {
      throw Exception(_serverDownMessage);
    }
  }

  Future<Contact> update(int id, Contact contact) async {
    try {
      final res = await _client
          .put(
            Uri.parse("$baseUrl/$id"),
            headers: _jsonHeaders,
            body: jsonEncode(contact.toJson()),
          )
          .timeout(_timeout);
      if (res.statusCode == 200) {
        return Contact.fromJson(jsonDecode(res.body) as Map<String, dynamic>);
      }
      throw _error("update contact $id", res);
    } on TimeoutException {
      throw Exception(_serverDownMessage);
    } on SocketException {
      throw Exception(_serverDownMessage);
    }
  }

  Future<void> delete(int id) async {
    try {
      final res =
          await _client.delete(Uri.parse("$baseUrl/$id")).timeout(_timeout);
      if (res.statusCode == 204 || res.statusCode == 200) return;
      throw _error("delete contact $id", res);
    } on TimeoutException {
      throw Exception(_serverDownMessage);
    } on SocketException {
      throw Exception(_serverDownMessage);
    }
  }

  static const String _serverDownMessage =
      "Cannot reach the server.\nIs the backend running on http://localhost:8080 ?";

  Exception _error(String action, http.Response res) {
    return Exception("Failed to $action (HTTP ${res.statusCode}): ${res.body}");
  }
}
