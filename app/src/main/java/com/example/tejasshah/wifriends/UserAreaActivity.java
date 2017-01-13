package com.example.tejasshah.wifriends;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserAreaActivity extends AppCompatActivity {
    Button bAddNetwork;
    Button bModify,bWiFi,bLogout,bViewFriend;
    android.support.v7.app.ActionBar actionBar;
    String name,username,email;

    // drawer fields
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        final EditText etUsername = ( EditText) findViewById(R.id.etUsername);
        final EditText etEmail = ( EditText) findViewById(R.id.etEmail);
        final TextView welcomeMessage = (TextView) findViewById(R.id.tvWelcomeMsg);
        etUsername.setEnabled(false);
        etEmail.setEnabled(false);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");

        // drawer

        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.user_icon);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        String message = "Welcome " + name  ;
        welcomeMessage.setText(message);
        etUsername.setText(username);
        etEmail.setText(email);
        bWiFi = (Button)findViewById(R.id.bWiFi);
        bAddNetwork = (Button)findViewById(R.id.bAddNetwork);
        bLogout = (Button)findViewById(R.id.bLogout);
        bLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserAreaActivity.this, LoginActivity.class);
                UserAreaActivity.this.startActivity(i);

            }
        });


        bAddNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNetworkIntent = new Intent(UserAreaActivity.this, AddNetworkActivity.class);
                addNetworkIntent.putExtra("username", username);
                addNetworkIntent.putExtra("name", name);
                addNetworkIntent.putExtra("email", email);
                UserAreaActivity.this.startActivity(addNetworkIntent);

            }
        });
       bModify = (Button)findViewById(R.id.bModify);
        bModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                //String wid = jsonResponse.getString("wid");
                                String wname = jsonResponse.getString("wname");
                                String wpass = jsonResponse.getString("wpass");
                                String username = jsonResponse.getString("username");
                                //System.out.println(name + email + wname + wpass + username);
                                Intent intent = new Intent(UserAreaActivity.this, ModifyNetworkActivity.class);
                                intent.putExtra("name",name);
                                intent.putExtra("username",username);
                                intent.putExtra("email",email);
                                //intent.putExtra("wid",wid);
                                intent.putExtra("wname",wname);
                                intent.putExtra("wpass",wpass);
                                UserAreaActivity.this.startActivity(intent);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                                builder.setMessage("No Network Registered")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ModifyNetwork modNetwork = new ModifyNetwork(username,email, name, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
                queue.add(modNetwork);

            }
        });

        bViewFriend = (Button)findViewById(R.id.bViewFriend);
        bViewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserAreaActivity.this, AddFriends.class);
                i.putExtra("name", name);
                i.putExtra("username", username);
                i.putExtra("email", email);
                startActivity(i);

            }
        });



        bWiFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            System.out.println(success);
                            if(success){
                                String name = jsonResponse.getString("name");
                                String email = jsonResponse.getString("email");
                                //String wid = jsonResponse.getString("wid");
                                String wname = jsonResponse.getString("wname");
                                String wpass = jsonResponse.getString("wpass");
                                String name1 = jsonResponse.getString("name1");
                                String username = jsonResponse.getString("username");
                               // System.out.println(name + email + wname + wpass + username);
                                Intent intent = new Intent(UserAreaActivity.this, ViewWiFiActivity.class);
                                intent.putExtra("name",name);
                                intent.putExtra("username",username);
                                intent.putExtra("email",email);
                                intent.putExtra("name1",name1);
                                intent.putExtra("wname",wname);
                                intent.putExtra("wpass",wpass);
                                startActivity(intent);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                                builder.setMessage("No WIFI Found for you !!")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ViewWiFi vw = new ViewWiFi(username,email, name, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
                queue.add(vw);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch(item.getItemId())
        {
            case R.id.connectWifi:
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            System.out.println(success);
                            if(success){
                                String name = jsonResponse.getString("name");
                                String email = jsonResponse.getString("email");
                                String wname = jsonResponse.getString("wname");
                                String wpass = jsonResponse.getString("wpass");
                                String name1 = jsonResponse.getString("name1");
                                String username = jsonResponse.getString("username");
                                // System.out.println(name + email + wname + wpass + username);
                                Intent intent = new Intent(UserAreaActivity.this, ViewWiFiActivity.class);
                                intent.putExtra("name",name);
                                intent.putExtra("username",username);
                                intent.putExtra("email",email);
                                intent.putExtra("name1",name1);
                                intent.putExtra("wname",wname);
                                intent.putExtra("wpass",wpass);
                                startActivity(intent);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                                builder.setMessage("No WIFI Found for you !!")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ViewWiFi vw = new ViewWiFi(username,email, name, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
                queue.add(vw);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDrawerItems() {
        String[] osArray = { "Android", "iOS", "Windows", "OS X", "Linux" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UserAreaActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Options!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
