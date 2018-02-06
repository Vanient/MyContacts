package com.example.vanient.mycontacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vanient.contacts.R;

public class MainActivity extends Activity {
    private Button confirm;
    private Button cancel;
    EditText name;
    EditText email;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ContactsManager cm = new ContactsManager(this.getContentResolver());
        confirm= findViewById(R.id.confirm);
        cancel=findViewById(R.id.cancel);
        name= super.findViewById(R.id.name);
        email= super.findViewById(R.id.email);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = new Contact();
                contact.setName(name.getText().toString());
                contact.setEmail(email.getText().toString());

                //test addContact

                cm.addContact(contact);
                Intent i=new Intent(MainActivity.this,AllContacts.class);
                startActivity(i);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,AllContacts.class);
                startActivity(i);
            }
        });

/*
        cm.searchContact("张一");
*/

   /*     cm.searchContact("张一");
*/
      /*     //test updateContact
        Contact contactNew = new Contact(contact);
        contactNew.setName("张二");
        contactNew.setNumber("987654321");
        contactNew.setEmail("newEmail@test");
        cm.updateContact(contact, contactNew);
        cm.searchContact("张一");
        cm.searchContact("张二");

     //test deleteContact
        cm.deleteContact(contactNew);
        cm.searchContact("张二");*/

    }

}
