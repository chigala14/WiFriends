package com.example.tejasshah.wifriends;

        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.toolbox.StringRequest;

        import java.util.HashMap;
        import java.util.Map;

/**
 * Created by Chirag on 4/18/16.
 */
public class ViewWiFi extends StringRequest {

    private static final String VIEW_WIFI_REQUEST_URL = "http://selvinphp.netau.net/ViewWiFi.php";
    private Map<String, String> params;

    public ViewWiFi(String username,String email, String name, Response.Listener<String> listener){
        super (Request.Method.POST, VIEW_WIFI_REQUEST_URL, listener, null);
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
