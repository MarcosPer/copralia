package net.copralianetwork.copralia.Adapter;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.copralianetwork.copralia.R;


/**
 * Adaptador del recycler view
 */
public class Listas_Adapter extends RecyclerView.Adapter<Listas_Adapter.ViewHolder>{
    private Cursor cursor;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(ViewHolder item, int position);
    }
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public OnItemClickListener getOnItemClickListener() {
        return listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Campos respectivos de un item
        public TextView monto;
        public TextView etiqueta;
        public TextView fecha;
        public String ID;
        private Listas_Adapter adapterMAIN = null;


        public ViewHolder(View v,Listas_Adapter adapter) {
            super(v);
            adapterMAIN = adapter;
            v.setOnClickListener(this);
            monto = (TextView) v.findViewById(R.id.nombre);
            etiqueta = (TextView) v.findViewById(R.id.etiqueta);
            fecha = (TextView) v.findViewById(R.id.fecha);
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = adapterMAIN.getOnItemClickListener();
            if (listener != null) {
                listener.onItemClick(this, getAdapterPosition());
            }
        }
    }

    public Listas_Adapter(Context context) {
        this.context= context;
    }

    @Override
    public int getItemCount() {
        if (cursor!=null)
            return cursor.getCount();
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lista, viewGroup, false);
        return new ViewHolder(v,this);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        cursor.moveToPosition(i);
        viewHolder.ID = cursor.getString(0);
        viewHolder.monto.setText( cursor.getString(1));
        viewHolder.etiqueta.setText("ID: "+viewHolder.ID);
        viewHolder.fecha.setText("aa");
    }

    public void updateData(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

}
