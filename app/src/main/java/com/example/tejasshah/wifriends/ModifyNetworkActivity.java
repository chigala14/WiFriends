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

public class ModifyNetworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_network);

        final Button bUpdate = (Button) findViewById(R.id.bUpdate);
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");
        final String name = intent.getStringExtra("name");
        //final String wid = intent.getStringExtra("username");
        final String wname = intent.getStringExtra("wname");
        final String wpass = intent.getStringExtra("wpass");
        String epass = new String(Base64.decode(wpass,Base64.DEFAULT));
        final EditText etWName = ( EditText) findViewById(R.id.etWName);
        final EditText etWPass = ( EditText) findViewById(R.id.etWPass);

        etWName.setText(wname);
        etWPass.setText(epass);

        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String wname1 = etWName.getText().toString();
                final String wpass1 = etWPass.getText().toString();
                String epass1 = Base64.encodeToString(wpass1.getBytes(), Base64.DEFAULT);
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                Toast.makeText(getBaseContext(),"Network Details modified",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ModifyNetworkActivity.this, Home.class);
                                intent.putExtra("name", name);
                                intent.putExtra("username", username);
                                intent.putExtra("email", email);
                                ModifyNetworkActivity.this.startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyNetworkActivity.this);
                                builder.setMessage("Network Update failed. Please try again...")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                UpdateNetwork updateNetwork = new UpdateNetwork(username, wname1, epass1, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ModifyNetworkActivity.this);
                queue.add(updateNetwork);

            }
        });


    }
}
