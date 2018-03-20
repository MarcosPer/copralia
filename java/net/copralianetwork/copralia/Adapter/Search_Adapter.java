package net.copralianetwork.copralia.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import net.copralianetwork.copralia.Activities.SearchActivity;
import net.copralianetwork.copralia.Utils.VolleySingleton;
import net.copralianetwork.copralia.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcos on 20/09/2015.
 */
public class Search_Adapter extends RecyclerView.Adapter<Search_Adapter.ViewHolder>{

    private ImageLoader imageLoader;
    private SearchActivity activity;

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
        public TextView nombre;
        public TextView marca;
        public TextView precio;
        public String ID;
        public NetworkImageView imagen;
        private ImageView inSuper;
        private Search_Adapter adapterMAIN = null;

        public ViewHolder(View v,Search_Adapter adapter) {
            super(v);
            adapterMAIN = adapter;

            v.setOnClickListener(this);
            nombre = (TextView) v.findViewById(R.id.prod_name);
            marca = (TextView) v.findViewById(R.id.item_marca);
            precio = (TextView) v.findViewById(R.id.item_precio);
            imagen = (NetworkImageView) v.findViewById(R.id.item_miniatura);
            inSuper = (ImageView) v.findViewById(R.id.super_fav);
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = adapterMAIN.getOnItemClickListener();
            if (listener != null) {
                listener.onItemClick(this, getAdapterPosition());
            }
        }

        public void inSuperFav(Boolean status){
            if(status){
                inSuper.setVisibility(View.GONE);
            }else{
                inSuper.setVisibility(View.VISIBLE);
                precio.setTextColor(Color.parseColor("#E60000"));
            }
        }
    }





    public Search_Adapter(Context ctx, SearchActivity searchActivity) {
        imageLoader = VolleySingleton.getInstance(ctx).getImageLoader();
        activity = searchActivity;
    }

    @Override
    public int getItemCount() {
        if (activity.resultados != null){
            return  activity.resultados.length();
        }else{
            return 0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_busqueda, viewGroup, false);
        return new ViewHolder(v,this);
    }
    public void clear(){
        activity.resultados = null;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try {
            JSONObject item = activity.resultados.getJSONObject(i);
            if(!item.isNull("imagen")){
                viewHolder.imagen.setImageUrl(item.getString("imagen"),imageLoader);
            }
            viewHolder.imagen.setErrorImageResId(R.drawable.no_disponible);
            viewHolder.imagen.setDefaultImageResId(R.drawable.no_disponible);
            viewHolder.nombre.setText(item.getString("nombre"));
            viewHolder.marca.setText(item.getString("marca"));
            viewHolder.precio.setText(item.getString("precio")+" €");
            viewHolder.ID = item.getString("ID");
            viewHolder.inSuperFav(true);
        } catch (JSONException e) {
        }
    }


}
