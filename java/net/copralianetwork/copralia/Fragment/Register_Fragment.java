package net.copralianetwork.copralia.Fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import net.copralianetwork.copralia.Utils.CopraliaAPI;
import net.copralianetwork.copralia.Utils.VolleySingleton;
import net.copralianetwork.copralia.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcos on 23/09/2015.
 */
public class Register_Fragment extends Fragment {

    private TextView error_text;
    private EditText email;
    private EditText password;
    private EditText nombre;
    private boolean requested = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        TextView registolink = (TextView) view.findViewById(R.id.registro_link);
        error_text = (TextView) view.findViewById(R.id.registro_text);

        email = (EditText) view.findViewById(R.id.registro_email);
        password = (EditText) view.findViewById(R.id.registro_pass);
        nombre = (EditText) view.findViewById(R.id.registro_nombre);

        registolink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //getFragmentManager().beginTransaction().addToBackStack("Pagina Anterior").replace(R.id.contenedor_principal, new Login_Fragment()).commit();
            }
        });

        Button register = (Button) view.findViewById(R.id.registro_button);

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!requested){
                    requested = true;
                    hideError();
                    register(email.getText().toString(), password.getText().toString(), nombre.getText().toString());
                }
            }
        });
        return view;
    }

    private void register (String email,String pass, String nombre){
        if(TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){//email no valido
            showError(getString(R.string.registro_error_1));
            requested = false;
            return;
        }
        if(pass.length() < 8){
            showError(getString(R.string.registro_error_2));
            requested = false;
            return;
        }
        if(TextUtils.isEmpty(nombre)){
            showError(getString(R.string.registro_error_3));
            requested = false;
            return;
        }

        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                CopraliaAPI.getURL() + "user/register?email=" + Uri.encode(email) + "&password=" + Uri.encode(pass),
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if(isAdded()){
                                            requested = false;
                                            procesarRespuesta(response);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (isAdded()) {
                                            requested = false;
                                            Toast.makeText(getActivity(),getResources().getString(R.string.error_conexion), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                        )
                );

    }


    private void showError(String text){
        error_text.setVisibility(View.VISIBLE);
        error_text.setText(text);
    }
    private void hideError(){
        error_text.setVisibility(View.GONE);
    }

    private void procesarRespuesta(JSONObject response){
        try {
            switch (response.getInt("status")){
                case 0://OK
                    Toast.makeText(getContext(),getString(R.string.registro_correcto),Toast.LENGTH_LONG).show();
                    //getFragmentManager().beginTransaction().addToBackStack("Pagina Anterior").replace(R.id.contenedor_principal, new Login_Fragment()).commit();
                    break;
                case 1:
                    showError(getString(R.string.registro_error_1));//email invalido
                    break;
                case 3:
                    showError(getString(R.string.registro_error_4));//email usado
                    break;
                default:
                    showError(getString(R.string.registro_error_5));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
