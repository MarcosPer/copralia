package net.copralianetwork.copralia.Activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.iid.InstanceID;

import net.copralianetwork.copralia.Auth.CopraliaAccount;
import net.copralianetwork.copralia.R;
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
public class ActividadRegister extends AccountAuthenticatorActivity{


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
                    submit();
                }
            }
        });
    }

    private void submit(){
        hideKeyBoard();

        Loading();

        final String userName = ((EditText) findViewById(R.id.login_email)).getText().toString();
        final String userPass = ((EditText) findViewById(R.id.login_pass)).getText().toString();

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


                                            String authtoken = "";

                                            try {

                                                int status = response.getInt("status");
                                                switch (response.getInt("status")) {
                                                    case 1://OK
                                                        Log.d("MEITAG", "recibido!!!! " + response.getString("token"));

                                                        Bundle data = new Bundle();
                                                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, getIntent().getStringExtra(ARG_ACCOUNT_TYPE));
                                                        data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                                                        data.putString(AccountManager.KEY_AUTHTOKEN, response.getString("token"));
                                                        data.putString(PARAM_USER_PASS, userPass);

                                                        registerDevice(data);
                                                        break;
                                                    case 2:
                                                        showText(getString(R.string.login_error_2));
                                                        stopLoading();
                                                        break;
                                                    case 3:
                                                        showText(getString(R.string.login_error_3));
                                                        stopLoading();
                                                        break;
                                                    default:
                                                        showText(getString(R.string.login_error_4));
                                                        stopLoading();
                                                        break;
                                                }

                                            } catch (JSONException e) {
                                                authtoken = "";
                                                stopLoading();
                                                Toast.makeText(getBaseContext(), getString(R.string.error_conexion), Toast.LENGTH_LONG).show();
                                                setResult(RESULT_CANCELED);
                                                finish();

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
                                            Toast.makeText(getBaseContext(), getResources().getString(R.string.error_conexion), Toast.LENGTH_LONG).show();
                                        }
                                    }

                            )
                    );



    }


    public void registerDevice(final Bundle loginData) {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected void onPostExecute(final Object result) {

                Map<String,String> post = new HashMap<>();
                post.put("token",loginData.getString(AccountManager.KEY_AUTHTOKEN));
                post.put("model", Build.MODEL);
                post.put("OS","android");
                post.put("cloud_type","gcm");
                post.put("cloud_token",result.toString());

                APIRequest request = new APIRequest(getApplicationContext(),
                    Request.Method.POST,
                    CopraliaAPI.getURL() + "/user/register_device",
                    post,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try{

                                Intent intent = new Intent();
                                intent.putExtras(loginData);

                                intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                                intent.getStringExtra(PARAM_USER_PASS);

                                String accountPassword = intent.getStringExtra(PARAM_USER_PASS);

                                final Account account = new Account(intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME), intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

                                String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

                                String authtokenType = mAuthTokenType;


                                mAccountManager.addAccountExplicitly(account, accountPassword, null);
                                mAccountManager.setAuthToken(account, authtokenType, authtoken);

                                mAccountManager.setUserData(account,"deviceID",response.getString("deviceID"));
                                mAccountManager.setUserData(account,"deviceToken",response.getString("deviceToken"));


                                setAccountAuthenticatorResult(intent.getExtras());
                                setResult(RESULT_OK, intent);
                                finish();



                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
            }

            @Override
            protected String doInBackground(final Object... params) {
                String regid = "";
                try {
                    //InstanceID iid = InstanceID.getInstance(getApplicationContext());
                    //regid = iid.getToken("1049551150934", GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return regid;
            }

        }.execute();
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;


            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);

            mAccountManager.setUserData(account, "deviceID", "ID");
            mAccountManager.setUserData(account,"deviceToken","token");

        } else {
            mAccountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
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
