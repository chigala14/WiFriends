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

public class AddNetworkActivity extends AppCompatActivity {
    Button bAddNetwork;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_network);

        final EditText etNetworkName = (EditText)findViewById(R.id.etNetworkName);
        final EditText etNetworkPassword = (EditText)findViewById(R.id.etNetworkPassword);

        bAddNetwork = (Button) findViewById(R.id.bSaveNetwork);
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");
        final String name = intent.getStringExtra("name");

        bAddNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String wname = etNetworkName.getText().toString();
                final String wpass = etNetworkPassword.getText().toString();
                String epass  = Base64.encodeToString(wpass.getBytes(), Base64.DEFAULT);
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success){
                                Toast.makeText(getBaseContext(),"Network Added Successfully",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddNetworkActivity.this, Home.class);
                                intent.putExtra("name",name);
                                intent.putExtra("username",username);
                                intent.putExtra("email",email);
                                AddNetworkActivity.this.startActivity(intent);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddNetworkActivity.this);
                                builder.setMessage("Network Already Registered")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                AddNetwork addNetwork = new AddNetwork(username, wname,epass, responseListener);
                RequestQueue queue = Volley.newRequestQueue(AddNetworkActivity.this);
                queue.add(addNetwork);

            }
        });
    }
}
