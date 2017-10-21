package com.example.vishal.vtalk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Created by vishal on 13/10/17.
 */

public class AddNewContact extends DialogFragment {
    public static AddNewContact newContact(String title) {
        AddNewContact frag = new AddNewContact();
        Bundle args = new Bundle();
        args.putString("title", title);

        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");
        //final Integer id = getArguments().getInt("id");

        //final Integer expense = getArguments().getInt("expense");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(R.layout.dialog_new_contact)
                .setPositiveButton(R.string.add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String name = ((EditText)getDialog().findViewById(R.id.new_contact_name))
                                        .getText().toString();
                                String username = ((EditText)getDialog().findViewById(R.id.new_contact_username))
                                        .getText().toString();


                                ((MainActivity)getActivity()).doPositiveClick(name, username);
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();
    }
}
