package net.copralianetwork.copralia.Activities.Principal.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import net.copralianetwork.copralia.Activities.List.ListActivity;
import net.copralianetwork.copralia.Adapter.Listas_Adapter;
import net.copralianetwork.copralia.MenuController;
import net.copralianetwork.copralia.Sync.Lists.ListsProvider;
import net.copralianetwork.copralia.Sync.Lists.ListsUtils;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.CopraliaAPI;
import net.copralianetwork.copralia.Utils.VolleySingleton;
import net.copralianetwork.copralia.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcos on 27/09/2015.
 */
public class Listas_Fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, Listas_Adapter.OnItemClickListener {

    private RecyclerView reciclador;
    private LinearLayoutManager layoutManager;
    private Listas_Adapter mAdapter;
    private ProgressDialog cargador;

    public static final int LOADER_LISTAS = 301;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmento_listas, container, false);
        reciclador = (RecyclerView) view.findViewById(R.id.reciclador);
        layoutManager = new LinearLayoutManager(getActivity());
        reciclador.setLayoutManager(layoutManager);
        mAdapter = new Listas_Adapter(getActivity());
        mAdapter.setOnItemClickListener(this);
        reciclador.setAdapter(mAdapter);

        setHasOptionsMenu(true);
        getLoaderManager().initLoader(LOADER_LISTAS,null,this);
        return view;

    }

    @Override
    public void onItemClick(Listas_Adapter.ViewHolder item, int position) {
        Intent start = new Intent(getContext(), ListActivity.class);
        start.putExtra("listID",item.ID);
        startActivity(start);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuController.setIcon(menu, R.id.menu_principal_lista_nueva);

        /* Boton crear lista */
/*
        menu.findItem(R.id.menu_principal_lista_nueva).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View promptView = li.inflate(R.layout.input_text_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(promptView);
                ((TextView) promptView.findViewById(R.id.dialog_title)).setText(getString(R.string.listas_nueva_nombre));

                final EditText editText = (EditText) promptView.findViewById(R.id.dialog_text);

                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Crear", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String nombreLista = editText.getText().toString();
                                if(!nombreLista.trim().isEmpty()){
                                    nuevaLista(nombreLista);
                                }else{
                                    Toast.makeText(getContext(),getContext().getText(R.string.listas_nueva_error_nombre),Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
                return true;
            }
        });

        */

        menu.findItem(R.id.menu_principal_lista_nueva).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FirebaseMessaging fm = FirebaseMessaging.getInstance();
                fm.send(new RemoteMessage.Builder("126281660599@gcm.googleapis.com")
                        .setMessageId(Integer.toString((int)(Math.random() * 10000)))
                        .addData("my_message", "Hello World")
                        .addData("my_action","SAY_HELLO")
                        .build());
                return true;
            }


        });



    }

    private void nuevaLista(final String listname){

        cargador = ProgressDialog.show(getContext(),listname,getString(R.string.listas_nueva_carga),true);

        Map<String,String> datos = new HashMap<>();

        datos.put("name", listname);

        final APIRequest request = new APIRequest(
                getContext(), Request.Method.POST,
                CopraliaAPI.getURL() + "lists/new",
                datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(isAdded()){
                            try {

                                if(response.getInt("status") == 200) {

                                    // Añadimos la lista al sistema


                                    ListsUtils.addList(getContext(),response.getString("listID"),listname,response.getInt("created"));

                                }else{
                                    Toast.makeText(getContext(), getString(R.string.listas_nueva_error), Toast.LENGTH_LONG).show();
                                }
                                cargador.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), getResources().getString(R.string.error_conexion), Toast.LENGTH_LONG).show();
                                cargador.cancel();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (isAdded()) {
                            error.printStackTrace();
                            Toast.makeText(getActivity(), getResources().getString(R.string.error_conexion), Toast.LENGTH_LONG).show();
                            cargador.cancel();
                        }
                    }
                }
        );

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),Uri.withAppendedPath(ListsProvider.URI_LISTAS,"lists"),null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case LOADER_LISTAS:
                mAdapter.updateData(data);
                break;
            default:
                break;
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case LOADER_LISTAS:
                mAdapter.updateData(null);
                break;
            default:
                break;
        }
    }
}

