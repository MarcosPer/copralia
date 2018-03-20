package net.copralianetwork.copralia.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.copralianetwork.copralia.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Marcos on 25/10/2015.
 */
public class TablaUtils {

    public static int TypeTextText = 1;
    public static int TypeNetImageText = 2;

    public static void addTableRows(Context ctx, TableLayout table,int tableType, LinkedHashMap<String,String> data){
        for(Map.Entry<String, String> entry : data.entrySet()) {
            addTableRow(ctx,table,tableType,entry.getKey(),entry.getValue());
        }
    }

    public static void addTableRow(Context ctx, TableLayout table,int tableType, String a, String b){
        LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(tableType == TypeTextText){
            TableRow fila = (TableRow) li.inflate(R.layout.tabla_fila_texttext, null);
            ((TextView)fila.findViewById(R.id.tabla_fila_a)).setText(a);
            ((TextView) fila.findViewById(R.id.tabla_fila_b)).setText(b);
            table.addView(fila, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }else if(tableType == TypeNetImageText){
            TableRow fila = (TableRow) li.inflate(R.layout.tabla_fila_textimage, null);
            ((TextView)fila.findViewById(R.id.tabla_fila_b)).setText(a);
            ImageView image = ((ImageView) fila.findViewById(R.id.tabla_fila_a));
            Glide.with(ctx)
                .load(a)
                .into(image);
            table.addView(fila, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

    }
}
