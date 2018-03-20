package net.copralianetwork.copralia.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Marcos on 19/10/2015.
 */
public class Database extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "Copraliados.db";
    private static final int DATABASE_VERSION = 2;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE IF NOT EXISTS list(listID VARCHAR(15), name VARCHAR(25), created DATETIME, lastmod DATETIME,storeID INT)");
        //db.execSQL("CREATE TABLE IF NOT EXISTS list_prod(prodID INT, listID VARCHAR(15),nombre VARCHAR(150), marca VARCHAR(50), formato VARCHAR(50), added DATETIME, addedby VARCHAR(10), buy BOOLEAN, precio FLOAT, cant FLOAT,storeID INT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS list_prod(prodID INT, listID VARCHAR(15), added DATETIME, addedby VARCHAR(10), buy BOOLEAN, precio FLOAT, cant FLOAT,storeID INT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS list_member(listID VARCHAR(15), userID VARCHAR(10), role INT,name VARCHAR(20), lastname VARCHAR(20))");
        db.execSQL("CREATE TABLE IF NOT EXISTS store(storeID INT, name VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS prod(prodID INT,nombre VARCHAR(150), marca VARCHAR(50), formato VARCHAR(50))");
        db.execSQL("CREATE INDEX prodID ON prod(prodID)");
        db.execSQL("CREATE TABLE IF NOT EXISTS prod_fav(prodID INT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS home_banner(ID INT,name VARCHAR,date DATETIME,url VARCHAR,intent VARCHAR,image VARCHAR )");
        db.execSQL("CREATE INDEX ID ON home_banner(ID)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE IF NOT EXISTS home_banner(ID INT,name VARCHAR,date DATETIME,url VARCHAR,intent VARCHAR,image VARCHAR )");
        db.execSQL("CREATE INDEX ID ON home_banner(ID)");

    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
