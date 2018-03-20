package net.copralianetwork.copralia.Sync.App;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import net.copralianetwork.copralia.Utils.CopraliaDB;

/**
 * Created by marcos on 22/11/16.
 */

public class AppProvider extends ContentProvider {


    private static String AUTHORITY = "net.copralia.sync.app";

    /** CONTENTS URIS disponibles **/
    public static Uri URI_HOME_BANNER = Uri.parse("content://" + AUTHORITY + "/banners" );

    /** Tipos de datos que se puede solicitar al contentProvider **/
    private static final int HOME_BANNERS = 1;
    private static final int HOME_BANNER = 2;

    private static final UriMatcher uriMatcher;

    /** TIPOS DE URIS */
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "banners", HOME_BANNERS);
        uriMatcher.addURI(AUTHORITY, "banners/*", HOME_BANNER);
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
        switch (match) {
            case HOME_BANNERS:
                c = db.rawQuery("select * from home_banner",null);
                /* IMPORTANTISIMO, ligamos el cursor a la uri */
                c.setNotificationUri(getContext().getContentResolver(),URI_HOME_BANNER);
                return c;
            case HOME_BANNER:
                String bannerID = uri.getPathSegments().get(1);
                getContext().getContentResolver().notifyChange(uri, null);
                c = db.query("home_banner",projection,"ID = "+bannerID,null,null,null,sortOrder);
                c.setNotificationUri(getContext().getContentResolver(),Uri.withAppendedPath(URI_HOME_BANNER,bannerID));
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
        SQLiteDatabase db = new CopraliaDB(getContext()).getDatabase();
        long rowId;
        switch (uriMatcher.match(uri)){
            case HOME_BANNERS:
                rowId = db.insert("home_banner", null, values);
                if (rowId > 0) {
                    getContext().getContentResolver().notifyChange(URI_HOME_BANNER, null);
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
        int totals;
        switch (uriMatcher.match(uri)){
            case HOME_BANNERS:
                totals = db.delete("home_banner",null,null);
                getContext().getContentResolver().notifyChange(URI_HOME_BANNER, null);
                return totals;
            case HOME_BANNER:
                String bannerID = uri.getPathSegments().get(1);
                totals = db.delete("home_banner","ID = "+bannerID,null);
                getContext().getContentResolver().notifyChange(URI_HOME_BANNER, null);
                return totals;
            default:
                throw new IllegalArgumentException("URI desconocida : " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = new CopraliaDB(getContext()).getDatabase();
        String bannerID;
        int affected = 0;
        switch (uriMatcher.match(uri)){
            case HOME_BANNER:
                bannerID = uri.getPathSegments().get(1);
                affected = db.update("home_banner",values,"ID = '"+bannerID+"'",null);
                getContext().getContentResolver().notifyChange(URI_HOME_BANNER, null);
                getContext().getContentResolver().notifyChange(Uri.withAppendedPath(URI_HOME_BANNER,bannerID), null);
                return  affected;
            default:
                throw  new IllegalArgumentException("URI desconocida: "+uri);
        }
    }
}