package com.example.vanient.mycontacts.activity;

import java.util.ArrayList;

import com.example.vanient.contacts.R;
import com.example.vanient.mycontacts.domain.util.GroupRead;
import com.example.vanient.mycontacts.domain.entity.Group;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GroupmemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmember);

        String sposition = getIntent().getStringExtra("position");
        int position = Integer.parseInt(sposition);

        TextView membername = findViewById(R.id.membername);

        Button togroup = (Button) findViewById(R.id.togroup);
        togroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(GroupmemberActivity.this, GroupRead.class);
                startActivity(j);
            }
        });

        Group keyx = GroupRead.groupsList.get(position);
        StringBuilder members = new StringBuilder("Group member:" + "\n");
        ArrayList<Group> mem = GroupRead.groupList.get(keyx);
        for (Group aMem : mem) {
            members.append(aMem.getPhDisplayName()).append("\n");
        }

        membername.setText(members.toString());

    }
}
