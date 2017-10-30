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

import java.util.ArrayList;
import java.util.List;


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
