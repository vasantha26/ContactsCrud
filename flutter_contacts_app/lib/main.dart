import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'viewmodels/contact_viewmodel.dart';
import 'screens/contact_list_screen.dart';

void main() {
  runApp(const ContactsApp());
}

class ContactsApp extends StatelessWidget {
  const ContactsApp({super.key});

  @override
  Widget build(BuildContext context) {

    return ChangeNotifierProvider(
      create: (_) => ContactViewModel()..loadContacts(),
      child: MaterialApp(
        title: 'Contacts CRUD',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
          useMaterial3: true,
        ),
        home: const ContactListScreen(),
      ),
    );
  }
}
