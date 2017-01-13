package com.example.tejasshah.wifriends;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.tejasshah.wifriends.models.Networks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedInputStream;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private Button btnWifi;
    private ListView lvNetworks;
    WifiManager wifiManager;
    WifiReceiver wifiRec = new WifiReceiver();
    List<Networks> NetworkAvail = new ArrayList<Networks>();
    android.support.v7.app.ActionBar actionBar;
    TextView tvResults;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnWifi = (Button)findViewById(R.id.btnWifiStatus);
        tvResults = (TextView)findViewById(R.id.tvResult);
        //lvNetworks = (ListView)findViewById(R.id.lv_NetworkAv);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.user_icon);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);


        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiRec, filter);
        lvNetworks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Networks netObj = NetworkAvail.get(position);

                WifiConfiguration wc = new WifiConfiguration();
                wc.SSID = "\"" + netObj.getSsid() + "\"";
                wc.BSSID = netObj.getBssid();
                wc.preSharedKey = "\"" + "12345678" + "\"";

                int netid = wifiManager.addNetwork(wc);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netid, true);
                wifiManager.reconnect();

            }
        });

        btnWifi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new LoadData().execute("http://selvinphp.netau.net/ViewFriends.php");


                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                Boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Toast.makeText(getBaseContext(), "Mobile Data Enabled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Mobile Data Disabled", Toast.LENGTH_LONG).show();
                }
            }
        });

    }



    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent)
        {
            List<WifiConfiguration> results = wifiManager.getConfiguredNetworks();
            Toast.makeText(getBaseContext(),String.valueOf(results.size()),Toast.LENGTH_SHORT).show();
            final List<ScanResult> scanResults = wifiManager.getScanResults();
            Toast.makeText(getBaseContext(),String.valueOf(scanResults.size()),Toast.LENGTH_SHORT).show();
            //unregisterReceiver(wifiRec);
            for(ScanResult result: scanResults){
                if((!result.SSID.isEmpty())){
                    Networks netObj = new Networks(result.SSID,result.BSSID,result.capabilities,result.level);
                    NetworkAvail.add(netObj);
                }

            }

            ArrayAdapter<Networks> networkAdp = new ArrayAdapter<Networks>(getBaseContext(), android.R.layout.simple_list_item_2, android.R.id.text1,NetworkAvail){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view =  super.getView(position, convertView, parent);
                    Networks netObj = NetworkAvail.get(position);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    text1.setText(netObj.getSsid());
                    text2.setText(netObj.getCapability());
                    return view;
                }
            };
            lvNetworks.setAdapter(networkAdp);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main_actions,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(wifiRec);
        }catch (Exception e){
            e.printStackTrace();
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
                JSONArray jArr = new JSONArray(buffer.toString());
                return buffer.toString();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException i) {
                i.printStackTrace();
            } catch(JSONException j){
                j.printStackTrace();
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
            //Toast.makeText(getBaseContext(),s,Toast.LENGTH_SHORT).show();
           // JSONObject jsonObject = new JSONObject(s);
            tvResults.setText(s);
        }
    }
}
