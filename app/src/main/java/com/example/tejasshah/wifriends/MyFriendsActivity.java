package com.example.tejasshah.wifriends;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tejasshah.wifriends.models.Friends;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFriendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static ListView lvMyFriends;
    static String username,email,name;
    ArrayList<Friends> friendsList;
    List<Friends>friends_list;
    FloatingActionButton fab;
    private static MyFriendsAdapter adapt_myFriends;
    SearchView svMyFriends;
    int image = R.drawable.user_icon;
    TextView tvNoFriends;

    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        username = i.getStringExtra("username");
        email = i.getStringExtra("email");
        name = i.getStringExtra("name");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new LoadFriendsData().execute("http://selvinphp.netau.net/ViewFriends.php");

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

        lvMyFriends = (ListView)findViewById(R.id.lvMyFriends);
        svMyFriends = (SearchView) findViewById(R.id.sv_MyFriends);
        svMyFriends.setIconified(false);
        svMyFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapt_myFriends.getFilter().filter(newText);
                return false;
            }
        });

    }

    private class FetchMyFriends extends StringRequest {
        private static final String FETCH_FRIEND_URL = "http://selvinphp.netau.net/MyFriends.php";
        private Map<String, String> params;

        public FetchMyFriends( String username, Response.Listener<String> listener){
            super (Request.Method.POST, FETCH_FRIEND_URL, listener, null);
            params = new HashMap<>();
            params.put("username",username);

        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        final View v = findViewById(R.id.lvMyFriends);
        if(!isInternetPresent){
            Snackbar.make(v,"Unable to Load Friends \nPlease Connect to the Internet",Snackbar.LENGTH_LONG)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(i);
                        }
                    }).show();
        }else{
           // new LoadFriends().execute("http://selvinphp.netau.net/MyFriends.php");

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                            adapt_myFriends= new MyFriendsAdapter(MyFriendsActivity.this,getMyFriends(response));
                            if(!adapt_myFriends.isEmpty()){
                                tvNoFriends = (TextView) findViewById(R.id.tvNoFriendsToShow);
                                tvNoFriends.setVisibility(View.GONE);
                                lvMyFriends.setAdapter(adapt_myFriends);
                            }else {
                                tvNoFriends = (TextView) findViewById(R.id.tvNoFriendsToShow);
                                tvNoFriends.setVisibility(View.VISIBLE);
                            }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            };
            FetchMyFriends fetchMyFriends = new FetchMyFriends(username, responseListener);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(fetchMyFriends);
        }
        super.onPostCreate(savedInstanceState);
    }

    private ArrayList<Friends> getMyFriends(String respsoneString)
    {
        JSONArray jArr = null;
        ArrayList<Friends> list = new ArrayList<Friends>();
        //ArrayList<BaseSearchObject> players=new ArrayList<BaseSearchObject>();
        try {
            jArr = new JSONArray(respsoneString);
            Friends frndObj;

            for(int i=0;i<jArr.length();i++)
            {
                JSONObject jObj = jArr.getJSONObject(i);
                String name = jObj.getString("name");
                String username = jObj.getString("username");
                frndObj=new Friends(name,image,username);
                list.add(frndObj);
            }
        }catch (JSONException j)
        {
            j.printStackTrace();
            tvNoFriends = (TextView) findViewById(R.id.tvNoFriendsToShow);
            tvNoFriends.setVisibility(View.VISIBLE);

        }

        return list;
    }


    class LoadFriends extends AsyncTask<String,Void,ArrayList<Friends>>{
        @Override
        protected ArrayList<Friends> doInBackground(String... params) {
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
                JSONArray jArr = new JSONArray(buffer.toString());
                friendsList = new ArrayList<Friends>();
                for(int i=0;i< jArr.length();i++){
                    JSONObject jObj = jArr.getJSONObject(i);
                    String name = jObj.get("name").toString();
                    String username = jObj.get("username").toString();
                    int imgResource = R.drawable.user_icon;
                    Friends friends = new Friends(name,imgResource,username);
                    friendsList.add(friends);
                }
                return friendsList;



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException i) {
                i.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
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
        protected void onPostExecute(final ArrayList<Friends> friendses) {
            super.onPostExecute(friendses);
            if(friendses.size()>0){
                adapt_myFriends= new MyFriendsAdapter(MyFriendsActivity.this,friendses);
                lvMyFriends.setAdapter(adapt_myFriends);
            }
            lvMyFriends.setTextFilterEnabled(true);
            lvMyFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getBaseContext(),"Test",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class LoadFriendsData extends AsyncTask<String,String,String> {

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
            Intent registerIntent = new Intent(MyFriendsActivity.this, SearchFriendsActivity.class);
            registerIntent.putExtra("JSONdata",s);
            registerIntent.putExtra("name", name);
            registerIntent.putExtra("username", username);
            registerIntent.putExtra("email", email);
            MyFriendsActivity.this.startActivity(registerIntent);

        }
    }

    public static String getUsername() {
        return username;
    }

    public static String getEmail() {
        return email;
    }

    public static String getName() {
        return name;
    }

    public static MyFriendsAdapter getAdapt_myFriends() {
        return adapt_myFriends;
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
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_about){
            Intent i = new Intent(MyFriendsActivity.this,About.class);
            i.putExtra("name", name);
            i.putExtra("username", username);
            i.putExtra("email", email);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.icn_AddNetwork) {

            Intent addNetworkIntent = new Intent(MyFriendsActivity.this, AddNetworkActivity.class);
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
                Intent i = new Intent(MyFriendsActivity.this,MyFriendsActivity.class);
                i.putExtra("name", name);
                i.putExtra("username", username);
                i.putExtra("email", email);
                startActivity(i);
            }
        } else if (id == R.id.icn_myProfile) {

        } else if (id == R.id.icn_LogOut) {
            Intent i = new Intent(MyFriendsActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }else{

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                        Intent intent = new Intent(MyFriendsActivity.this, ModifyNetworkActivity.class);
                        intent.putExtra("name",name);
                        intent.putExtra("username",username);
                        intent.putExtra("email",email);
                        //intent.putExtra("wid",wid);
                        intent.putExtra("wname",wname);
                        intent.putExtra("wpass",wpass);
                        startActivity(intent);
                    }else{
                        ErrorDialogue ed = new ErrorDialogue();
                        ed.showErrorText("No Network Registered",MyFriendsActivity.this);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ModifyNetwork modNetwork = new ModifyNetwork(username,email, name, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MyFriendsActivity.this);
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
            Intent registerIntent = new Intent(MyFriendsActivity.this, SearchFriendsActivity.class);
            registerIntent.putExtra("JSONdata",s);
            registerIntent.putExtra("name", name);
            registerIntent.putExtra("username", username);
            registerIntent.putExtra("email", email);
            startActivity(registerIntent);

        }
    }
}
