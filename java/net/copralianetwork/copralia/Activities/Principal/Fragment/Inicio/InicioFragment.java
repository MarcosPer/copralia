package net.copralianetwork.copralia.Activities.Principal.Fragment.Inicio;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.os.Parcel;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import net.copralianetwork.copralia.Activities.Principal.ActividadInicio;
import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Sync.App.AppProvider;
import net.copralianetwork.copralia.Sync.App.AppSync;
import net.copralianetwork.copralia.Utils.Slider.SliderIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by marcos on 21/11/16.
 */

public class InicioFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private BannerView mBannerView;
    private BannerAdapter mBannerAdapter;
    private Boolean mBannerRequest = false;
    private SliderIndicator mIndicator;


    private static final int LOADER_BANNER = 101;
    private static final int LOADER_LISTS = 102;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmento_inicio, container, false);
        
        mBannerAdapter = new BannerAdapter(getActivity());
        mBannerView = (BannerView) view.findViewById(R.id.inicio_slider);
        mBannerView.setAdapter(mBannerAdapter);
        mIndicator = (SliderIndicator) view.findViewById(R.id.slider_indicator);
        mIndicator.setViewPager(mBannerView);



        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSync.remoteHomeBannerSync(getContext());
            }
        });
        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Pepero",Toast.LENGTH_SHORT).show();
                getContext().getContentResolver().delete(AppProvider.URI_HOME_BANNER,null,null);
            }
        });

        getLoaderManager().initLoader(LOADER_BANNER, null, this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 40: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("MEITAG",result.toString());
                }
                break;
            }
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id){
            case LOADER_BANNER:
                return new CursorLoader(getContext(), AppProvider.URI_HOME_BANNER, null, null, null, null);
            case LOADER_LISTS:
                return null;
            default:
                return null;
        }
    }
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /* Cargar el cursor aqui a una mala */
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case LOADER_BANNER:
                /* Se sincronizan (1 vez) los banners si estan vacíos */
                if(data == null || data.getCount() == 0){
                    mIndicator.setVisibility(View.GONE);
                    mBannerView.setVisibility(View.GONE);
                    if(!mBannerRequest){
                        mBannerRequest = true;
                        AppSync.remoteHomeBannerSync(this.getContext());
                    }
                }else{
                    mIndicator.setVisibility(View.VISIBLE);
                    mBannerView.setVisibility(View.VISIBLE);
                    mBannerAdapter.updateData(data);
                }

                break;
            default:
                break;
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case LOADER_BANNER:
                mBannerAdapter.updateData(null);
                break;
            default:
                break;
        }
    }
}