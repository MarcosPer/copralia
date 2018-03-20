package net.copralianetwork.copralia.Activities;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;


import net.copralianetwork.copralia.Auth.CopraliaAccount;
import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Sync.Sync;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.CopraliaAPI;
import net.copralianetwork.copralia.Utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcos on 22/09/2015.
 */
public class ActividadLogin extends AccountAuthenticatorActivity{


    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    private final String TAG = this.getClass().getSimpleName();

    private AccountManager mAccountManager;
    private String mAuthTokenType;


    private Boolean requested = false;

    private TextView login_text;
    private RelativeLayout login_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_login);

        mAccountManager = AccountManager.get(getBaseContext());
        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        login_text = (TextView) findViewById(R.id.login_text);
        login_loading = (RelativeLayout) findViewById(R.id.login_loading);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if(mAuthTokenType == null){
            mAuthTokenType = CopraliaAccount.AUTHTOKEN_USER;
        }
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideText();
                if (!requested) {
                    requested = true;
                    login();
                }
            }
        });
    }

    private void login(){
        hideKeyBoard();

        Loading();


        final String userName = ((TextInputEditText) findViewById(R.id.login_email)).getText().toString();
        final String userPass = ((TextInputEditText) findViewById(R.id.login_pass)).getText().toString();

        if(TextUtils.isEmpty(userName)){
            stopLoading();
            showText(getString(R.string.login_error_5));
            return;
        }
        if(TextUtils.isEmpty(userPass)){
            stopLoading();
            showText(getString(R.string.login_error_6));
            return;
        }




            Map<String,String> post = new HashMap<>();
            post.put("email",userName);
            post.put("password",userPass);

            VolleySingleton.getInstance(getApplicationContext()).
                    addToRequestQueue(
                            new APIRequest(getBaseContext(),
                                    Request.Method.POST,
                                    CopraliaAPI.getURL() + "user/login",
                                    post,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {



                                            try {

                                                int status = response.getInt("status");
                                                switch (response.getInt("status")) {
                                                    case 200://OK
                                                        //Log.d("MEITAG", "recibido el token es !!!! " + response.getString("token"));

                                                        Bundle data = new Bundle();
                                                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, getIntent().getStringExtra(ARG_ACCOUNT_TYPE));
                                                        data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                                                        data.putString(AccountManager.KEY_AUTHTOKEN, response.getString("token"));
                                                        data.putString(PARAM_USER_PASS, userPass);

                                                        //registerDevice(data); TODO: Diseñar un nuevo modelo con firecloud


                                                        Intent intent = new Intent();
                                                        intent.putExtras(data);

                                                        final Account account = new Account(intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME), intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));


                                                        mAccountManager.addAccountExplicitly(account, "", null);
                                                        mAccountManager.setUserData(account,AccountManager.KEY_AUTHTOKEN,response.getString("token"));
                                                        mAccountManager.setUserData(account,"userID",response.getString("userID"));

                                                        registerDevice();

                                                        setAccountAuthenticatorResult(intent.getExtras());
                                                        setResult(RESULT_OK, intent);

                                                        Sync.FullSync(getBaseContext());

                                                        finish();

                                                        //finishLogin();
                                                        break;
                                                    case 406:
                                                        showText(getString(R.string.login_error_2));
                                                        stopLoading();
                                                        break;
                                                    case 407:
                                                        showText(getString(R.string.login_error_3));
                                                        stopLoading();
                                                        break;
                                                    default:
                                                        showText(getString(R.string.login_error_4));
                                                        stopLoading();
                                                        break;
                                                }

                                            } catch (JSONException e) {
                                                stopLoading();
                                                Toast.makeText(getBaseContext(), getString(R.string.error_conexion), Toast.LENGTH_LONG).show();
                                                setResult(RESULT_CANCELED);
                                                finish();
                                                Log.d("MEITAG","TEST");
                                                e.printStackTrace();
                                                //TODO: throw error servidor
                                            }


                                            //TODO crear intent https://github.com/Udinic/AccountAuthenticator/blob/master/src/com/udinic/accounts_authenticator_example/authentication/AuthenticatorActivity.java
                                            //TODO linea 105
                                            //finishLogin(/*TODO LOGIN */ null);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            stopLoading();
                                            error.printStackTrace();
                                            Toast.makeText(getBaseContext(), getResources().getString(R.string.error_conexion), Toast.LENGTH_LONG).show();
                                        }
                                    }

                            )
                    );



    }


    public void registerDevice() {

        Map<String,String> post = new HashMap<>();
        post.put("device_type","1");
        post.put("device_model", Build.MODEL);
        post.put("os", "ANDROID");
        post.put("os_version", Build.VERSION.RELEASE);
        post.put("cloud_type","FIREBASE");
        post.put("cloud_token",FirebaseInstanceId.getInstance().getToken());

        APIRequest request = new APIRequest(getApplicationContext(),
            Request.Method.POST,
            CopraliaAPI.getURL() + "user/register_device",
            post,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response){
                    Toast.makeText(getBaseContext(),"Dispositivo registrado",Toast.LENGTH_SHORT).show();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(),"Dispositivo NO registrado",Toast.LENGTH_SHORT).show();
                }
            }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }



    private void Loading(){
        requested = true;
        login_loading.setVisibility(View.VISIBLE);
    }
    private void stopLoading(){
        requested = false;
        login_loading.setVisibility(View.GONE);
    }
    private void hideKeyBoard(){
        try  {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }
    private void showText(String text){
        login_text.setVisibility(View.VISIBLE);
        login_text.setText(text);
    }
    private void hideText(){
        login_text.setVisibility(View.GONE);
    }

}
