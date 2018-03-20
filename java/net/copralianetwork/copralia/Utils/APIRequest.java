package net.copralianetwork.copralia.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import net.copralianetwork.copralia.Auth.AccountHelper;
import net.copralianetwork.copralia.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcos on 09/12/2015.
 */
public class APIRequest extends Request<String> {

    private Listener<JSONObject> listener;
    private Map<String, String> params;
    private Context ctx;


    public APIRequest(Context ctx,String url, Map<String, String> params, Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.ctx = ctx;
        this.listener = reponseListener;
        this.params = params;
    }

    public APIRequest(Context ctx,int method, String url, Map<String, String> params, Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);//sirve para llamar al constructor de la clase extendida
        this.ctx = ctx;
        this.listener = reponseListener;
        this.params = params;
    }


    public APIRequest(final Context ctx,String url, Map<String, String> params, Listener<JSONObject> reponseListener) {
        super(Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx,ctx.getString(R.string.error_conexion),Toast.LENGTH_SHORT).show();
            }
        });
        this.ctx = ctx;
        this.listener = reponseListener;
        this.params = params;
    }

    public APIRequest(final Context ctx,int method, String url, Map<String, String> params, Listener<JSONObject> reponseListener) {
        super(method, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx,ctx.getString(R.string.error_conexion),Toast.LENGTH_SHORT).show();
            }
        });//sirve para llamar al constructor de la clase extendida
        this.ctx = ctx;
        this.listener = reponseListener;
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    }
    @Override
    public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
        Map<String,String> headers = new HashMap<>();
        headers.put("API-KEY","potongometocaelmorongo");
        headers.put("AUTH", AccountHelper.getToken(this.ctx));
        return headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            JSONObject res = new JSONObject(jsonString);
            String status = res.getString("status");

            switch (status){
                case "OK":
                    return Response.success(res.getJSONObject("data").toString(), HttpHeaderParser.parseCacheHeaders(response));
                case "ERROR":
                    this.getUrl();

                    int errorCode = 0;
                    String errorMsg = "";

                    if(res.has("error_code")){
                        errorCode = res.getInt("error_code");
                    }

                    if(res.has("error_msg")){
                        errorMsg = res.getString("error_msg");
                    }

                    if(errorCode != 0){
                        if(errorCode == 700){//Invalid API-KEY

                        }
                    }
                    //TODO: FIXEAR LOS ERRORES GRAVES
                    //TODO: AQUI SALTA SI HAY ERROR DE TOKEN O API-KEY
                    return Response.error(new ParseError(new APIException(errorCode,errorMsg,this.getUrl())));
                default:
                    return Response.error(new ParseError(new APIException(0,"",this.getUrl())));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String resp) {
        try {
            listener.onResponse(new JSONObject(resp));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Map<String,String> getPostData(){
        return params;
    }

    public static class APIException extends Exception{
        public int errorCode;
        public String errorMsg;
        public String requestURL;

        public APIException(int errorCode,String errorMsg,String url){
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
            this.requestURL = url;
        }
        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public String getRequestURL() {
            return requestURL;
        }
    }
}