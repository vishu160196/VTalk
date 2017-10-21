package com.example.vishal.vtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vishal on 13/10/17.
 */

public class CustomAdapter extends ArrayAdapter {


    private int resId;
    public CustomAdapter(Context context, int resource, List<Contact> items) {
        super(context, resource, items);
        resId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(resId, null);
        }

        Contact p = (Contact)getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.name);
            TextView tt2 = (TextView) v.findViewById(R.id.username);

            if (tt1 != null) {
                tt1.setText(p.getName());
            }

            if (tt2 != null) {
                tt2.setText(p.getUsername());
            }
        }

        return v;
    }
}
