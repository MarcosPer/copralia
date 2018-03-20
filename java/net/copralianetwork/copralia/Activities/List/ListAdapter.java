package net.copralianetwork.copralia.Activities.List;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.copralianetwork.copralia.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Adaptador del recycler view
 */
public class ListAdapter extends BaseAdapter{
    protected Context mContext;
    public Cursor mCursor;
    private List<Integer> deletedPos;
    public int totalPos;
    private float totalPrecio;
    private int totalProds;

    public ListAdapter(Context context) {
        super();
        this.mContext = context;
        this.mCursor = null;
        this.deletedPos = new ArrayList<Integer>();
    }

    /* Cambio de cursor para actualizaciones */

    public void updateData(Cursor newCursor) {
        this.deletedPos.clear();
        totalPrecio = 0;
        totalProds = 0;
        mCursor = newCursor;

        Boolean allbuy = true;
        if(mCursor != null){
            if(mCursor.getCount() > 0){
                while (mCursor.moveToNext()){
                    int buy = mCursor.getInt(mCursor.getColumnIndex("buy"));
                    if(buy != 1 && allbuy){
                        allbuy = false;
                    }
                    Float precio = mCursor.getFloat(mCursor.getColumnIndex("precio"));
                    Float cantidad = mCursor.getFloat(mCursor.getColumnIndex("cant"));
                    totalProds = totalProds + Math.round(cantidad);
                    totalPrecio = totalPrecio + (cantidad * precio);
                }
                if(allbuy){
                    if(mContext instanceof ListActivity){
                        ((ListActivity) mContext).onChangeState(ListActivity.COMPLETED_MODE);
                    }
                }else{
                    if(mContext instanceof ListActivity){
                        ((ListActivity) mContext).onChangeState(ListActivity.NORMAL_MODE);
                    }
                }
            }else{
                if(mContext instanceof ListActivity){
                    ((ListActivity) mContext).onChangeState(ListActivity.EMPTY_MODE);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mCursor == null){
            return 0;
        }else{
            if(mCursor.getCount() == 0){
                return 0;
            }else{
                /* Sumamos 1 para añadir el total */
                return mCursor.getCount()+1;
            }
        }
    }

    @Override
    public Object getItem(int position){
        return null;
    }
    public String getProdName(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(mCursor.getColumnIndex("nombre"));
    }
    public String getProdID(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(mCursor.getColumnIndex("prodID"));
    }
    public Integer getProdCant(int position) {
        mCursor.moveToPosition(position);
        return (int) mCursor.getDouble(mCursor.getColumnIndex("cant"));
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Si esta eliminado en lado del cliente
        if(deletedPos.contains(position)){
            return new View(mContext);
        }

        if((position+1) == getCount()){
            totalPos = position;
            ListTotal aux = new ListTotal(mContext);
            aux.setCant(totalProds);
            aux.setTotal(totalPrecio);
            convertView = aux;
        }else{

            mCursor.moveToPosition(position);
            ProdItem aux = new ProdItem(this,mCursor.getString(mCursor.getColumnIndex("prodID")));
            convertView = aux;
            aux.setName(mCursor.getString(mCursor.getColumnIndex("nombre")));
            aux.setMarca(mCursor.getString(mCursor.getColumnIndex("marca")));
            aux.setCant(mCursor.getDouble(mCursor.getColumnIndex("cant")),mCursor.getDouble(mCursor.getColumnIndex("precio")));
            aux.setCheck(mCursor.getInt(mCursor.getColumnIndex("buy")));
            if((position+2) == getCount()){
                aux.findViewById(R.id.prod_list_divisor).setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public void remove(int position){
        deletedPos.add(position);
        notifyDataSetChanged();
    }

    private void onCheckedItem(String itemID, Boolean status){
        if(mContext instanceof ListActivity){
            ((ListActivity) mContext).onCheckedItem(itemID,status);
        }
    }

    private static class ProdItem extends FrameLayout implements CompoundButton.OnCheckedChangeListener {
        public TextView nombre;
        public TextView marca;
        public TextView cantidad;
        public CheckBox check;

        public String ID;
        private Context mContext;
        private ListAdapter mAdapter;

        public ProdItem(ListAdapter adapter,String prodID) {
            super(adapter.mContext);
            mContext = adapter.mContext;
            mAdapter = adapter;

            this.ID = prodID;
            inflate(mContext, R.layout.item_lista_producto,this);
            nombre = (TextView) findViewById(R.id.prod_name);
            marca = (TextView) findViewById(R.id.prod_marca);
            cantidad = (TextView) findViewById(R.id.prod_cantprice);
            check = (CheckBox) findViewById(R.id.prod_check);

        }

        public void setName(String name){
            nombre.setText(name);
        }
        public void setMarca(String name){
            marca.setText(name);
        }
        public void setCant(double cant, double price){ cantidad.setText(String.format("%.0f x %.2f €",cant,price));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mAdapter.onCheckedItem(ID,isChecked);
        }

        public void setCheck(Integer status) {
            if(status != null){
                if(status == 1){
                    check.setOnCheckedChangeListener (null);
                    check.setChecked(true);
                    check.setOnCheckedChangeListener(this);
                }else{
                    check.setOnCheckedChangeListener (null);
                    check.setChecked(false);
                    check.setOnCheckedChangeListener(this);
                }
            }
        }
    }
    private static class ListTotal extends FrameLayout{
        public TextView total;
        public TextView prods;
        public String ID;
        private Context mContext;

        public ListTotal(Context ctx) {
            super(ctx);
            mContext = ctx;
            inflate(mContext, R.layout.actividad_lista_total,this);
            total = (TextView) findViewById(R.id.total_value);
            prods = (TextView) findViewById(R.id.articulos_value);
        }

        public void setTotal(Float tota){
            total.setText(String.format("%.2f €",tota));
        }
        public void setCant(int cant){
            prods.setText(String.valueOf(cant));
        }
    }
}
