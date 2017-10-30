package com.example.vishal.vtalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

public class DisplayContacts extends AppCompatActivity {
    private ListView mContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contacts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContactList=(ListView)findViewById(R.id.display_contacts_list);
        List<Contact> contactList;

        Intent i = getIntent();

        contactList = (List<Contact>) i.getSerializableExtra("contacts");

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.contact_list_row, contactList);

        mContactList.setAdapter(customAdapter);
    }

}
