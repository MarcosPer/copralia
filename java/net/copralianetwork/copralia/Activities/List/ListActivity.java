package net.copralianetwork.copralia.Activities.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;

import net.copralianetwork.copralia.Activities.ActividadProducto;
import net.copralianetwork.copralia.Activities.ProdFav.ProdFavActivity;
import net.copralianetwork.copralia.Activities.Scanner.ScannerActivity;
import net.copralianetwork.copralia.Activities.SearchActivity;
import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Sync.CloudActions;
import net.copralianetwork.copralia.Sync.FirebaseEngine;
import net.copralianetwork.copralia.Sync.Lists.ListSync;
import net.copralianetwork.copralia.Sync.Lists.ListsProvider;
import net.copralianetwork.copralia.Sync.Lists.Models.List;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.CopraliaAPI;
import net.copralianetwork.copralia.Utils.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Marcos on 29/11/2015.
 * INPUTS
 * listID
 */
public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>,View.OnClickListener,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    private ListAdapter adapter;
    private List mList;
    private int longSelect;
    private Context mContext;

    private Boolean forcedView = false;

    private CloudActions cloudActions;

    //IDs estado
    public final static int EMPTY_MODE = 1;
    public final static int NORMAL_MODE = 2;
    public final static int COMPLETED_MODE = 3;
    public int CURRENT_MODE;

    //IDs menu
    public final static int MENU_ELIMINAR = 1;
    public final static int MENU_INFO = 2;
    public final static int MENU_CANTIDAD = 3;

    //IDs menu opciones
    public final static int OPCION_VACIAR = 1;
    public final static int OPCION_ACTUALIZAR = 2;

    //Views
    private FloatingActionMenu addMenu;
    private FrameLayout content;

    private SwipeToDismissTouchListener touchListener;
    public void onCheckedItem(String prodID, Boolean status){
        cloudActions.updateProdListBuy(mList.getListID(),prodID,status);
    }


    public void onChangeState(int state){
        if(!forcedView){
            setViewMode(state);
        }
    }

    public void setViewMode(int mode){
        content.removeAllViews();

        switch (mode){
            case NORMAL_MODE:
                LayoutInflater.from(mContext).inflate(R.layout.actividad_lista_listado,content);
                addMenu.setVisibility(View.VISIBLE);

                ListView listView = (ListView) findViewById(R.id.list_view);

                listView.setAdapter(adapter);
                touchListener = new SwipeToDismissTouchListener<>(new ListViewAdapter(listView), this);
                touchListener.setDismissDelay(3000);
                listView.setOnTouchListener(touchListener);
                listView.setDivider(null);
                listView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
                listView.setOnItemClickListener(this);
                listView.setOnItemLongClickListener(this);
                listView.setOnCreateContextMenuListener(this);
                break;
            case COMPLETED_MODE:
                LayoutInflater.from(mContext).inflate(R.layout.actividad_lista_completa,content);
                addMenu.setVisibility(View.GONE);
                findViewById(R.id.list_completed_ver).setOnClickListener(this);
                findViewById(R.id.list_completed_vaciar).setOnClickListener(this);
                break;
            case EMPTY_MODE:
                LayoutInflater.from(mContext).inflate(R.layout.actividad_lista_vacia,content);
                addMenu.setVisibility(View.VISIBLE);
                break;
            default:
                //setViewMode(NORMAL_MODE);
                return;
        }
        CURRENT_MODE = mode;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.actividad_lista);
        if(!getIntent().hasExtra("listID")){
            finish();
        }
        mList = new List(getIntent().getStringExtra("listID"),this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }


        getSupportActionBar().setTitle(mList.getName());
        getSupportActionBar().setSubtitle("ID: " + mList.getListID());

        cloudActions = new CloudActions(this);
        adapter = new ListAdapter(this);

        addMenu = (FloatingActionMenu) findViewById(R.id.add_menu);
        content = (FrameLayout) findViewById(R.id.main_content);

        getSupportLoaderManager().initLoader(0, null, this);

        findViewById(R.id.add_menu_busqueda).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FloatingActionMenu) findViewById(R.id.add_menu)).close(true);
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("type",SearchActivity.TYPE_SELECT);
                intent.putExtra("keyboard",SearchActivity.KEYBOARD_OPEN);
                startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.add_menu_favoritos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FloatingActionMenu) findViewById(R.id.add_menu)).close(true);
                Intent intent = new Intent(getApplicationContext(),ProdFavActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.add_menu_codigobarras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FloatingActionMenu) findViewById(R.id.add_menu)).close(true);
                Intent intent = new Intent(getApplicationContext(),ScannerActivity.class);
                intent.putExtra("type",ScannerActivity.PROD_SCAN);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v instanceof ListView){
            if(longSelect != adapter.totalPos) {
                menu.setHeaderTitle(adapter.getProdName(longSelect));
                menu.add(0, MENU_CANTIDAD, 0, getString(R.string.lista_cantidad));//groupId, itemId, order, title
                menu.add(0, MENU_INFO, 0, "Informacion");//groupId, itemId, order, title
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case MENU_CANTIDAD:
                LayoutInflater li = (LayoutInflater) ListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View promptView = li.inflate(R.layout.dialog_lista_producto_cantidad, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListActivity.this);
                alertDialogBuilder.setView(promptView);
                alertDialogBuilder.setTitle(getString(R.string.lista_cantidad));
                final NumberPicker selector = (NumberPicker) promptView.findViewById(R.id.producto_cantidad);
                String[] nums = new String[150];
                for(int i=0; i<nums.length; i++){
                    nums[i] = Integer.toString(i+1);
                }

                selector.setMinValue(1);
                selector.setMaxValue(150);
                selector.setWrapSelectorWheel(false);
                selector.setDisplayedValues(nums);
                selector.setValue(adapter.getProdCant(longSelect));
                selector.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cloudActions.updateProdListCant(mList.getListID(),adapter.getProdID(longSelect),selector.getValue());
                    }
                });
                alertDialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
                break;
            case MENU_ELIMINAR:
                break;
            case MENU_INFO:
                break;
        }
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();//flecha de atras
                return true;
            case OPCION_VACIAR:
                new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.lista_vaciar))
                    .setMessage(getString(R.string.lista_vaciar_msg))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            cloudActions.clearList(mList.getListID());
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
                return true;
            case OPCION_ACTUALIZAR:
                ListSync.RemoteListSync(getBaseContext(),mList.getListID());
                Toast.makeText(getBaseContext(),R.string.lista_actualizar_progress,LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Barra de herramientas superior */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,OPCION_VACIAR,1,R.string.lista_vaciar).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(1,OPCION_ACTUALIZAR,1,R.string.lista_actualizar).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+mList.getListID()+"/prods"),null, null, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.updateData(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.updateData(null);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("prodID");
                    cloudActions.addProdList(mList.getListID(),result);
                }
                break;
        }
    }

    @Override
    public boolean canDismiss(int position) {
        if (position == adapter.totalPos){
            return false;
        }else{
            return true;
        }
    }
    @Override
    public void onPendingDismiss(ListViewAdapter recyclerView, int position) {

    }
    @Override
    public void onDismiss(ListViewAdapter view, int position) {
        cloudActions.removeProdList(mList.getListID(),adapter.getProdID(position));
        adapter.remove(position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        addMenu.close(true);
        if (touchListener != null && touchListener.existPendingDismisses()) {
            touchListener.undoPendingDismiss();
        } else {
            if(position != adapter.totalPos){
                Intent intent = new Intent(getBaseContext(), ActividadProducto.class);
                intent.putExtra("prodID", adapter.getProdID(position));
                startActivity(intent);
            }
        }
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        longSelect = position;
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.list_completed_ver:
                forcedView = true;
                setViewMode(NORMAL_MODE);
                break;
            case R.id.list_completed_vaciar:
                cloudActions.clearList(mList.getListID());
                Toast.makeText(getBaseContext(),"Lista vaciada",LENGTH_SHORT).show();
                break;
        }
    }
}
