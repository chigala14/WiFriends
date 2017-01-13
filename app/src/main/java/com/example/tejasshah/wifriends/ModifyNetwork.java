package com.example.tejasshah.wifriends;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chirag on 4/16/16.
 */
public class ModifyNetwork extends StringRequest{
    private static final String MODIFY_REQUEST_URL = "http://selvinphp.netau.net/ModifyNetwork.php";
    private Map<String, String> params;

    public ModifyNetwork(String username,String email, String name, Response.Listener<String> listener){
        super (Request.Method.POST, MODIFY_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username",username);
        params.put("email",email);
        params.put("name",name);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
