package com.example.vanient.mycontacts.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.domain.entity.Contact;
import com.example.vanient.mycontacts.domain.adapter.ContactsAdapter;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

public class ContactsDisplayActivity extends AppCompatActivity implements ContactsAdapter.CheckItemListener{
    private Button add;
    private RecyclerView rvContacts;
    private Button jump;
    private Button done;
    private List<Contact> mChoosedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contacts);

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContactsDisplayActivity.this, ContactAddActivity.class);
                startActivity(i);

            }
        });

        jump = (Button) findViewById(R.id.jump);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(ContactsDisplayActivity.this, GrougDisplayActivity.class);
                startActivity(j);
            }
        });

        done = (Button) findViewById(R.id.edit_done);
        Intent i = getIntent();
        if (i.getBooleanExtra("EDIT", false)) {
            done.setVisibility(View.VISIBLE);
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 保存联系人到群组
                Intent i = new Intent(ContactsDisplayActivity.this, GroupChatActivity.class);
                i.putExtra("CONTACTLIST",(Serializable) mChoosedContacts);
                startActivity(i);
            }
        });

        getAllContacts();
    }

    private void getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        Contact contact;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                name = "Name:" + name;

                contact = new Contact();
                contact.setName(name);

                Cursor emailCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id},
                        null);
                if (emailCursor.moveToNext()) {
                    String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    emailId = "Email:" + emailId;
                    contact.setEmail(emailId);
                }
                emailCursor.close();
                contactList.add(contact);
            }
        }
        cursor.close();

        ContactsAdapter contactAdapter = new ContactsAdapter(contactList, getApplicationContext(), this);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(contactAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        done.setVisibility(View.GONE);
    }

    @Override
    public void itemChecked(Contact contact, boolean isChecked) {
        if (isChecked) {
            mChoosedContacts.add(contact);
        } else {
            if (mChoosedContacts.contains(contact)) {
                mChoosedContacts.remove(contact);
            }
        }
    }
}