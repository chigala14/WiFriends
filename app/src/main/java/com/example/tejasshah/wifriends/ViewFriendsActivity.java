package com.example.tejasshah.wifriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jay on 4/18/16.
 */
public class ViewFriendsActivity extends AppCompatActivity implements OnItemClickListener {

    ListView lv;
    SearchView sv;

    String[] names = {"Parth Patel", "Akash Patel", "Leo Messi", "Christiano Ronaldo", "Andres Inista", "Gerrad Pique", "Sergio Ramos"};
    int image = R.drawable.user_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_friends);

        lv = (ListView) findViewById(R.id.friendListView);
        lv.setOnItemClickListener(this);
        sv = (SearchView) findViewById(R.id.searchFriendsView);

        //ADAPTER
        final SearchAdapter adapter = new SearchAdapter(this, getFriends());

        lv.setAdapter(adapter);


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

    private ArrayList<BaseSearchObject> getFriends() {
        ArrayList<BaseSearchObject> players = new ArrayList<BaseSearchObject>();
        BaseSearchObject p;

        for (int i = 0; i < names.length; i++) {

            p = new BaseSearchObject(names[i], image,"");
            players.add(p);
        }

        return players;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Intent i = new Intent(ViewFriendsActivity.this, RemoveItemActivity.class);
            i.putExtra("Name", names);
            ViewFriendsActivity.this.startActivity(i);

        } catch (Exception e) {
            Toast.makeText(ViewFriendsActivity.this, "Exception in setOnItemClickListener: "
                    + e.toString(), Toast.LENGTH_LONG).show();
        }
}

}
