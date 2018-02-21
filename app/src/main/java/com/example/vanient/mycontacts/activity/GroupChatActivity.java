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

    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        StringBuilder members = new StringBuilder("Group member:" + "\n");
        TextView memberName = findViewById(R.id.membername);

        groupId = getIntent().getStringExtra("groupId");
        int position = getIntent().getIntExtra("position", -1);

        if (position != -1) {
            Group group = GroupDisplayActivity.groupsList.get(position);

            ArrayList<Group> mem = GroupDisplayActivity.groupList.get(group);
            if (mem != null) {
                for (Group aMem : mem) {
                    members.append(aMem.getPhDisplayName()).append("\n");
                }
            }

            memberName.setText(members.toString());
        }

        Button toGroup = (Button) findViewById(R.id.togroup);
        Button mEditGroup = (Button) findViewById(R.id.group_edit);

        mEditGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupChatActivity.this, ContactsDisplayActivity.class);
                i.putExtra("EDIT", true);
                i.putExtra("groupId", groupId);
                startActivity(i);
            }
        });

        toGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(GroupChatActivity.this, GroupDisplayActivity.class);
                startActivity(j);
            }
        });
    }
}
