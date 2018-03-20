package net.copralianetwork.copralia.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.CopraliaAPI;
import net.copralianetwork.copralia.Utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;


/**
 * Created by Marcos on 06/12/2015.
 */
public class Cuenta_Perfil_Fragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragmento_cuenta_perfil, container, false);
        v.findViewById(R.id.card_pass).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View promptView = li.inflate(R.layout.dialog_cambio_pass, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(promptView);
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String pass = ((EditText) promptView.findViewById(R.id.pass)).getText().toString();
                                String repeat = ((EditText) promptView.findViewById(R.id.pass_repeat)).getText().toString();
                                String old = ((EditText) promptView.findViewById(R.id.oldpass)).getText().toString();

                                if(pass.isEmpty() || repeat.isEmpty() || old.isEmpty()){
                                    Toast.makeText(getContext(),getString(R.string.cuenta_perfil_contrasena_dialog_rellena),Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(!pass.equals(repeat)){
                                    Toast.makeText(getContext(),getString(R.string.cuenta_perfil_contrasena_dialog_coinciden),Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                final ProgressDialog cargador = ProgressDialog.show(getContext(), getString(R.string.cuenta_perfil_contrasena_carga_titulo), getString(R.string.cuenta_perfil_contrasena_carga_texto), true);
                                cargador.show();

                                Map<String,String> postData = new HashMap<String, String>();
                                postData.put("newpass",pass);
                                postData.put("oldpass",old);

                                final APIRequest req = new APIRequest(getContext(),Request.Method.POST,CopraliaAPI.getURL()+"/user/changepass",postData,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    switch (response.getInt("status")){
                                                        case 100:
                                                            Toast.makeText(getContext(),getString(R.string.cuenta_perfil_contrasena_cambiada),Toast.LENGTH_SHORT).show();
                                                        case 101:
                                                            Toast.makeText(getContext(),getString(R.string.cuenta_perfil_contrasena_incorrecta),Toast.LENGTH_SHORT).show();
                                                        case 102:
                                                            Toast.makeText(getContext(),getString(R.string.cuenta_perfil_contrasena_incorrecta),Toast.LENGTH_SHORT).show();
                                                        default:
                                                            Toast.makeText(getContext(),getString(R.string.error_servidor),Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                cargador.hide();

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                cargador.hide();
                                                Toast.makeText(getContext(),getString(R.string.error_conexion),Toast.LENGTH_SHORT).show();
                                            }
                                });

                                VolleySingleton.
                                        getInstance(getActivity()).
                                        addToRequestQueue(req);

                            }


                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();

            }
        });
        v.findViewById(R.id.card_fav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.cuenta_perfil_favoritos_titulo))
                        .setMessage(getString(R.string.cuenta_perfil_favoritos_dialog_texto))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

            }
        });
        return v;
    }
    

}
