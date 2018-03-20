package net.copralianetwork.copralia.Activities.Principal;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.speech.RecognizerIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;

/*import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchBehavior;
import com.lapism.searchview.SearchItem;*/

import net.copralianetwork.copralia.Activities.ActividadProducto;
import net.copralianetwork.copralia.Activities.ProdFav.ProdFavFragment;
import net.copralianetwork.copralia.Activities.SearchActivity;
import net.copralianetwork.copralia.AndroidDatabaseManager;
import net.copralianetwork.copralia.Auth.AccountHelper;
import net.copralianetwork.copralia.Fragment.Cuenta_Fragment;
import net.copralianetwork.copralia.Activities.Principal.Fragment.Inicio.InicioFragment;
import net.copralianetwork.copralia.Activities.Principal.Fragment.Listas_Fragment;
import net.copralianetwork.copralia.MenuController;
import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.TestFiles;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.CopraliaAPI;
import net.copralianetwork.copralia.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class ActividadInicio extends AppCompatActivity implements FloatingSearchView.OnSearchListener{

    private DrawerLayout drawerLayout;//BarraLateral
    private SearchView mSearch;//Buscador en toolbar
    private Context mContext;

    private FloatingSearchView mSearchView;
    private FrameLayout mContent;

    private Boolean suggestionsRequested;

    public static final int LOADER_INICIO_BANNER = 101;
    public static final int LOADER_INICIO_LISTAS = 102;


    public static final int VOICE_SEARCH_REQUEST = 40;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        suggestionsRequested = false;
        setContentView(R.layout.main_layout);


        agregarToolbar();//La barra superior que tiene el boton de menu etc

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // el contenido principal
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        mContent = (FrameLayout) findViewById(R.id.contenedor_principal);
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search);

        mSearchView.setOnSearchListener(this);

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                //Log.d("MEITAG","joder si es tan facil "+ newQuery);
                //get suggestions based on newQuery

                if(!suggestionsRequested && !newQuery.trim().isEmpty()){
                    Log.d("MEITAG","primero "+newQuery);
                    suggestionsRequested = true;
                    mSearchView.showProgress();
                    Map<String,String> data = new HashMap<String, String>();
                    data.put("text",newQuery);

                    final APIRequest request = new APIRequest(mContext, Request.Method.POST, "http://192.168.1.218:8080/suggestion",data,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    List<SearchSuggestion> newSuggestions = new ArrayList<SearchSuggestion>();
                                    try {
                                        JSONArray suggestions = response.getJSONArray("suggestions");
                                        for (int i = 0; i < suggestions.length(); i++) {
                                            final JSONObject suggestion = suggestions.getJSONObject(i);
                                            SuggestionItem item = new SuggestionItem();
                                            item.setType(suggestion.getString("type"));
                                            item.setId(suggestion.getString("ID"));
                                            item.setName(suggestion.getString("name"));
                                            item.setDesc(suggestion.getString("desc"));
                                            item.setImage(suggestion.getString("image"));
                                            newSuggestions.add(item);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mSearchView.hideProgress();
                                    mSearchView.swapSuggestions(newSuggestions);
                                    suggestionsRequested = false;

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    /* Si no hay suggestions, salta el error */
                                    APIRequest.APIException exception = (APIRequest.APIException) error.getCause();
                                    Log.d("MEITAG", " lo que pasa, en efecto es "+exception.getErrorMsg());
                                    suggestionsRequested = false;
                                    mSearchView.hideProgress();
                                }
                            }
                    );
                    VolleySingleton.getInstance(mContext).addToRequestQueue(request);
                }else{
                    mSearchView.clearSuggestions();
                }
            }
        });

        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                SuggestionItem suggestion = (SuggestionItem) item;
                textView.setText(suggestion.getName());
                Glide.with(mContext).load(suggestion.getImage()).into(leftIcon);
            }

        });


        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.busqueda_voz:
                        /* Iniciar la búsqueda por voz */
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra("android.speech.extra.DICTATION_MODE", true);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                        try {
                            startActivityForResult(intent, VOICE_SEARCH_REQUEST);
                        } catch (ActivityNotFoundException a) {
                            Toast.makeText(mContext, "No soportado", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });



        FragmentManager fragmentManager = getSupportFragmentManager();

        if (navigationView != null) {
            prepararDrawer(navigationView);
        }
        showSearch();
        fragmentManager.beginTransaction().replace(mContent.getId(), new InicioFragment()).commit();//pagina principal

    }

    private void showSearch(){
        mSearchView.setVisibility(View.VISIBLE);
        findViewById(R.id.searchbar_fondo).setVisibility(View.VISIBLE);
    }

    private void hideSearch(){
        //int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
        mSearchView.setVisibility(View.GONE);
        //mAppbar.getLayoutParams().height = height;
        //mToolbar.getLayoutParams().height = height;
        findViewById(R.id.searchbar_fondo).setVisibility(View.GONE);
        //((ViewGroup.MarginLayoutParams) mContent.getLayoutParams()).setMargins(0, height, 0, 0);
    }

    private void agregarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.drawer_toggle);
            ab.setDisplayHomeAsUpEnabled(true);
        }

    }
    /** DRAWER = MENU LATERAL */


    public void prepararDrawer(NavigationView navigationView) {//Me crea el listener del menu lateral, el listener llama al metodo selecccionar item.
        //METO AQUI MI VOLLEY SINGLETON
/*        VolleySingleton.
                getInstance(getBaseContext()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                CopraliaAPI.getURL()+"user/info?token="+ AccountHelper.getToken(getBaseContext()),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if(response.getInt("status") == 1){
                                                JSONObject data = response.getJSONObject("data");
                                                String aux = "";
                                                if(!TextUtils.isEmpty(data.getString("last_name"))){
                                                    aux = " "+data.getString("last_name").toUpperCase().charAt(0)+".";
                                                }
                                                ((TextView) findViewById(R.id.etiqueta_nombre)).setText(data.getString("name") + aux);
                                                ((TextView) findViewById(R.id.etiqueta_email)).setText(data.getString("email"));
                                            }
                                        } catch (JSONException e) {
                                        }
                                    }
                                },null));*/


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        seleccionarItem(menuItem);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

    }



    public void seleccionarItem(MenuItem itemDrawer) {

        switch (itemDrawer.getItemId()) {
            case R.id.menu_item_ofertas:
                showSearch();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.contenedor_principal, new InicioFragment()).commit();
                //findViewById(R.id.searchbar).setVisibility(View.VISIBLE);
                break;
            case R.id.menu_item_lista:
                hideSearch();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.contenedor_principal, new Listas_Fragment()).commit();
                //findViewById(R.id.searchbar).setVisibility(View.GONE);
                break;
            case R.id.menu_item_cuenta:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.contenedor_principal, new Cuenta_Fragment()).commit();
                break;
            case R.id.menu_item_favoritos:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.contenedor_principal, new ProdFavFragment()).commit();
                break;
            case R.id.menu_item_config:
                Intent dbmanager = new Intent(getBaseContext(),AndroidDatabaseManager.class);
                startActivity(dbmanager);
                break;
            case R.id.menu_item_test1:
                TestFiles.test1(this);
                break;
            case R.id.menu_item_test2:
                TestFiles.test2(this);
                break;
            case R.id.menu_item_test3:
                TestFiles.test3(this);
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actividad_principal, menu);//AÑADIR BARRA DE HERRAMIENTAS SUPERIOR
        MenuController.hideAll(menu);


        mSearch = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_principal_busqueda));
        mSearch.setQueryHint("Busca productos");
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearch.onActionViewCollapsed();//cerrar menu busqueda
                Intent busqueda = new Intent(getBaseContext(), SearchActivity.class);
                busqueda.putExtra("query",query);
                busqueda.putExtra("type",SearchActivity.TYPE_SEARCH);
                startActivity(busqueda);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getBaseContext(), newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });



        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case VOICE_SEARCH_REQUEST:
                if(mSearchView.getVisibility() == View.VISIBLE && resultCode == RESULT_OK){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mSearchView.setActivated(true);
                    mSearchView.setSearchText(result.get(0));
                    onSearchAction(result.get(0));
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Esto es el menu de tres puntoss
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();//cerrar el menu lateral al dar atras
        }else if(!mSearch.isIconified()){
            mSearch.onActionViewCollapsed();//cerrar el buscador superior al dar atras.
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public void onSearchAction(String currentQuery) {
        Intent busqueda = new Intent(getBaseContext(), SearchActivity.class);
        busqueda.putExtra("query",currentQuery);
        busqueda.putExtra("type",SearchActivity.TYPE_SEARCH);
        startActivity(busqueda);
    }

    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        if(searchSuggestion instanceof SuggestionItem){
            SuggestionItem item = (SuggestionItem) searchSuggestion;

                    switch(item.getType()){
                        case "prod":
                            Intent prod = new Intent(getBaseContext(), ActividadProducto.class);
                            prod.putExtra("prodID",item.getId());
                            startActivity(prod);
                            break;
                        case "shop":
                            break;
                        default:
                            break;
                    }
        }
        //onSearchAction(searchSuggestion.getBody());
    }
}