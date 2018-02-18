package com.example.vanient.mycontacts.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.activity.listener.RecyclerItemClickListener;
import com.example.vanient.mycontacts.domain.adapter.GroupAdapter;
import com.example.vanient.mycontacts.domain.entity.Group;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

/**
 * Created by Vanient on 2018/2/3.
 */

public class GroupDisplayActivity extends AppCompatActivity {
    public static LinkedHashMap<Group, ArrayList<Group>> groupList = new LinkedHashMap<Group, ArrayList<Group>>();
    public static ArrayList<Group> groupsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readgroup_main);
        Button returncontacts = findViewById(R.id.returncontacts);
        Button mCreateGroup = findViewById(R.id.creategroup);
        RecyclerView showgroup = (RecyclerView) findViewById(R.id.showgroup);

        groupsList = fetchGroups();
        for (Group group : groupsList) {
            String[] ids = group.getGroupid().split(",");
            ArrayList<Group> groupMembers = new ArrayList<>();
            for (String groupId : ids) {
                groupMembers.addAll(fetchGroupMembers(groupId));
            }
            group.setGroupName(group.getGroupName() + " (" + groupMembers.size() + ")");
            groupList.put(group, groupMembers);
        }
        GroupAdapter groupAdapter = new GroupAdapter(groupsList, getApplicationContext());
        showgroup.setLayoutManager(new LinearLayoutManager(this));
        showgroup.setAdapter(groupAdapter);

        returncontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(GroupDisplayActivity.this, ContactsDisplayActivity.class);
                startActivity(j);
            }
        });

        mCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupDisplayActivity.this, GroupAddActivity.class);
                startActivity(i);
            }
        });

        showgroup.addOnItemTouchListener(new RecyclerItemClickListener(GroupDisplayActivity.this, showgroup, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(GroupDisplayActivity.this, GroupChatActivity.class);
                i.putExtra("position", position + "");
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private ArrayList<Group> fetchGroups() {
        ArrayList<Group> groupList = new ArrayList<>();
        String[] projection = new String[]{ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};
        Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
                projection, null, null, null);
        ArrayList<String> groupTitle = new ArrayList<>();
        while (cursor.moveToNext()) {
            Group item = new Group();
            item.setGroupid(cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID)));
            String groupName = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));

            if (groupName.contains("Group:")) {
                groupName = groupName.substring(groupName.indexOf("Group:") + "Group:".length()).trim();
            }


            if (groupName.contains("Favorite_")) {
                groupName = "Favorite";
            }

            if (groupName.contains("Starred in Android") || groupName.contains("My Contacts")) {
                continue;
            }

            if (groupTitle.contains(groupName)) {
                for (Group group : groupList) {
                    if (group.getGroupName().equals(groupName)) {
                        group.setGroupid(group.getGroupid() + "," + item.getGroupid());
                        break;
                    }
                }
            } else {
                groupTitle.add(groupName);
                item.setGroupName(groupName);
                groupList.add(item);
            }

        }

        cursor.close();

        Collections.sort(groupList, new Comparator<Group>() {
            @Override
            public int compare(Group group1, Group group2) {
                return group2.getGroupName().compareTo(group1.getGroupName()) < 0
                        ? 0 : -1;
            }
        });
        return groupList;
    }

    private ArrayList<Group> fetchGroupMembers(String groupId) {
        ArrayList<Group> groupMembers = new ArrayList<>();
        String where = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=" + groupId
                + " AND "
                + ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "='"
                + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID, ContactsContract.Data.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, where, null,
                ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        while (cursor.moveToNext()) {
            Group group = new Group();
            group.setGroupName(cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));
            group.setGroupid(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID)));
            Cursor phoneFetchCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.TYPE},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + group.getGroupid(), null, null);
            while (phoneFetchCursor.moveToNext()) {
                group.setPhNo(phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                group.setPhDisplayName(phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                group.setPhType(phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                group.setEmail(phoneFetchCursor.getString(phoneFetchCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
            }
            phoneFetchCursor.close();
            groupMembers.add(group);
        }
        cursor.close();
        return groupMembers;
    }


}
