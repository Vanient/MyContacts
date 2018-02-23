package com.example.vanient.mycontacts.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.domain.entity.Contact;
import com.example.vanient.mycontacts.domain.adapter.ContactsAdapter;
import com.example.vanient.mycontacts.domain.util.ContactsManager;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ContactsDisplayActivity extends AppCompatActivity implements ContactsAdapter.CheckItemListener{
    private Button add;
    private RecyclerView rvContacts;
    private Button jump;
    private Button done;
    private List<Contact> mChoosedContacts = new ArrayList<>();
    private String groupid;
    private ContactsManager contactsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contacts);

        contactsManager = new ContactsManager(this.getContentResolver());

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContactsDisplayActivity.this, ContactAddActivity.class);
                startActivity(i);

            }
        });


        final String groupId = getIntent().getStringExtra("groupId");


        jump = (Button) findViewById(R.id.jump);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(ContactsDisplayActivity.this, GroupDisplayActivity.class);
                startActivity(j);
            }
        });

        done = (Button) findViewById(R.id.edit_done);
        final Intent i = getIntent();
        if (i.getBooleanExtra("EDIT", false)) {
            done.setVisibility(View.VISIBLE);
        }


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
               for (Contact contact : mChoosedContacts) {
                    operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, Integer.parseInt(contactsManager.getContactID(contact.getName())))
                            .withValue(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
                                    ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId)
                            .build())
                            ;
                }

                contactsManager.groupAddContacts(operationList);

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
        if (mChoosedContacts.contains(contact)) {
            mChoosedContacts.remove(contact);
        } else {
            mChoosedContacts.add(contact);

        }
    }
}
