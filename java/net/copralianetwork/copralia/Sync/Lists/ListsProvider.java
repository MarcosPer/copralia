package net.copralianetwork.copralia.Sync.Lists;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import net.copralianetwork.copralia.Utils.CopraliaDB;

public class ListsProvider extends ContentProvider {

    private static String AUTHORITY = "net.copralia.sync.lists";

    /** CONTENTS URIS disponibles **/
    public static Uri URI_LISTAS = Uri.parse("content://" + AUTHORITY );

    /** Tipos de datos que se puede solicitar al contentProvider **/
    private static final int LIST_INDEX = 1;
    private static final int LIST_INFO = 2;
    private static final int LIST_MEMBERS = 3;
    private static final int LIST_PRODS = 4;
    private static final int LIST_MEMBER = 5;
    private static final int LIST_PROD = 6;

    private static final UriMatcher uriMatcher;

    /** TIPOS DE URIS */
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "lists", LIST_INDEX);
        uriMatcher.addURI(AUTHORITY, "list/*/info", LIST_INFO);
        uriMatcher.addURI(AUTHORITY, "list/*/members", LIST_MEMBERS);
        uriMatcher.addURI(AUTHORITY, "list/*/prods", LIST_PRODS);
        uriMatcher.addURI(AUTHORITY, "list/*/member/*", LIST_MEMBER);
        uriMatcher.addURI(AUTHORITY, "list/*/prod/*", LIST_PROD);

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
        String listID;
        String auxID;
        switch (match) {
            case LIST_INDEX:
                c = db.query("list", projection, selection, null, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case LIST_INFO:
                listID = uri.getPathSegments().get(1);
                c = db.query("list", projection, "listID = '"+listID+"'", null, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case LIST_MEMBERS:
                listID = uri.getPathSegments().get(1);
                c = db.query("list_member", projection, "listID = '"+listID+"'", null, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case LIST_PRODS:
                listID = uri.getPathSegments().get(1);
                c = db.rawQuery("select * from list_prod JOIN prod ON prod.prodID = list_prod.prodID where listID = '"+listID+"'",null);
                //c = db.query("list_prod", projection, "listID = '"+listID+"'", null, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case LIST_MEMBER:
                listID = uri.getPathSegments().get(1);
                auxID = uri.getPathSegments().get(3);
                c = db.query("list_member", projection, "listID = '"+listID+"' AND userID='"+auxID+"'", null, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case LIST_PROD:
                listID = uri.getPathSegments().get(1);
                auxID = uri.getPathSegments().get(3);
                c = db.rawQuery("select * from list_prod JOIN prod ON prod.prodID = list_prod.prodID where listID = '"+listID+"' and prodID = "+auxID+"",null);
                //c = db.query("list_prod", projection, "listID = '"+listID+"' AND prodID='"+auxID+"'", null, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            default:
                throw new IllegalArgumentException("URI no valida");
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case LIST_INDEX:
                return "vnd.android.cursor.dir/"+AUTHORITY+"/lists";
            default:
                throw new IllegalArgumentException("Elemento solicitado invalido: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        ContentValues contentValues;
        SQLiteDatabase db = new CopraliaDB(getContext()).getDatabase();
        long rowId;
        switch (uriMatcher.match(uri)){
            case LIST_INDEX:
                rowId = db.insert("list", null, values);
                if (rowId > 0) {
                    /*Notificamos a la URI de listas  y devolvemos la URI de la nueva lista*/
                    getContext().getContentResolver().notifyChange(Uri.withAppendedPath(URI_LISTAS,"lists"), null, false);
                    return Uri.withAppendedPath(URI_LISTAS,"list/"+values.getAsString("listID")+"/info");
                }
                throw new SQLException("Falla al insertar fila en : " + uri);
            case LIST_MEMBERS:
                rowId = db.insert("list_member", null, values);
                if (rowId > 0) {
                    /*Notificamos a la URI de listas  y devolvemos la URI de los miembros*/
                    getContext().getContentResolver().notifyChange(uri, null, false);
                    return uri;
                }
               throw new SQLException("Falla al insertar fila en : " + uri);
            case LIST_PRODS:

                Cursor aux = db.query("prod", null, "prodID = "+values.getAsInteger("prodID"), null, null, null, null);
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
                ContentValues listprodtable = new ContentValues(0);
                listprodtable.put("listID", values.getAsString("listID"));
                listprodtable.put("prodID", values.getAsInteger("prodID"));
                listprodtable.put("precio", values.getAsDouble("precio"));
                listprodtable.put("storeID", values.getAsInteger("storeID"));
                listprodtable.put("cant", values.getAsDouble("cant"));
                listprodtable.put("added", values.getAsString("added"));
                listprodtable.put("addedby", values.getAsString("addedby"));
                listprodtable.put("buy", values.getAsInteger("buy"));

                rowId = db.insert("list_prod", null, listprodtable);
                if (rowId > 0) {
                    /*Notificamos a la URI de listas  y devolvemos la URI de los productos*/
                    getContext().getContentResolver().notifyChange(uri,null);
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
        String listID;
        String auxID;
        Cursor aux;
        int totals;
        switch (uriMatcher.match(uri)){
            case LIST_INFO:
                totals = 0;
                listID = uri.getPathSegments().get(1);
                totals += db.delete("list", "listID='"+listID+"'",null);
                totals += db.delete("list_member", "listID='"+listID+"'",null);

                aux = db.query("list_prod", null, "listID = '"+listID+"'", null, null, null, null);

                /* Eliminamos producto a producto */
                while (aux.moveToNext()){
                    getContext().getContentResolver().delete(Uri.withAppendedPath(URI_LISTAS,"list/"+listID+"/prod/"+aux.getInt(aux.getColumnIndex("prodID"))),null,null);
                    totals ++;
                }
                aux.close();

                return totals;
            case LIST_MEMBER:
                listID = uri.getPathSegments().get(1);
                auxID = uri.getPathSegments().get(3);
                totals = db.delete("list_member","listID = '"+listID+"' AND userID='"+auxID+"'",null);
                getContext().getContentResolver().notifyChange(Uri.withAppendedPath(URI_LISTAS,"list/"+listID+"/members"), null);
                return totals;
            case LIST_PRODS:
                totals = 0;
                listID = uri.getPathSegments().get(1);
                aux = db.query("list_prod", null, "listID = '"+listID+"'", null, null, null, null);

                /* Eliminamos producto a producto */
                while (aux.moveToNext()){
                    getContext().getContentResolver().delete(Uri.withAppendedPath(URI_LISTAS,"list/"+listID+"/prod/"+aux.getString(aux.getColumnIndex("prodID"))),null,null);
                    totals ++;
                }
                aux.close();
                return totals;
            case LIST_PROD:
                listID = uri.getPathSegments().get(1);
                auxID = uri.getPathSegments().get(3);
                Boolean check1 = false;
                Boolean check2 = false;

                /* Eliminamos el registro de la lista a eliminar */
                totals = db.delete("list_prod","listID = '"+listID+"' AND prodID='"+auxID+"'",null);

                /* Comprobamos si mas listas usan el producto a eliminar */
                aux = db.query("list_prod", null, "prodID = "+auxID, null, null, null, null);
                if(aux.getCount() == 0){
                    check1 = true;
                }
                aux.close();

                /* Comprobamos si el producto a eliminar es favorito*/
                aux = db.query("prod_fav", null, "prodID = "+auxID, null, null, null, null);
                if(aux.getCount() == 0){
                    check2 = true;
                }
                aux.close();

                /* Si no se usa en ningun caso, lo eliminamos*/
                if(check1 && check2){
                    db.delete("prod","prodID="+auxID,null);
                }

                getContext().getContentResolver().notifyChange(Uri.withAppendedPath(URI_LISTAS,"list/"+listID+"/prods"), null);
                return totals;
            default:
                throw new IllegalArgumentException("URI desconocida : " + uri);
        }
    }

    /*  Con selection String
        affected = db.update("lists_prods",values,"ID = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),selectionArgs);
    */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = new CopraliaDB(getContext()).getDatabase();
        String listID;
        String auxID;
        int affected = 0;

        switch (uriMatcher.match(uri)){
            //El listIndex no tiene sentido aqui
            case LIST_INFO:
                listID = uri.getPathSegments().get(1);
                affected = db.update("list",values,"listID = '"+listID+"'",null);
                getContext().getContentResolver().notifyChange(Uri.withAppendedPath(URI_LISTAS,"lists"), null);
                getContext().getContentResolver().notifyChange(uri, null);
                return  affected;
            case LIST_MEMBER:
                listID = uri.getPathSegments().get(1);
                auxID = uri.getPathSegments().get(3);
                affected = db.update("list_member",values,"listID = '"+listID+"' AND userID='"+auxID+"'",null);
                getContext().getContentResolver().notifyChange(Uri.withAppendedPath(URI_LISTAS,"list/"+listID+"/members"), null);
                getContext().getContentResolver().notifyChange(uri, null);
                return  affected;
            case LIST_PROD:
                listID = uri.getPathSegments().get(1);
                auxID = uri.getPathSegments().get(3);
                affected = db.update("list_prod",values,"listID = '"+listID+"' AND prodID='"+auxID+"'",null);
                getContext().getContentResolver().notifyChange(Uri.withAppendedPath(URI_LISTAS,"list/"+listID+"/prods"), null);
                getContext().getContentResolver().notifyChange(uri, null);
                return  affected;
            default:
                throw  new IllegalArgumentException("URI desconocida: "+uri);
        }
    }
}