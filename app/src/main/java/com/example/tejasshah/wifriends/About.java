package com.example.tejasshah.wifriends;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class About extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;
    String name,username,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            Intent i = new Intent(About.this,ViewWiFiActivity.class);
                            i.putExtra("jsonResponse",response);
                            i.putExtra("name",name);
                            i.putExtra("username",username);
                            i.putExtra("email",email);
                            startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.make(getCurrentFocus(), "Error trying to retrive your Friend's Wifi !!", Snackbar.LENGTH_LONG).show();
                        }
                    }
                };
                ViewWiFi vw = new ViewWiFi(username,email, name, responseListener);
                RequestQueue queue = Volley.newRequestQueue(About.this);
                queue.add(vw);
            }
        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        TextView tvUser_Name = (TextView) headerLayout.findViewById(R.id.tvName);
        TextView tvUser_Email = (TextView) headerLayout.findViewById(R.id.tvEmail_Id);
        tvUser_Email.setText(email);
        tvUser_Name.setText(name);
    }

    private void  modifyNetwork(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    System.out.println(success);
                    if(success){
                        String name = jsonResponse.getString("name");
                        String email = jsonResponse.getString("email");
                        String wname = jsonResponse.getString("wname");
                        String wpass = jsonResponse.getString("wpass");
                        String username = jsonResponse.getString("username");
                        Intent intent = new Intent(About.this, ModifyNetworkActivity.class);
                        intent.putExtra("name",name);
                        intent.putExtra("username",username);
                        intent.putExtra("email",email);
                        //intent.putExtra("wid",wid);
                        intent.putExtra("wname",wname);
                        intent.putExtra("wpass",wpass);
                        startActivity(intent);
                    }else{
                        ErrorDialogue ed = new ErrorDialogue();
                        ed.showErrorText("No Network Registered",About.this);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ModifyNetwork modNetwork = new ModifyNetwork(username,email, name, responseListener);
        RequestQueue queue = Volley.newRequestQueue(About.this);
        queue.add(modNetwork);
    }

    class LoadData extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);


                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", username);
                String query = builder.build().getEncodedQuery();
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                //readStream(in);

                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                //JSONArray jArr = new JSONArray(buffer.toString());
                return buffer.toString();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException i) {
                i.printStackTrace();
            }finally {
                if(connection!= null){
                    connection.disconnect();
                }
            }try{
                reader.close();
            }catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent registerIntent = new Intent(About.this, SearchFriendsActivity.class);
            registerIntent.putExtra("JSONdata",s);
            registerIntent.putExtra("name", name);
            registerIntent.putExtra("username", username);
            registerIntent.putExtra("email", email);
            startActivity(registerIntent);

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_about){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.icn_AddNetwork) {

            Intent addNetworkIntent = new Intent(About.this, AddNetworkActivity.class);
            addNetworkIntent.putExtra("username", username);
            addNetworkIntent.putExtra("name", name);
            addNetworkIntent.putExtra("email", email);
            startActivity(addNetworkIntent);
        } else if (id == R.id.icn_ModifyNetwork) {
            modifyNetwork();

        } else if (id == R.id.icn_AddFriend) {
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
            }else{
                new LoadData().execute("http://selvinphp.netau.net/ViewFriends.php");
            }

        } else if (id == R.id.icn_Myfriends) {
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
            }else{
                Intent i = new Intent(About.this,MyFriendsActivity.class);
                i.putExtra("name", name);
                i.putExtra("username", username);
                i.putExtra("email", email);
                startActivity(i);
            }
        } else if (id == R.id.icn_myProfile) {

        } else if (id == R.id.icn_LogOut) {
            Intent i = new Intent(About.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }else{

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
