package com.example.tejasshah.wifriends;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chirag on 4/18/16.
 */
public class AddNewFriend extends StringRequest {
    private static final String ADD_NEW_FRIEND_REQUEST_URL = "http://selvinphp.netau.net/AddNewFriend.php";
    private Map<String, String> params;

    public AddNewFriend(String username, String addNewFriend,  Response.Listener<String> listener){
        super (Method.POST, ADD_NEW_FRIEND_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username",username);
        params.put("addNewFriend",addNewFriend);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
