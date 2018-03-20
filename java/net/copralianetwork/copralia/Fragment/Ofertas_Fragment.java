package net.copralianetwork.copralia.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import net.copralianetwork.copralia.Adapter.Ofertas_Adapter;
import net.copralianetwork.copralia.MenuController;
import net.copralianetwork.copralia.Utils.CopraliaAPI;
import net.copralianetwork.copralia.Utils.VolleySingleton;
import net.copralianetwork.copralia.R;

import org.json.JSONArray;

/**
 * Created by Marcos on 18/09/2015.
 */
public class Ofertas_Fragment extends Fragment{
        private RecyclerView reciclador;
        private LinearLayoutManager layoutManager;
        private SwipeRefreshLayout refreshLayout;
        private Ofertas_Adapter adaptador;
        public static JSONArray ofertas;
        //TODO: meter en un nuevo hilo la carga de las noticias? imagenes y tal
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragmento_ofertas, container, false);


            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.ofertas_refresh);


            reciclador = (RecyclerView) view.findViewById(R.id.reciclador);

            layoutManager = new LinearLayoutManager(getActivity());
            reciclador.setLayoutManager(layoutManager);
            adaptador = new Ofertas_Adapter(getActivity());
            reciclador.setAdapter(adaptador);

            refreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            cargar();
                        }
                    }
            );
            refreshLayout.setColorSchemeResources(R.color.refresh1, R.color.refresh2, R.color.refresh3, R.color.refresh4);

            cargar();
            setHasOptionsMenu(true);

            return view;
        }


        public void cargar() {
            refreshLayout.setRefreshing(true);

            // Petición GET
            VolleySingleton.
                    getInstance(getActivity()).
                    addToRequestQueue(
                            new JsonArrayRequest(
                                    Request.Method.GET,
                                    CopraliaAPI.getURL()+"ofertas",
                                    new Response.Listener<JSONArray>() {

                                        @Override
                                        public void onResponse(JSONArray response) {
                                            procesarRespuesta(response);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if(isAdded()){
                                                Toast.makeText(getActivity(), getResources().getString(R.string.error_conexion), Toast.LENGTH_LONG).show();
                                                refreshLayout.setRefreshing(false);
                                            }
                                        }
                                    }

                            )
                    );
        }

        private void procesarRespuesta(JSONArray response){
            if(isAdded()){
                try {
                    ofertas = response;
                    adaptador.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                } catch (Exception e) {
                }
            }
        }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuController.setIcon(menu, R.id.menu_principal_escanear);

        menu.add("calvsdao");
    }


}
