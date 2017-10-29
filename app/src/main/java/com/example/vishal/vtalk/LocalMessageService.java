package com.example.vishal.vtalk;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by vishal on 26/10/17.
 */

public class MessageService extends JobService{

    private MessageLoader mMessageLoader;

    @Override
    public boolean onStartJob(JobParameters params) {
        mMessageLoader = new MessageLoader();
        mMessageLoader.execute((Void) null);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public class MessageLoader extends AsyncTask<Void, Void, List<String>>{

        @Override
        protected List<String> doInBackground(Void... params) {
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
                    List<MessageResponse> messages = response.body();

                }
                else {
                    Toast.makeText(getApplicationContext(), response.body().get(0).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }


        }
    }
}
