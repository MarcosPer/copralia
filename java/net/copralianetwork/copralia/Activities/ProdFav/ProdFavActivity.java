package net.copralianetwork.copralia.Activities.ProdFav;

/**
   Solo sirve de selector. No elimina
*/

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Sync.Account.AccountProvider;


public class ProdFavActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private ListView listView;
    private ProdFavAdapter adapter;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.prodfav_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        getSupportActionBar().setTitle(getString(R.string.prodfav_activity_nombre));
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ProdFavAdapter(getBaseContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        getSupportLoaderManager().initLoader(0, null, this);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                    this.finish();//flecha de atras
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("prodID",adapter.getProdID(position));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), Uri.withAppendedPath(AccountProvider.URI_ACCOUNT,"prodfav"),null, null, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.updateData(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.updateData(null);
    }



}
