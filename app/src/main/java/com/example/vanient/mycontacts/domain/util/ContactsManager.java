package com.example.vanient.mycontacts.domain.util;

import java.util.ArrayList;

import com.example.vanient.mycontacts.domain.entity.Contact;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactsManager {

    private ContentResolver contentResolver;
    private static final String TAG = "ContactsManager";

    private static final String COLUMN_RAW_CONTACT_ID =
            ContactsContract.Data.RAW_CONTACT_ID;
    private static final String COLUMN_MIMETYPE =
            ContactsContract.Data.MIMETYPE;
    private static final String COLUMN_NAME =
            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME;
    private static final String COLUMN_EMAIL =
            ContactsContract.CommonDataKinds.Email.DATA;
    private static final String COLUMN_EMAIL_TYPE =
            ContactsContract.CommonDataKinds.Email.TYPE;
    private static final String MIMETYPE_STRING_NAME =
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
    private static final String MIMETYPE_STRING_EMAIL =
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;

    public ContactsManager(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }


    /**
     * @param name The contact who you get the id from. The name of
     *             the contact should be set.
     * @return 0 if contact not exist in contacts list. Otherwise return
     * the id of the contact.
     */
    private String getContactID(String name) {
        String id = "0";
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID},
                ContactsContract.Contacts.DISPLAY_NAME +
                        "='" + name + "'", null, null);
        if (cursor.moveToNext()) {
            id = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts._ID));
        }
        cursor.close();
        return id;
    }

    public void addContact(Contact contact) {
        Log.w(TAG, "**add start**");
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        String id = getContactID(contact.getName());

        if (!"0".equals(id)) {
            Log.d(TAG, "contact already exist. exit.");
        } else if ("".equals(contact.getName().trim())) {
            Log.d(TAG, "contact name is empty. exit.");
        } else {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                    .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_NAME)
                    .withValue(COLUMN_NAME, contact.getName())
                    .build());
            Log.d(TAG, "add name: " + contact.getName());

            if (!"".equals(contact.getEmail().trim())) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(COLUMN_RAW_CONTACT_ID, 0)
                        .withValue(COLUMN_MIMETYPE, MIMETYPE_STRING_EMAIL)
                        .withValue(COLUMN_EMAIL, contact.getEmail())
                        .withValue(COLUMN_EMAIL_TYPE, contact.getEmailType())
                        .build());
                Log.d(TAG, "add email: " + contact.getEmail());
            }

            try {
                contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                Log.d(TAG, "add contact success.");
            } catch (Exception e) {
                Log.d(TAG, "add contact failed.");
                Log.e(TAG, e.getMessage());
            }
        }
        Log.w(TAG, "**add end**");
    }
}
