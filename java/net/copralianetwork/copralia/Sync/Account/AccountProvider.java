package net.copralianetwork.copralia.Sync.Account;

/**
 * Created by marcos on 6/11/16.
 */
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import net.copralianetwork.copralia.Utils.CopraliaDB;

public class AccountProvider extends ContentProvider {


    private static String AUTHORITY = "net.copralia.sync.account";

    /** CONTENTS URIS disponibles **/
    public static Uri URI_ACCOUNT = Uri.parse("content://" + AUTHORITY );

    /** Tipos de datos que se puede solicitar al contentProvider **/
    private static final int ACCOUNT_PRODFAV = 1;
    private static final int ACCOUNT_PRODSFAV = 2;


    private static final UriMatcher uriMatcher;

    /** TIPOS DE URIS */
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "prodfav/*", ACCOUNT_PRODFAV);
        uriMatcher.addURI(AUTHORITY, "prodfav", ACCOUNT_PRODSFAV);
    }

    @Override
    public boolean onCreate() {
        return false;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = new CopraliaDB(getContext()).getDatabase();
        int match = uriMatcher.match(uri);
        Cursor c;
        String prodID;
        switch (match) {
            case ACCOUNT_PRODSFAV:
                c = db.rawQuery("select * from prod_fav JOIN prod ON prod.prodID = prod_fav.prodID",null);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case ACCOUNT_PRODFAV:
                prodID = uri.getPathSegments().get(1);
                c = db.rawQuery("select * from prod_fav JOIN prod ON prod.prodID = prod_fav.prodID WHERE prod_fav.prodID = "+prodID,null);
                return c;
            default:
                throw new IllegalArgumentException("URI no valida");
        }
    }

    @Override
    public String getType(Uri uri) {
        /*switch (uriMatcher.match(uri)){
            case LIST_INDEX:
                return "vnd.android.cursor.dir/"+AUTHORITY+"/lists";
            default:
                throw new IllegalArgumentException("Elemento solicitado invalido: " + uri);
        }*/
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        ContentValues contentValues;
        SQLiteDatabase db = new CopraliaDB(getContext()).getDatabase();
        long rowId;
        switch (uriMatcher.match(uri)){
            case ACCOUNT_PRODSFAV:
                Cursor aux = db.query("prod_fav", null, "prodID = "+values.getAsInteger("prodID"), null, null, null, null);
                if(aux.getCount() != 0){
                    return uri;
                }

                aux = db.query("prod", null, "prodID = "+values.getAsInteger("prodID"), null, null, null, null);
                /* Si no esta en la tabla de productos, lo añadimos */
                if(aux.getCount() == 0){
                    /* Preparamos datos de la tabla prod */
                    ContentValues prodtable = new ContentValues(0);
                    prodtable.put("prodID", values.getAsInteger("prodID"));
                    prodtable.put("nombre", values.getAsString("nombre"));
                    prodtable.put("marca", values.getAsString("marca"));
                    prodtable.put("formato", values.getAsString("formato"));
                    db.insert("prod",null,prodtable);
                }
                aux.close();

                /* Preparamos los datos de la tabla de list_prod */
                ContentValues prodfavtable = new ContentValues(0);
                prodfavtable.put("prodID", values.getAsInteger("prodID"));

                rowId = db.insert("prod_fav", null, prodfavtable);
                if (rowId > 0) {
                    /*Notificamos a la URI de listas  y devolvemos la URI de los productos*/
                    getContext().getContentResolver().notifyChange(uri, null, false);
                    return uri;
                }
                throw new SQLException("Falla al insertar fila en : " + uri);
            default:
                throw new IllegalArgumentException("URI desconocida : " + uri);
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = new CopraliaDB(getContext()).getDatabase();
        String prodID;
        Cursor aux;
        int totals = 0;
        switch (uriMatcher.match(uri)){
            case ACCOUNT_PRODFAV:
                prodID = uri.getPathSegments().get(1);
                Boolean check1 = false;
                Boolean check2 = false;

                /* Eliminamos el registro de la lista a eliminar */
                totals = db.delete("prod_fav","prodID = "+prodID+"",null);
                /* Comprobamos si mas listas usan el producto a eliminar */
                aux = db.query("list_prod", null, "prodID = "+prodID, null, null, null, null);
                if(aux.getCount() == 0){
                    check1 = true;
                }
                aux.close();

                /* Comprobamos si el producto a eliminar es favorito*/
                aux = db.query("prod_fav", null, "prodID = "+prodID, null, null, null, null);
                if(aux.getCount() == 0){
                    check2 = true;
                }
                aux.close();

                /* Si no se usa en ningun caso, lo eliminamos*/
                if(check1 && check2){
                    db.delete("prod","prodID="+prodID,null);
                }

                getContext().getContentResolver().notifyChange(Uri.withAppendedPath(URI_ACCOUNT,"prodfav"), null);
                return totals;
            case ACCOUNT_PRODSFAV:
                aux = db.query("prod_fav", null, "", null, null, null, null);
                /* Eliminamos producto a producto */
                while (aux.moveToNext()){
                    getContext().getContentResolver().delete(Uri.withAppendedPath(URI_ACCOUNT,"prodfav/"+aux.getString(aux.getColumnIndex("prodID"))),null,null);
                    totals ++;
                }
                aux.close();
                return totals;
            default:
                throw new IllegalArgumentException("URI desconocida : " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return  0;
    }
}