package com.example.tejasshah.wifriends;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
        import android.util.Base64;
        import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tejasshah.wifriends.models.Networks;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
import java.util.List;

public class ViewWiFiActivity extends AppCompatActivity {
    private ListView lvNetworks;
    WifiManager wifiManager;
    WifiReceiver wifiRec = new WifiReceiver();
    List<Networks> NetworkAvail = new ArrayList<Networks>();
    List<Networks> FriendsWifi = new ArrayList<Networks>();
    String wname,wpass,epass;
    TextView tvwifiStat;
    JSONArray jsonArray;
    AlertDialog.Builder builder1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wi_fi);

        tvwifiStat = (TextView)findViewById(R.id.tvWifiSearchStatus);
        lvNetworks = (ListView)findViewById(R.id.lvNetworkAv);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");
        final String name = intent.getStringExtra("name");
        try{
            jsonArray = new JSONArray(intent.getStringExtra("jsonResponse"));
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Networks networks = new Networks(jsonObject.getString("wname"),jsonObject.getString("wpass"),jsonObject.getString("name1"));
                FriendsWifi.add(networks);
            }
        }catch (JSONException e){
            e.printStackTrace();
            Snackbar.make(getCurrentFocus(),"Error Retrieving available Wifi",Snackbar.LENGTH_SHORT).show();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiRec, filter);
        Snackbar.make(tvwifiStat,"Searching for Wifi..",Snackbar.LENGTH_LONG).show();
        lvNetworks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Networks netObj = NetworkAvail.get(position);
                String encrypPass = netObj.getPass();
                String pass = new String(Base64.decode(encrypPass,Base64.DEFAULT));
                WifiConfiguration wc = new WifiConfiguration();
                wc.SSID = "\"" + netObj.getSsid() + "\"";
                wc.BSSID = netObj.getBssid();
                wc.preSharedKey = "\"" + pass + "\"";

                int netid = wifiManager.addNetwork(wc);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netid, true);
                wifiManager.reconnect();

                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mWifi.isConnected()) {
                    WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
                    WifiInfo info = wifiManager.getConnectionInfo ();
                    info.getBSSID ();
                    if(wc.SSID.equals(info.getBSSID())){
                        Snackbar.make(getCurrentFocus(),"Wifi Connected Successfully",Snackbar.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent)
        {
            List<WifiConfiguration> results = wifiManager.getConfiguredNetworks();
            //Toast.makeText(getBaseContext(),String.valueOf(results.size()),Toast.LENGTH_SHORT).show();
            final List<ScanResult> scanResults = wifiManager.getScanResults();
            //Toast.makeText(getBaseContext(),String.valueOf(scanResults.size()),Toast.LENGTH_SHORT).show();
            unregisterReceiver(wifiRec);
            for(ScanResult result: scanResults){
                for (Networks networks : FriendsWifi){
                    if(!result.SSID.isEmpty() && result.SSID.equals(networks.getSsid())){
                        Networks netObj = new Networks(result.SSID,result.BSSID,result.capabilities,result.level,networks.getPass(),networks.getOwnerName());
                        NetworkAvail.add(netObj);
                    }
                }
            }

            if(NetworkAvail.size() > 0)
            {
              /*  ArrayAdapter<Networks> networkAdp = new ArrayAdapter<Networks>(getBaseContext(), android.R.layout.simple_list_item_2, android.R.id.text1,NetworkAvail){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view =  super.getView(position, convertView, parent);
                    Networks netObj = NetworkAvail.get(position);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                    System.out.println("test"+text2);

                    text1.setText(netObj.getSsid());
                    text2.setText(netObj.getCapability());
                    return view;
                }
            };
                lvNetworks.setAdapter(networkAdp);*/

                ListAdapter listAdapter = new ViewWifi_Adapter(getBaseContext(),NetworkAvail);
                lvNetworks.setAdapter(listAdapter);

                tvwifiStat.setVisibility(View.GONE);
            }
            else {
                tvwifiStat.setText("No Wireless Connections to connect");
                Snackbar.make(getCurrentFocus(),"No Wireless Connections to Connect",Snackbar.LENGTH_SHORT);
            }
        }


    }

}

