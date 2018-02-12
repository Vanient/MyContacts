package com.example.vanient.mycontacts.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.domain.entity.Contact;
import com.example.vanient.mycontacts.domain.entity.Group;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GroupChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        StringBuilder members = new StringBuilder("Group member:" + "\n");
        TextView membername = findViewById(R.id.membername);

        Button togroup = (Button) findViewById(R.id.togroup);
        Button mEditGroup = (Button) findViewById(R.id.group_edit);

        togroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(GroupChatActivity.this, GroupDisplayActivity.class);
                startActivity(j);
            }
        });

        mEditGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupChatActivity.this, ContactsDisplayActivity.class);
                i.putExtra("EDIT", true);
                startActivity(i);
            }
        });

        if (getIntent().getStringExtra("position") != null) {

            String sposition = getIntent().getStringExtra("position");
            int position = Integer.parseInt(sposition);


            Group keyx = GroupDisplayActivity.groupsList.get(position);

            ArrayList<Group> mem = GroupDisplayActivity.groupList.get(keyx);
            for (Group aMem : mem) {
                members.append(aMem.getPhDisplayName()).append("\n");
            }

            membername.setText(members.toString());
        } else {
            Intent i = getIntent();
            List<Contact> contacts = (List<Contact>) i.getSerializableExtra("CONTACTLIST");
            if (contacts != null) {
                members = new StringBuilder("Group member:" + "\n");
                for (Contact contact : contacts) {
                    members.append(contact.getName()).append("\n");
                }
                membername.setText(members.toString());
            }
        }
    }
}
