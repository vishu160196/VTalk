package com.example.vishal.vtalk;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatWindow extends AppCompatActivity implements View.OnClickListener{
    private static LinearLayout mMessageBox;
    private static int id;

    private EditText mMessageToBeSent;
    private int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        JobScheduler mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        mMessageToBeSent = (EditText)findViewById(R.id.message_to_be_sent);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMessageBox = (LinearLayout) findViewById(R.id.message_box);
        Intent i = getIntent();

        String username = i.getExtras().getString("username");
        SQLiteDatabase db = MainActivity.mDbHelper.getReadableDatabase();

        Cursor cursor;

        cursor=db.rawQuery("select contact_id from contact where username = "+ username +";", null);
        cursor.moveToNext();
        id=cursor.getInt(cursor.getColumnIndexOrThrow(Contacts.FeedEntry.contact_id));

        cursor.close();


        // fetch new messages from message_info
        JobInfo.Builder builder = new JobInfo.Builder( ++jobId,
                new ComponentName( getPackageName(), LocalMessageService.class.getName() ) );

        builder.setPeriodic( 1000 );

        mJobScheduler.schedule( builder.build() );

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab:
                try{
                    sendMessage();
                }catch(EmptyMessageBoxException e){
                    mMessageToBeSent.setError(e.getError());
                    mMessageToBeSent.requestFocus();
                }
        }
    }

    private void sendMessage() throws EmptyMessageBoxException{
        String content=mMessageToBeSent.getText().toString();

        if(TextUtils.isEmpty(content)){
            throw new EmptyMessageBoxException("Empty message Box");
        }
        SQLiteDatabase db = MainActivity.mDbHelper.getWritableDatabase();
        Date time=new Date();
        Cursor cursor=db.rawQuery("insert into message_info(content,state,receiver_id,time) values('"+content+"', "+"1, "+id+", "+time, null);
        cursor.close();

        SendMessage client=LoginActivity.retrofit.create(SendMessage.class);
        Call<String> call=client.sendMessage(content, LoginActivity.token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), response.body(), Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(getApplicationContext(), response.body(), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static List<Message> getMessagesFromLocalDb(){
        SQLiteDatabase db = MainActivity.mDbHelper.getReadableDatabase();
        List<Message> messages = new ArrayList<>();
        Cursor cursor = db.rawQuery("select content, state from message_info where sender_id="+id+" or receiver_id="+id+" order by time " +
                "desc;", null);

        while(cursor.moveToNext()) {
            String content = cursor.getString(
                    cursor.getColumnIndexOrThrow(Messages.FeedEntry.content));

            Integer state = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Messages.FeedEntry.state));

            messages.add(new Message(content, state));
        }
        cursor.close();
        return messages;
    }

    public static void displayMessages(List<Message> result, Context ctx){
        mMessageBox.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        for (Message m:result
                ) {

            if(m.getState()==1){
                // sent message
                TextView messageBox = (TextView)inflater.inflate(R.layout.sent_message, mMessageBox, false);
                messageBox.setText(m.getContent());
                mMessageBox.addView(messageBox);
            }
            else{
                // received message
                TextView messageBox = (TextView)inflater.inflate(R.layout.received_message, mMessageBox, false);
                messageBox.setText(m.getContent());
                mMessageBox.addView(messageBox);
            }
        }
    }

    private class EmptyMessageBoxException extends Throwable {
        private String error;

        public String getError() {
            return error;
        }

        public EmptyMessageBoxException(String error) {
            this.error = error;
        }
    }
}
