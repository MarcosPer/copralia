package net.copralianetwork.copralia.Activities.Shop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import net.copralianetwork.copralia.Fragment.Cuenta_Perfil_Fragment;
import net.copralianetwork.copralia.Fragment.Cuenta_Supers_Fragment;
import net.copralianetwork.copralia.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcos on 24/03/17.
 */


public class ShopActivity extends AppCompatActivity {

    private Context mContext;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.shop_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        getSupportActionBar().setTitle("TiendaNombre");

        mViewPager = (ViewPager) findViewById(R.id.pager);
        FragmentsAdapter adapter = new FragmentsAdapter(getSupportFragmentManager());
        adapter.addFragment(new Cuenta_Perfil_Fragment(),getString(R.string.shop_tab_info));
        adapter.addFragment(new Cuenta_Supers_Fragment(),getString(R.string.shop_tab_cards));
        adapter.addFragment(new Cuenta_Perfil_Fragment(),getString(R.string.shop_tab_shops));
        mViewPager.setAdapter(adapter);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actividad_producto, menu);
        super.onCreateOptionsMenu(menu);
        /*
        Cursor resp = getContentResolver().query(Uri.withAppendedPath(AccountProvider.URI_ACCOUNT,"prodfav/"+this.prodID),null,null,null,null);
        MenuItem favButton =menu.findItem(R.id.menu_favorito);

        if(resp != null && resp.getCount() != 0){
            favButton.setIcon(R.drawable.favorite_on);
            this.favorite = true;
        }else{
            favButton.setIcon(R.drawable.favorito_negro_off);
            this.favorite = false;
        }*/
        return true;
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }*/



    public class FragmentsAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentsTitle = new ArrayList<>();

        public FragmentsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentsTitle.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentsTitle.get(position);
        }
    }

}
