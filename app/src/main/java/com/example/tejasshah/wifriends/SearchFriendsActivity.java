package com.example.tejasshah.wifriends;

/**
 * Created by jay on 4/16/16.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

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


public class SearchFriendsActivity extends AppCompatActivity implements OnItemClickListener {

    ListView lv;
    SearchView sv;
    String respsoneString;
    SearchAdapter adapter;
    ArrayList<BaseSearchObject> players=new ArrayList<BaseSearchObject>();
    String username,email,name;

    //String[] names={"Jay Patel","Chirag Gala","Tejas Shah","Jeff Bickford","Michael Carrick","Diego Costa","Jose Mourinho"};
    int image = R.drawable.user_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_friends_user);


        lv = (ListView) findViewById(R.id.friendList);
        lv.setOnItemClickListener(this);
        sv = (SearchView) findViewById(R.id.searchFriends);

        Intent i = getIntent();
        respsoneString = i.getStringExtra("JSONdata");

        username = i.getStringExtra("username");
        email = i.getStringExtra("email");
        name = i.getStringExtra("name");

        //ADAPTER
            adapter=new SearchAdapter(this, getFriends());
            lv.setAdapter(adapter);
            sv.setIconified(false);
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String arg0) {
                    // TODO Auto-generated method stub
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    // TODO Auto-generated method stub

                    adapter.getFilter().filter(query);

                    return false;
                }
            });


        }

    private ArrayList<BaseSearchObject> getFriends()
    {
        JSONArray jArr = null;
        //ArrayList<BaseSearchObject> players=new ArrayList<BaseSearchObject>();
        try {
            jArr = new JSONArray(respsoneString);
            BaseSearchObject p;

            for(int i=0;i<jArr.length();i++)
            {
                JSONObject jObj = jArr.getJSONObject(i);
                p=new BaseSearchObject(jObj.getString("name"), image,jObj.getString("username"));
                players.add(p);
            }
        }catch (JSONException j)
        {
            j.printStackTrace();
        }


        return players;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                final String friendUserName = players.get(position).getUsername();

                new AlertDialog.Builder(this)
                        .setTitle("Add Friend")
                        .setMessage("Add "+players.get(position).getFriendName()+ " as your Friend ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success = jsonResponse.getBoolean("success");

                                            if (success) {
                                                Toast.makeText(getBaseContext(), "Friend created successfully!", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(SearchFriendsActivity.this, Home.class);
                                                intent.putExtra("name", name);
                                                intent.putExtra("username", username);
                                                intent.putExtra("email", email);
                                                startActivity(intent);

                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(SearchFriendsActivity.this);
                                                builder.setMessage(friendUserName +" is already your Friend")
                                                        .setNegativeButton("Retry", null)
                                                        .create()
                                                        .show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                AddNewFriend newFriend = new AddNewFriend(username, friendUserName, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(SearchFriendsActivity.this);
                                queue.add(newFriend);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            } catch (Exception e) {
                Toast.makeText(SearchFriendsActivity.this, "Exception in setOnItemClickListener: "
                        + e.toString(), Toast.LENGTH_LONG).show();
            }


        }

    class LoadData extends AsyncTask<String,String,String>{

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
                        .appendQueryParameter("username", "jay123");
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
            sv = (SearchView) findViewById(R.id.searchFriends);
            adapter=new SearchAdapter(getBaseContext(), getFriends());
            lv.setAdapter(adapter);

        }
    }
}






