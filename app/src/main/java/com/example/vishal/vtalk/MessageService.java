package com.example.vishal.vtalk;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by vishal on 28/10/17.
 */

public class MessageService extends JobService {
    private MessageLoader mMessageLoader;


    @Override
    public boolean onStartJob(JobParameters params) {
        mMessageLoader = new MessageLoader(this);
Log.d("debug2","job started");
        mMessageLoader.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public class MessageLoader extends AsyncTask<JobParameters, Void, JobParameters> {

        private final JobService jobService;

        public MessageLoader(JobService jobService) {

            this.jobService = jobService;
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            Log.d("debug2","job in bg");
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


            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters params) {
            Log.d("debug2","job in post execute");
            jobService.jobFinished(params, false);
        }
    }


}
