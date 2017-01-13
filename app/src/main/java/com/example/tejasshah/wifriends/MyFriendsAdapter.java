package com.example.tejasshah.wifriends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tejasshah.wifriends.models.Friends;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tejas Shah on 5/5/2016.
 */
public class MyFriendsAdapter extends BaseAdapter implements Filterable{
    Context c;
    ArrayList<Friends> friends;
    ArrayList<Friends> filterList;
    CustomFilter filter;


    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return friends.indexOf(getItem(position));
    }

    public MyFriendsAdapter(Context context, ArrayList<Friends> friends){
        this.c = context;
        this.friends = friends;
        this.filterList = friends;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null)
        {
            convertView=inflater.inflate(R.layout.view_myfriends_row, null);
        }
        final View customView = convertView;


        /*LayoutInflater listFriends_view = LayoutInflater.from(getContext());
        final View customView = listFriends_view.inflate(R.layout.view_myfriends_row,null);
        final Friends friend = getItem(position);*/

        TextView nameTxt=(TextView) convertView.findViewById(R.id.tv_FriendName);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tv_FriendUserName);
        ImageView img=(ImageView) convertView.findViewById(R.id.imgFriend);
        Button btnRmvFriend = (Button)convertView.findViewById(R.id.btnRemoveFriend);

        //SET DATA TO THEM
        nameTxt.setText(friends.get(position).getFriendName());
        tvUserName.setText(friends.get(position).getUsername());
        img.setImageResource(friends.get(position).getImg());

        btnRmvFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                               Snackbar.make(customView,"Friend Removed Successfully",Snackbar.LENGTH_SHORT).show();
                                MyFriendsActivity myFriendsActivity = new MyFriendsActivity();
                                Intent i = new Intent(c,MyFriendsActivity.class);
                                i.putExtra("name", myFriendsActivity.getName());
                                i.putExtra("username", myFriendsActivity.getUsername());
                                i.putExtra("email", myFriendsActivity.getEmail());
                                c.startActivity(i);
                            } else {

                                Snackbar.make(customView,"Failed to Add Friend",Snackbar.LENGTH_LONG).show();
                                    /*AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("Login Failed")
                                            .setNegativeButton("Retry",null)
                                            .create()
                                            .show();*/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                };


                // Enter Remove Friend Code here...
                //Snackbar.make(customView,"Code for Remove Friends Needed",Snackbar.LENGTH_SHORT).show();

                RemoveFriend removeFriend = new RemoveFriend(MyFriendsActivity.getUsername(), friends.get(position).getUsername(), responseListener);
                RequestQueue queue = Volley.newRequestQueue(c);
                queue.add(removeFriend);
            }
        });

        return customView;
    }

    private class RemoveFriend extends StringRequest {
        private static final String REMOVE_FRIEND_URL = "http://selvinphp.netau.net/delFriend.php";
        private Map<String, String> params;

        public RemoveFriend( String username, String friendUserName, Response.Listener<String> listener){
            super (Request.Method.POST, REMOVE_FRIEND_URL, listener, null);
            params = new HashMap<>();
            params.put("username",username);
            params.put("delFriend",friendUserName);

        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }


   /* private static class ViewHolder {
        public TextView nameTxt;
        public TextView userName;
        public ImageView img;
        public Button btnRmvFriend;

    }*/

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new CustomFilter();
        }
        return filter;

    }


    class CustomFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() > 0){
                constraint = constraint.toString().toUpperCase();

                ArrayList<Friends> filters = new ArrayList<Friends>();
                for(int i =0; i<filterList.size();i++){
                    if(filterList.get(i).getFriendName().toUpperCase().contains(constraint)){
                        Friends f = new Friends(filterList.get(i).getFriendName(),filterList.get(i).getImg(),filterList.get(i).getUsername());
                        filters.add(f);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            }else {
                    results.values = filterList;
                    results.count = filterList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            friends = (ArrayList<Friends>)results.values;
            notifyDataSetChanged();

        }
    }

    public void updateResults(){
        notifyDataSetChanged();
    }

}

