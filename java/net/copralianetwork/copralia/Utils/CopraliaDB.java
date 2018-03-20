package net.copralianetwork.copralia.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Marcos on 20/10/2015.
 */
public class CopraliaDB {

    private Database openHelper;
    private SQLiteDatabase database;

    public CopraliaDB(Context context) {
        //Creando una instancia hacia la base de datos
        openHelper = new Database(context);
        database = openHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase(){
        return database;
    }

    public Cursor get(String query){
        return database.rawQuery(query,null);
    }

}
