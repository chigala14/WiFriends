package com.example.tejasshah.wifriends;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    Button bRegister;
    Button bCancel;
    EditText etUsername;
    EditText etPassword;
    EditText etName;
    EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = ( EditText) findViewById(R.id.etEmail);
        etName = ( EditText) findViewById(R.id.etName);
        etPassword = ( EditText) findViewById(R.id.etPassword);
        etUsername = ( EditText) findViewById(R.id.etUsername);

        bRegister = (Button) findViewById(R.id.bRegister);
        bCancel = (Button) findViewById(R.id.button);

        bCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(i);

            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString();
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                String p = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
                final String email = etEmail.getText().toString();
                ErrorDialogue ed = new ErrorDialogue();
                if(name.isEmpty()){
                    ed.showErrorText("Please Enter the Name",RegisterActivity.this);
                }else if(username.isEmpty()){
                    ed.showErrorText("Please Enter a UserName",RegisterActivity.this);
                }else if (password.isEmpty()){
                    ed.showErrorText("Please Enter the Password",RegisterActivity.this);
                }else if(email.isEmpty()){
                    ed.showErrorText("Please Enter the Email ID",RegisterActivity.this);
                }else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {
                                    Toast.makeText(getBaseContext(), "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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

                    RegisterRequest registerRequest = new RegisterRequest(name, email, username, p, responseListener);

                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(registerRequest);
                }
            }
        });



    }


}
