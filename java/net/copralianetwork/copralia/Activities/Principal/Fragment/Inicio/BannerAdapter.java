package net.copralianetwork.copralia.Activities.Principal.Fragment.Inicio;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.copralianetwork.copralia.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by marcos on 21/11/16.
 */

public class BannerAdapter extends PagerAdapter{
    private Context mContext;
    private Cursor mCursor;
    public BannerAdapter(Context ctx){
        super();
        this.mContext = ctx;
    }

    @Override
    public int getCount() {
        if(mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragmento_inicio_slideitem,container,false);
        mCursor.moveToPosition(position);

        ImageView image = (ImageView) view.findViewById(R.id.slide_image);
        TextView text = (TextView) view.findViewById(R.id.slide_text);

        text.setText("posicion: "+position);

        /* Cargamos y cacheamos la imagen */
        /*diskCacheStrategy(DiskCacheStrategy.RESULT) */
        Glide.with(mContext).load(mCursor.getString(mCursor.getColumnIndex("image"))).into(image);

        /* Establecer evento de click para abrir la pagina */
        try {
            final String url = mCursor.getString(mCursor.getColumnIndex("url"));
            /* Comprobar si url es válida */
            new URL(url);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mContext.startActivity(intent);
                }
            });
        } catch (MalformedURLException ignored) {
        }

        /* Añadimos la vista al container */
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void updateData(Cursor newcursor) {
        this.mCursor = newcursor;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
