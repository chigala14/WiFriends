package com.example.tejasshah.wifriends;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chirag on 4/16/16.
 */
public class UpdateNetwork extends StringRequest{
    private static final String UPDATE_NETWORK_REQUEST_URL = "http://selvinphp.netau.net/UpdateNetwork.php";
    private Map<String, String> params;

    public UpdateNetwork(String username, String wname, String wpass, Response.Listener<String> listener){
        super (Request.Method.POST, UPDATE_NETWORK_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username",username);
        params.put("wname",wname);
        params.put("wpass",wpass);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
