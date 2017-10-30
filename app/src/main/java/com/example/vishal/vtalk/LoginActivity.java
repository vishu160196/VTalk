package com.example.vishal.vtalk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    public static String token;
    public static Integer userId;

    public static Retrofit retrofit;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mRegisterName;
    private EditText mRegisterUsername;
    private EditText mRegisterPassword;
    private EditText mRegisterEmail;
    final public String BASE_URL = "https://ddc0b860.ngrok.io/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set request options for all requests
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        );

        // create retrofit adapter
        retrofit =
                builder
                        .build();

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        //populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mRegisterEmail=(EditText) findViewById(R.id.register_email);
        mRegisterName=(EditText) findViewById(R.id.register_name);
        mRegisterUsername=(EditText) findViewById(R.id.register_username);
        mRegisterPassword=(EditText) findViewById(R.id.register_password);

        Button mRegisterButton = (Button) findViewById(R.id.email_register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout registrationForm = (LinearLayout) findViewById(R.id.registration_form);
                registrationForm.setVisibility(View.VISIBLE);
                if(isFormValid()){
                    registerNewUser();
                }

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private boolean isFormValid() {
        // Reset errors.
        mRegisterName.setError(null);
        mRegisterUsername.setError(null);
        mRegisterPassword.setError(null);
        mRegisterEmail.setError(null);

        // Store values at the time of the login attempt.
        String name = mRegisterName.getText().toString();
        String username = mRegisterUsername.getText().toString();
        String password = mRegisterPassword.getText().toString();
        String email = mRegisterEmail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            mRegisterEmail.setError(getString(R.string.error_field_required));
            focusView = mRegisterEmail;
            cancel = true;
        }



        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mRegisterPassword.setError(getString(R.string.error_invalid_password));
            focusView = mRegisterPassword;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mRegisterUsername.setError(getString(R.string.error_field_required));
            focusView = mRegisterUsername;
            cancel = true;
        }

        // Check for a valid name.
        if (TextUtils.isEmpty(name)) {
            mRegisterName.setError(getString(R.string.error_field_required));
            focusView = mRegisterName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        return !cancel;
    }

    private void registerNewUser() {


        Register signUpClient = retrofit.create(Register.class);

        Call<RegisterResponse> call = signUpClient.registerNewUser(new RegistrationForm(mRegisterName.getText().toString(), mRegisterEmail.getText().toString(), mRegisterUsername.getText().toString(), mRegisterPassword.getText().toString()));
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.internal_server_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        private Response<LoginResponse> response;
        @Override
        protected Boolean doInBackground(Void... params) {


            Login signUpClient = retrofit.create(Login.class);

            Call<LoginResponse> call = signUpClient.loginUser(new LoginForm(mEmail, mPassword));
            response=null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(response !=null){

                if (response.isSuccessful()) {
                    return true;
                }

                else{
                    return false;
                }
            }

            return false;


        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if(response==null){
                Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (success) {
                // start LoginActivity
                token = "JWT " + response.body().getToken();
                userId = response.body().getId();
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
            } else {
                try{
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }catch(NullPointerException e){
                    Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                }
            }



        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

