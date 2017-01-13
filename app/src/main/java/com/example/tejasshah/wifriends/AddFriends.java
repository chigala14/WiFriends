package com.example.tejasshah.wifriends;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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


/**
 * Created by jay on 4/16/16.
 */
public class AddFriends extends AppCompatActivity {
    Button searchFriends;
    Button viewFriends;
    Button backToProfileFromSearch;
    Button bLogout;
    String username,email,name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friends);

        searchFriends = (Button) findViewById(R.id.searchFriends);
        viewFriends = (Button) findViewById(R.id.viewFriend);
        backToProfileFromSearch = (Button) findViewById(R.id.backToProfileFromSearch);
        bLogout = (Button) findViewById(R.id.bLogout);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");

        bLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddFriends.this, LoginActivity.class);
                AddFriends.this.startActivity(i);

            }
        });

        searchFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LoadData().execute("http://selvinphp.netau.net/ViewFriends.php");
                //Intent registerIntent = new Intent(AddFriends.this, SearchFriendsActivity.class);
                //AddFriends.this.startActivity(registerIntent);
            }


        });

        viewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddFriends.this,MyFriendsActivity.class);
                i.putExtra("name", name);
                i.putExtra("username", username);
                i.putExtra("email", email);
                startActivity(i);
               // Intent intent = new Intent(AddFriends.this, ViewFriendsActivity.class);
               // AddFriends.this.startActivity(intent);
            }


        });

        backToProfileFromSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFriends.this, UserAreaActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                AddFriends.this.startActivity(intent);
            }
        });





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
            Intent registerIntent = new Intent(AddFriends.this, SearchFriendsActivity.class);
            registerIntent.putExtra("JSONdata",s);
            registerIntent.putExtra("name", name);
            registerIntent.putExtra("username", username);
            registerIntent.putExtra("email", email);
            AddFriends.this.startActivity(registerIntent);

        }
    }
}

