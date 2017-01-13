package com.example.tejasshah.wifriends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tejasshah.wifriends.models.Networks;

import java.util.List;

/**
 * Created by Tejas Shah on 5/9/2016.
 */
public class ViewWifi_Adapter extends ArrayAdapter<Networks> {
    Context c;

    public ViewWifi_Adapter(Context context, List<Networks> networkses) {
        super(context, R.layout.view_wifi_row, networkses);
        this.c = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater listTrips_view =  LayoutInflater.from(getContext());
        final View customView = listTrips_view.inflate(R.layout.view_wifi_row,null);

        final Networks networks = getItem(position);

        TextView tvWirelessName = (TextView)customView.findViewById(R.id.tvWifiName);
        TextView tvFriendName = (TextView) customView.findViewById(R.id.tvFriendName);
        TextView tvNumber = (TextView) customView.findViewById(R.id.tvWifiNumber);

        tvWirelessName.setText(networks.getSsid());
        tvFriendName.setText(networks.getOwnerName());
        tvNumber.setText(String.valueOf(position+1));
        return  customView;
    }
}
