package com.example.vanient.mycontacts;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.vanient.contacts.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * Created by Vanient on 2018/2/3.
 */

public class Readgroup extends AppCompatActivity {
    Button creategroup;
    Button returncontacts;
    RecyclerView showgroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readgroup_main);
        returncontacts=findViewById(R.id.returncontacts);
        showgroup = (RecyclerView) findViewById(R.id.showgroup);

        LinkedHashMap<Item,ArrayList<Item>> groupList = new LinkedHashMap<Item,ArrayList<Item>>();
        ArrayList<Item> groupsList = fetchGroups();
        for(Item item:groupsList){
            String[] ids = item.groupid.split(",");
            ArrayList<Item> groupMembers =new ArrayList<Item>();
            for(int i=0;i<ids.length;i++){
                String groupId = ids[i];
                groupMembers.addAll(fetchGroupMembers(groupId));
            }
            item.groupName = item.groupName +" ("+groupMembers.size()+")";
            groupList.put(item,groupMembers);
        }
        ItemsAdapter itemsAdapter = new ItemsAdapter(groupsList, getApplicationContext());
        showgroup.setLayoutManager(new LinearLayoutManager(this));
        showgroup.setAdapter(itemsAdapter);

        returncontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j=new Intent(Readgroup.this,AllContacts.class);
                startActivity(j);
            }
        });
    }

    private ArrayList<Item> fetchGroups(){
        ArrayList<Item> groupList = new ArrayList<Item>();
        String[] projection = new String[]{ContactsContract.Groups._ID,ContactsContract.Groups.TITLE};
        Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
                projection, null, null, null);
        ArrayList<String> groupTitle = new ArrayList<String>();
        while(cursor.moveToNext()){
            Item item = new Item();
            item.groupid = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));
            String groupName =      cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));

            if(groupName.contains("Group:"))
                groupName = groupName.substring(groupName.indexOf("Group:")+"Group:".length()).trim();

            if(groupName.contains("Favorite_"))
                groupName = "Favorite";

            if(groupName.contains("Starred in Android") || groupName.contains("My Contacts"))
                continue;

            if(groupTitle.contains(groupName)){
                for(Item group:groupList){
                    if(group.groupName.equals(groupName)){
                        group.groupid += ","+item.groupid;
                        break;
                    }
                }
            }else{
                groupTitle.add(groupName);
                item.groupName = groupName;
                groupList.add(item);
            }

        }

        cursor.close();
        Collections.sort(groupList,new Comparator<Item>() {
            public int compare(Item item1, Item item2) {
                return item2.groupName.compareTo(item1.groupName)<0
                        ?0:-1;
            }
        });
        return groupList;
    }

    private ArrayList<Item> fetchGroupMembers(String groupId){
        ArrayList<Item> groupMembers = new ArrayList<Item>();
        String where =  ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID +"="+groupId
                +" AND "
                + ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE+"='"
                + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE+"'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID, ContactsContract.Data.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, where,null,
                ContactsContract.Data.DISPLAY_NAME+" COLLATE LOCALIZED ASC");
        while(cursor.moveToNext()){
            Item item = new Item();
            item.groupName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            item.groupid = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID));
            Cursor phoneFetchCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.TYPE},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+item.groupid,null,null);
            while(phoneFetchCursor.moveToNext()){
                item.phNo = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                item.phDisplayName = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                item.phType = phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            }
            phoneFetchCursor.close();
            groupMembers.add(item);
        }
        cursor.close();
        return groupMembers;
    }



}
