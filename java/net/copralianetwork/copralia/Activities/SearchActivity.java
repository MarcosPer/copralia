package net.copralianetwork.copralia.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.copralianetwork.copralia.Adapter.Search_Adapter;
import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.CopraliaAPI;
import net.copralianetwork.copralia.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Marcos on 03/12/2015.
 *
 * INPUTS
 *  query
 *  type DEFAULT TYPE_SEARCH
 *  keyboard DEFAULT KEYBOARD_CLOSE
 */
public class SearchActivity extends AppCompatActivity implements Search_Adapter.OnItemClickListener{

    private int paginado = 10;


    public static int TYPE_SEARCH = 1;
    public static int TYPE_SELECT = 2;
    public static int KEYBOARD_CLOSE = 1;
    public static int KEYBOARD_OPEN = 2;

    private SearchView buscador;
    private RecyclerView lista;
    private TextView texto;
    private Search_Adapter adapter;
    private GridLayoutManager layoutManager;
    private Boolean masResultados;
    public  JSONArray resultados;


    private String query;
    private int page;

    private boolean cargando;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();//flecha de atras
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_busqueda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lista = (RecyclerView) findViewById(R.id.reciclador);
        texto = (TextView) findViewById(R.id.busqueda_no_resultados);


        adapter = new Search_Adapter(getBaseContext(),this);
        adapter.setOnItemClickListener(this);
        lista.setAdapter(adapter);

        layoutManager = new GridLayoutManager(getBaseContext(), getIntent().getIntExtra("grid",2));
        lista.setLayoutManager(layoutManager);


        lista.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = lista.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if (!cargando && masResultados) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            cargar(query);
                        }
                    }
                }
            }
        });


        if(getIntent().hasExtra("query")){
            cargar(getIntent().getStringExtra("query"));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_busqueda, menu);//AÑADIR BARRA DE HERRAMIENTAS SUPERIOR
        buscador = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.buscador));
        buscador.setQueryHint("Busca productos");
        int keyboard = getIntent().getIntExtra("keyboard",1);
        if(keyboard == KEYBOARD_OPEN){
            buscador.setIconifiedByDefault(false);
            buscador.requestFocus();
        }

        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscador.onActionViewCollapsed();
                cargar(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }
    public void cargar(String busqueda) {
        cargando = true;

        if(query == busqueda){
            page++;
        }else if(query != busqueda){
            resultados = null;
            page = 1;
            query = busqueda;
        }

        final boolean first;
        if(resultados == null){
            first = true;
        }else{
            first = false;
        }

        if(first){
            lista.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.busqueda_header)).setText(busqueda);
            findViewById(R.id.busqueda_no_resultados).setVisibility(View.GONE);
            findViewById(R.id.busqueda_carga).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.carga_banner).setVisibility(View.VISIBLE);
        }

        VolleySingleton.getInstance(getBaseContext()).
                addToRequestQueue(
                        new APIRequest(getBaseContext(),
                                Request.Method.GET,
                                CopraliaAPI.getURL() + "/Producto/search?busqueda=" + Uri.encode(busqueda) + "&pagina=" + page + "&resultados=" + paginado,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if (first) {
                                            findViewById(R.id.busqueda_carga).setVisibility(View.GONE);
                                        } else {
                                            findViewById(R.id.carga_banner).setVisibility(View.GONE);
                                        }
                                        cargando = false;
                                        procesarRespuesta(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (first) {
                                            findViewById(R.id.busqueda_carga).setVisibility(View.GONE);
                                        } else {
                                            findViewById(R.id.carga_banner).setVisibility(View.GONE);
                                        }
                                        cargando = false;
                                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_conexion), Toast.LENGTH_LONG).show();
                                    }
                                }
                        )
                );
    }

    private void procesarRespuesta(JSONObject response){
            try {
                if(response.getInt("status") == 1){
                    if(resultados == null){
                        lista.setVisibility(View.VISIBLE);
                    }
                    if(response.getJSONArray("data").length() == paginado){//no hay mas páginas
                        masResultados= true;
                    }else{
                        masResultados = false;
                    }
                    if(resultados != null){
                        resultados = concatArray(resultados,response.getJSONArray("data"));
                    }else{
                        resultados = response.getJSONArray("data");
                    }
                    adapter.notifyDataSetChanged();
                }else if(response.getInt("status") == 0){
                    masResultados=false;
                    if(resultados == null){
                        lista.setVisibility(View.GONE);
                        findViewById(R.id.busqueda_no_resultados).setVisibility(View.VISIBLE);
                    }
                }
            }catch (Exception e){
            }
    }

    @Override
    public void onItemClick(Search_Adapter.ViewHolder item, int position) {
        int type = getIntent().getIntExtra("type", 1);
        if(type == TYPE_SEARCH){
            Bitmap bitmap = ((BitmapDrawable)item.imagen.getDrawable()).getBitmap();//Pasamos la imagen
            Intent intent = new Intent(getBaseContext(), ActividadProducto.class);
            intent.putExtra("prodID", item.ID);
            intent.putExtra("nombre", item.nombre.getText());
            if(bitmap != null){
                intent.putExtra("imagen", bitmap);
            }
            startActivity(intent);
        }else if(type == TYPE_SELECT){
            Intent intent = new Intent();
            intent.putExtra("prodID",item.ID);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
    private JSONArray concatArray(JSONArray arr1, JSONArray arr2) throws JSONException {
        JSONArray result = new JSONArray();
        for (int i = 0; i < arr1.length(); i++) {
            result.put(arr1.get(i));
        }
        for (int i = 0; i < arr2.length(); i++) {
            result.put(arr2.get(i));
        }
        return result;
    }
}


