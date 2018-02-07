package com.example.vanient.mycontacts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vanient.contacts.R;

import java.util.ArrayList;

public class Groupmember extends AppCompatActivity {
    Button togroup;
    TextView membername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmember);

        String sposition=getIntent().getStringExtra("position");
        int position=Integer.parseInt(sposition);

        membername = findViewById(R.id.membername);

        togroup=(Button) findViewById(R.id.togroup);
        togroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j=new Intent(Groupmember.this, Readgroup.class);
                startActivity(j);
            }
        });

        Group keyx = Readgroup.groupsList.get(position);
        String members = "Group member:" + "\n";
        ArrayList<Group> mem = Readgroup.groupList.get(keyx);
        for(int i=0;i<mem.size();i++){
            members = members + mem.get(i).phDisplayName + "\n";
        }

        membername.setText(members);

    }
}
