package net.copralianetwork.copralia.Activities.ProdFav;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionMenu;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;

import net.copralianetwork.copralia.Activities.ActividadProducto;
import net.copralianetwork.copralia.Activities.Scanner.ScannerActivity;
import net.copralianetwork.copralia.Activities.SearchActivity;
import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Sync.Account.AccountProvider;
import net.copralianetwork.copralia.Sync.Account.Favs.FavsSync;
import net.copralianetwork.copralia.Sync.CloudActions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by marcos on 7/11/16.
 */
public class ProdFavFragment  extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
    private ProdFavAdapter mAdapter;
    private ListView listview;
    private CloudActions mCloud;
    private View mView;

    private static final int MENU_UPDATE = 10;
    private static final int MENU_CLEAR = 20;

    private static final int LOADER_FAVORITOS = 201;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCloud = new CloudActions(getContext());
        mView = inflater.inflate(R.layout.prodfav_fragment, container, false);

        listview = (ListView) mView.findViewById(R.id.list_view);
        mAdapter = new ProdFavAdapter(getContext());
        listview.setAdapter(mAdapter);

        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(listview),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onPendingDismiss(ListViewAdapter recyclerView, int position) {

                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                mCloud.removeProdFav(mAdapter.getProdID(position));
                                mAdapter.remove(position);
                            }
                        });
        touchListener.setDismissDelay(3000);
        listview.setOnTouchListener(touchListener);
        listview.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    Intent intent = new Intent(getContext(), ActividadProducto.class);
                    intent.putExtra("prodID", mAdapter.getProdID(position));
                    startActivity(intent);
                }
            }
        });
        mView.findViewById(R.id.add_menu_busqueda).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),SearchActivity.class);
                intent.putExtra("type",SearchActivity.TYPE_SELECT);
                intent.putExtra("keyboard",SearchActivity.KEYBOARD_OPEN);
                startActivityForResult(intent, 1);
            }
        });
        mView.findViewById(R.id.add_menu_codigobarras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FloatingActionMenu) mView.findViewById(R.id.add_menu)).close(true);
                Intent intent = new Intent(getContext(),ScannerActivity.class);
                intent.putExtra("type",ScannerActivity.PROD_SCAN);
                startActivityForResult(intent, 1);
            }
        });
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(LOADER_FAVORITOS,null,this);
        return mView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_UPDATE:
                FavsSync.RemoveFavsSync(getContext());
                return true;
            case MENU_CLEAR:
                mCloud.clearProdFav();
                return true;
            default:
                return false;
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0,MENU_UPDATE,0,getString(R.string.prodfav_activity_actualizar)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0,MENU_CLEAR,1,getString(R.string.prodfav_activity_vaciar)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), Uri.withAppendedPath(AccountProvider.URI_ACCOUNT,"prodfav"),null, null, null, null);
        //return  null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case LOADER_FAVORITOS:
                mAdapter.updateData(data);
                break;
            default:
                break;
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case LOADER_FAVORITOS:
                mAdapter.updateData(null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("prodID");
                    mCloud.addProdFav(result);
                }
                break;
        }
    }
}