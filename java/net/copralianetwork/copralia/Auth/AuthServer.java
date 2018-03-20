package net.copralianetwork.copralia.Auth;



import android.net.Uri;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.copralianetwork.copralia.Utils.CopraliaAPI;


import org.json.JSONObject;

import java.io.IOException;


public class AuthServer {
    public String userSignUp(final String name, final String email, final String pass, String authType) {

        return null;//AUTHTOKEN
    }

    public String userSignIn(final String user, final String pass, String authType) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(CopraliaAPI.getURL() + "user/login?email=" + Uri.encode(user)+"&password="+Uri.encode(pass)).build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject data = new JSONObject(response.body().string());
            if(data.getInt("status") != 1){
                throw new Exception(""+data.getInt("status"));
            }
            return data.getString("token");
        } catch (Exception e) {
            throw new Exception("-1");
        }
    }
}
