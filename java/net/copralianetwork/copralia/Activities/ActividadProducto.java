package net.copralianetwork.copralia.Activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;

import android.support.v7.widget.Toolbar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Sync.Account.AccountProvider;
import net.copralianetwork.copralia.Sync.CloudActions;
import net.copralianetwork.copralia.Utils.APIRequest;
import net.copralianetwork.copralia.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Marcos on 25/10/2015.
 */

public class ActividadProducto extends AppCompatActivity{
    private boolean imageExpanded;
    private Intent shareIntent = null;
    private boolean favorite = false;
    private int prodID;
    private CloudActions cloudActions;

    /* Reportes */
    private Map<Integer,Integer> reportTypes;

    private Integer reportType = 0;
    private List<String> reportPrices;
    private View reportDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        cloudActions = new CloudActions(this);

        try{
            this.prodID = Integer.parseInt(getIntent().getStringExtra("prodID"));
        }catch (NumberFormatException e){
            finish();
        }

        setContentView(R.layout.actividad_producto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("");
            getSupportActionBar().setTitle("");
        }

        if(getIntent().getParcelableExtra("imagen") != null){
            Bitmap bmp2 = getIntent().getParcelableExtra("imagen");
            ((ImageView) findViewById(R.id.prod_imagen)).setImageBitmap(bmp2);
        }

        if(getIntent().getStringExtra("nombre") != null){
            ((TextView) findViewById(R.id.prod_name)).setText(getIntent().getStringExtra("nombre"));
        }

        findViewById(R.id.prod_imagen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageExpanded) {
                    retractImage();
                }else{
                    expandImage();
                }
            }
        });

        reportPrices = new ArrayList<>();

        reportTypes = new HashMap<>();
        reportTypes.put(R.id.report_type_1,1);
        reportTypes.put(R.id.report_type_2,2);
        reportTypes.put(R.id.report_type_3,3);
        reportTypes.put(R.id.report_type_4,4);
        reportTypes.put(R.id.report_type_5,5);


        cargar();



        /*findViewById(R.id.producto_text).setVisibility(View.VISIBLE);
        findViewById(R.id.producto_info_container).setVisibility(View.VISIBLE);*/
    }

    @Override
    public void onBackPressed() {
        if(!retractImage()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actividad_producto, menu);
        super.onCreateOptionsMenu(menu);

        Cursor resp = getContentResolver().query(Uri.withAppendedPath(AccountProvider.URI_ACCOUNT,"prodfav/"+this.prodID),null,null,null,null);
        MenuItem favButton =menu.findItem(R.id.menu_favorito);

        if(resp != null && resp.getCount() != 0){
            favButton.setIcon(R.drawable.favorite_on);
            this.favorite = true;
        }else{
            favButton.setIcon(R.drawable.favorito_negro_off);
            this.favorite = false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!retractImage()){
                    this.finish();//flecha de atras
                }
                return true;
            case R.id.menu_compartir:
                if(shareIntent != null){
                    startActivity(shareIntent);
                }
                return true;
            case R.id.menu_favorito:
                //El off es un grey 500
                if(favorite){
                    item.setIcon(R.drawable.favorito_negro_off);
                    cloudActions.removeProdFav(String.valueOf(prodID));
                    favorite = false;
                }else{
                    item.setIcon(R.drawable.favorite_on);
                    cloudActions.addProdFav(String.valueOf(prodID));
                    favorite = true;
                }
                return true;
            case R.id.menu_reportar:
                createReportDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean expandImage(){
        ImageView imageView = ((ImageView) findViewById(R.id.prod_imagen));
        if(!imageExpanded) {
            imageExpanded = true;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            return true;
        }
        return false;
    }

    private boolean retractImage(){
        ImageView imageView = ((ImageView) findViewById(R.id.prod_imagen));
        if(imageExpanded) {
            imageExpanded = false;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return true;
        }
        //No se ha retraido la imagen
        return false;
    }
    private void cargar(){

        findViewById(R.id.data_container).setVisibility(View.GONE);
        findViewById(R.id.loading_container).setVisibility(View.VISIBLE);

        final APIRequest request = new APIRequest(this, Request.Method.GET, "http://192.168.1.218:8080/prod/" + this.prodID + "/info",null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        procesarRespuesta(response);
                        //notificacion diciendo ok
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);
    }

    private void procesarRespuesta(JSONObject resp){
        try{


            /* Carga de imagen */
            if(!getIntent().hasExtra("imagen")){
                ImageView imagen = (ImageView) findViewById(R.id.prod_imagen);
                Glide.with(this)
                        .load(resp.getString("image"))
                        .error(R.drawable.no_disponible)
                        .into(imagen);
            }

            /* Carga de precios */
            JSONArray shops = resp.getJSONArray("shops");

            TableLayout pricesTable = (TableLayout) findViewById(R.id.prices_table);
            for (int i = 0; i < shops.length(); i++) {

                JSONObject shop = shops.getJSONObject(i);
                JSONObject shopInfo = shop.getJSONObject("shop");

                TableRow priceRow = (TableRow) getLayoutInflater().inflate(R.layout.prod_activity_price_row,null);

                ((TextView)priceRow.findViewById(R.id.price_value)).setText(String.valueOf(shop.getDouble("price")));

                ImageView image = ((ImageView) priceRow.findViewById(R.id.price_store));
                Glide.with(this)
                        .load("http://192.168.1.45/copralia/shops/banner/"+shopInfo.getString("image"))
                        .override(140,40)
                        .fitCenter()
                        .into(image);
                pricesTable.addView(priceRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                reportPrices.add(shopInfo.getString("name")+" - "+shop.getDouble("price"));
            }

            /* Actualizar vista */
            ((TextView) findViewById(R.id.prod_marca)).setText(resp.getString("brand"));
            ((TextView) findViewById(R.id.prod_name)).setText(resp.getString("name"));
            ((TextView) findViewById(R.id.prod_formato)).setText(resp.getString("format"));

            /* Intent de compartir */
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, resp.getString("name")+ " " + resp.getString("brand")+ " http://www.copralia.com/producto/"+resp.getInt("prodID"));
            shareIntent = Intent.createChooser(share,"Compartir...");

            findViewById(R.id.data_container).setVisibility(View.VISIBLE);
            findViewById(R.id.loading_container).setVisibility(View.GONE);

        }catch (Exception e){
            Toast.makeText(getBaseContext(),getString(R.string.error_conexion),Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void createReportDialog(){
        reportDialog = LayoutInflater.from(this).inflate(R.layout.prod_activity_report, null);
        setActiveReport(null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setView(reportDialog);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reportPrices);
        ((Spinner) reportDialog.findViewById(R.id.report_store)).setAdapter(spinnerAdapter);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sendReport();
            }
        });
        dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void selectReportType(View v) {
        LinearLayout frame = (LinearLayout) v;
        setActiveReport(frame.getChildAt(0).getId());
    }
    public void sendReport(){
        Map<String,String> data = new HashMap<String, String>();
        data.put("prodID",""+prodID);
        data.put("report_type",""+reportType);
        if(reportType == 2){
            String precio =  ((Spinner) reportDialog.findViewById(R.id.report_store)).getSelectedItem().toString();
            data.put("report_extra",precio);
        }else if(reportType == 5){
            String comentario =((EditText) reportDialog.findViewById(R.id.report_otrotext)).getText().toString();
            data.put("report_extra",comentario);
        }
        final APIRequest request = new APIRequest(getBaseContext(), Request.Method.POST, "http://192.168.1.218:8080/prod/" + prodID + "/report",data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getBaseContext(),getString(R.string.prod_report_ok),Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(request);
    }

    public void setActiveReport(Integer id){
        for (Map.Entry<Integer, Integer> e: reportTypes.entrySet()) {
            ImageView image = (ImageView) reportDialog.findViewById(e.getKey());
            if(id!= null && id.equals(e.getKey())){
                if(e.getValue() == 5){
                    reportDialog.findViewById(R.id.report_otrotext).setVisibility(View.VISIBLE);
                    reportDialog.findViewById(R.id.report_store).setVisibility(View.GONE);
                }else if(e.getValue() == 2){
                    reportDialog.findViewById(R.id.report_otrotext).setVisibility(View.GONE);
                    reportDialog.findViewById(R.id.report_store).setVisibility(View.VISIBLE);
                } else{
                    reportDialog.findViewById(R.id.report_otrotext).setVisibility(View.GONE);
                    reportDialog.findViewById(R.id.report_store).setVisibility(View.GONE);
                }
                reportType = e.getValue();
                image.setColorFilter(Color.parseColor("#EF6C00"));
            }else{
                image.setColorFilter(Color.parseColor("#4CAF50"));
            }
        }
    }
}
