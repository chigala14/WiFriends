package com.example.tejasshah.wifriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by jay on 4/18/16.
 */
public class RemoveItemActivity extends AppCompatActivity {

    Button addFriend;
    Button backToSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_friend);

        addFriend = (Button) findViewById(R.id.buttonItemClickRemoveFriend);
        backToSearch = (Button) findViewById(R.id.backToViewFriends);

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemoveItemActivity.this, AddFriends.class);
                RemoveItemActivity.this.startActivity(intent);
            }


        });

        backToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemoveItemActivity.this, ViewFriendsActivity.class);
                RemoveItemActivity.this.startActivity(intent);
            }


        });
    }
}
