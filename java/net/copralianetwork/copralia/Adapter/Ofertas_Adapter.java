package net.copralianetwork.copralia.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import net.copralianetwork.copralia.Fragment.Ofertas_Fragment;
import net.copralianetwork.copralia.Utils.VolleySingleton;
import net.copralianetwork.copralia.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcos on 18/09/2015.
 */
public class Ofertas_Adapter extends RecyclerView.Adapter<Ofertas_Adapter.ViewHolder>{
    private ImageLoader imageLoader;

    public Ofertas_Adapter(Context ctx){
        imageLoader = VolleySingleton.getInstance(ctx).getImageLoader();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Campos respectivos de un item
        public TextView nombre;
        public TextView precio;
        public NetworkImageView imagen;

        public ViewHolder(View v) {
            super(v);
            nombre = (TextView) v.findViewById(R.id.nombre_comida);
            precio = (TextView) v.findViewById(R.id.precio_comida);
            imagen = (NetworkImageView) v.findViewById(R.id.oferta_miniatura);
        }
    }

    @Override
    public int getItemCount() {
        if (Ofertas_Fragment.ofertas != null){
            return  Ofertas_Fragment.ofertas.length();
        }else{
            return 0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_oferta, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            JSONObject item = Ofertas_Fragment.ofertas.getJSONObject(position);
            viewHolder.imagen.setImageUrl(item.getString("imagen"),imageLoader);
            viewHolder.imagen.setErrorImageResId(R.drawable.no_disponible);
            viewHolder.imagen.setDefaultImageResId(R.drawable.no_disponible);
            viewHolder.nombre.setText(item.getString("texto1"));
            viewHolder.precio.setText(item.getString("texto2"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
