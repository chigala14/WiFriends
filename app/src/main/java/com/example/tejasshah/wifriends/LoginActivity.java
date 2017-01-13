package com.example.tejasshah.wifriends;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    android.support.v7.app.ActionBar actionBar;
    Button bLogin;
    TextView registerLink;
    EditText etPassword;
    EditText etUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_login);

     /*   if (isLogin()){

            AccessToken ac = getAccessToken();
            final String userId = ac.getUserId();
            System.out.println("test"+ userId);
            //
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            Toast.makeText(getBaseContext(), "Logged In Successfully !!!", Toast.LENGTH_SHORT).show();
                            String name = jsonResponse.getString("name");
                            String email = jsonResponse.getString("email");
                            System.out.println(email);
                            Intent intent = new Intent(LoginActivity.this, Home.class);
                            intent.putExtra("name", name);
                            intent.putExtra("username", userId);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();


                        } else {

                            Snackbar.make(getCurrentFocus(), "Login Failed", Snackbar.LENGTH_LONG).show();
                                    /*AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("Login Failed")
                                            .setNegativeButton("Retry",null)
                                            .create()
                                            .show();*/ /*
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            };
            FBRequest fb = new FBRequest(userId, responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(fb);
            //


        } */
        etPassword = (EditText) findViewById(R.id.etPassword);
        etUsername = (EditText) findViewById(R.id.etUsername);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email");
        getLoginDetails(loginButton);
        bLogin = (Button) findViewById(R.id.bLogin);

        registerLink = (TextView) findViewById(R.id.tvRegisterHere);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);

            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                Boolean isInternetPresent = cd.isConnectingToInternet();
                if(!isInternetPresent){
                    Snackbar.make(getCurrentFocus(),"Please Connect to the Internet",Snackbar.LENGTH_LONG)
                            .setAction("Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(i);
                                }
                            }).show();
                }
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                String p = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
                ErrorDialogue ed = new ErrorDialogue();
                if (username.isEmpty()) {
                    ed.showErrorText("Please Enter a UserName", LoginActivity.this);
                } else if (password.isEmpty()) {
                    ed.showErrorText("Please Enter a Password", LoginActivity.this);
                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    Toast.makeText(getBaseContext(), "Logged In Successfully !!!", Toast.LENGTH_SHORT).show();
                                    String name = jsonResponse.getString("name");
                                    String email = jsonResponse.getString("email");
                                    System.out.println(email);
                                    Intent intent = new Intent(LoginActivity.this, Home.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("username", username);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                    finish();


                                } else {

                                    Snackbar.make(getCurrentFocus(), "Login Failed", Snackbar.LENGTH_LONG).show();
                                    /*AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("Login Failed")
                                            .setNegativeButton("Retry",null)
                                            .create()
                                            .show();*/
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    };
                    LoginRequest loginRequest = new LoginRequest(username, p, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);
                }


            }
        });


    }

    public boolean isLogin() {
        AccessToken accessToken = getAccessToken();
        if (accessToken == null) {
            return false;
        }
        return !accessToken.isExpired();
    }

    public AccessToken getAccessToken() {
        return AccessToken.getCurrentAccessToken();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    /*
 Initialize the facebook sdk.
 And then callback manager will handle the login responses.
*/
    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    /*
 Register a callback function with LoginButton to respond to the login result.
*/
    protected void getLoginDetails(LoginButton login_button){

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                AccessToken accessToken = login_result.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                GraphRequest request = GraphRequest.newMeRequest(
                        login_result.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("LoginActivity Response ", response.toString());

                                try {
                                    String fname = object.getString("name");
                                    String email = object.getString("email");
                                    String uname = object.getString("id");
                                    Log.v("Email = ", " " + uname);
                                    System.out.println("Email"+email);
                                    System.out.println("id : "+uname);
                                    //
                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                boolean success = jsonResponse.getBoolean("success");

                                                if (success) {
                                                    Toast.makeText(getBaseContext(), "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                                    String name = jsonResponse.getString("name");
                                                    String email = jsonResponse.getString("email");
                                                    String username = jsonResponse.getString("username");
                                                    Intent intent = new Intent(LoginActivity.this, Home.class);
                                                    intent.putExtra("name", name);
                                                    intent.putExtra("username", username);
                                                    intent.putExtra("email", email);
                                                    startActivity(intent);
                                                } else {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                                    builder.setMessage("Username Unavailable")
                                                            .setNegativeButton("Retry", null)
                                                            .create()
                                                            .show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };

                                    RegisterRequest registerRequest = new RegisterRequest(fname, email, uname, uname, responseListener);

                                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                                    queue.add(registerRequest);
                                    //



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data", data.toString());
    }

}