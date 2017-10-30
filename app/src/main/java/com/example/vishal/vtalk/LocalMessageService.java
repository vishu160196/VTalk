package com.example.vishal.vtalk;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by vishal on 26/10/17.
 */

public class LocalMessageService extends JobService{

    private LocalMessageService.MessageLoader mMessageLoader;



    @Override
    public boolean onStartJob(JobParameters params) {
        mMessageLoader = new MessageLoader(this);
        Log.d("debug","job started");
        mMessageLoader.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public class MessageLoader extends AsyncTask<JobParameters, Void, List<Message>> {

        private JobService jobService;
        private JobParameters jobParameters;

        public MessageLoader(JobService jobService) {

            this.jobService = jobService;
        }

        @Override
        protected List<Message> doInBackground(JobParameters... params) {
            this.jobParameters=params[0];
            Log.d("debug","job in bg");

            MessageClient client = LoginActivity.retrofit.create(MessageClient.class);
            Call<List<MessageResponse>> call = client.getMessages(LoginActivity.userId, LoginActivity.token);
            Response<List<MessageResponse>> response=null;
            try{
                response = call.execute();
            }catch (IOException e) {
                e.printStackTrace();
            }
            if(response !=null){
                if(response.isSuccessful()){
                    // add messages to message_info
                    List<MessageResponse> message= response.body();
                    SQLiteDatabase db = MainActivity.mDbHelper.getWritableDatabase();
                    for(MessageResponse m:message){
                        try {
                            db.execSQL("insert into message_info(content, state, sender_id, time) values('" + m.getContent() +
                                    "', 0, " + m.getSender_id() + ", '" + m.getTime() + "');");
                        } catch (android.database.sqlite.SQLiteConstraintException e) {
                            e.printStackTrace();
                        }


                    }

                }
            }

            else {
                Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
            Log.d("debug2",response.body().toString());

            return ChatWindow.getMessagesFromLocalDb();
        }

        @Override
        protected void onPostExecute(List<Message> result){
            Log.d("debug","job on post execute");

            ChatWindow.displayMessages(result, getApplicationContext());
            jobService.jobFinished(jobParameters, false);
        }

    }

}
