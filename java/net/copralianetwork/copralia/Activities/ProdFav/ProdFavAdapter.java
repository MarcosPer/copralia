package net.copralianetwork.copralia.Activities.ProdFav;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.copralianetwork.copralia.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcos on 8/11/16.
 */

public class ProdFavAdapter extends BaseAdapter{
    private Context mContext;
    private Cursor mCursor;
    private List<Integer> deletedPos;


    public ProdFavAdapter(Context ctx){
        this.mContext = ctx;
        this.deletedPos = new ArrayList<Integer>();

    }

    @Override
    public int getCount() {
        if(mCursor != null){
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(deletedPos.contains(position)){
            return new View(mContext);
        }

        mCursor.moveToPosition(position);
        ProdItem aux = new ProdItem(mContext);
        convertView = aux;
        aux.setName(mCursor.getString(mCursor.getColumnIndex("nombre")));
        aux.setMarca(mCursor.getString(mCursor.getColumnIndex("marca")));

        return convertView;
    }

    public void updateData(Cursor newCursor) {
        this.deletedPos.clear();
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public String getProdID(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(mCursor.getColumnIndex("prodID"));
    }

    public void remove(int position){
        deletedPos.add(position);
        notifyDataSetChanged();
    }


    private static class ProdItem extends FrameLayout {
        // Campos respectivos de un item
        public TextView nombre;
        public TextView marca;

        public String ID;
        private Context mContext;

        public ProdItem(Context ctx) {
            super(ctx);
            mContext = ctx;
            inflate(mContext, R.layout.prodfav_item,this);
            nombre = (TextView) findViewById(R.id.prod_name);
            marca = (TextView) findViewById(R.id.item_marca);
        }

        public void setName(String name){
            nombre.setText(name);
        }
        public void setMarca(String name){
            marca.setText(name);
        }
    }
}
