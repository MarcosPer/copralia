package net.copralianetwork.copralia.Sync.Lists.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import net.copralianetwork.copralia.Sync.Lists.ListSync;
import net.copralianetwork.copralia.Sync.Lists.ListsProvider;
import net.copralianetwork.copralia.Sync.Lists.MemberUtils;
import net.copralianetwork.copralia.Sync.Lists.ProdUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by marcos on 28/09/16.
 */
public class List {
    private String listID;
    private Context mContext;

    public List(String id, Context ctx){
        this.listID = id;
        this.mContext = ctx;
    }

    public String getListID(){
        return listID;
    }

    public String getName(){
        Cursor cursor = mContext.getContentResolver().query(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+this.listID+"/info"),null,null,null,null);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex("name"));
        cursor.close();
        return name;
    }

    public void remoteSync(){
        ListSync.RemoteListSync(mContext,getListID());
    }

    public void removeList(){
        mContext.getContentResolver().delete(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+listID+"/info"),null,null);
    }

    public void addProduct(Map<String,String> message) throws NullPointerException{
        ContentValues aux = ProdUtils.getContentValues(message);
        if(aux != null){
            aux.put("listID",this.listID);
            mContext.getContentResolver().insert(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+this.listID+"/prods"), aux);
        }else{
            Log.d("COPRALIAPP","Producto no añadido error 10520");
        }
    }
    public void addProduct( JSONObject message) throws NullPointerException{
        ContentValues aux = ProdUtils.getContentValuesObject(message);
        if(aux != null){
            aux.put("listID",this.listID);
            mContext.getContentResolver().insert(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+this.listID+"/prods"), aux);
        }else{
            Log.d("COPRALIAPP","Producto no añadido error 10520");
        }
    }
    public void updateProduct(Map<String,String> message) throws NullPointerException,ListSync.UnSyncedList {
        ContentValues aux = ProdUtils.getContentValues(message);
        if(aux != null){
            int result = mContext.getContentResolver().update(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+this.listID+"/prod/"+message.get("prodID")), aux,null,null);
            if(result == 0){
                throw new ListSync.UnSyncedList(getListID());
            }
        }else{
            Log.d("COPRALIAPP","Producto no actualizado error 10521");
        }
    }
    public void removeProduct(Map<String,String> message) throws NullPointerException,ListSync.UnSyncedList {
        int result = mContext.getContentResolver().delete(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+this.listID+"/prod/"+message.get("prodID")),null,null);
        if(result == 0){
            throw new ListSync.UnSyncedList(getListID());
        }

    }
    public void addMember(JSONObject member){
        ContentValues aux = MemberUtils.getContentValues(member);
        if(aux != null){
            aux.put("listID",this.listID);
            mContext.getContentResolver().insert(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+this.listID+"/members"), aux);
        }else{
            Log.d("COPRALIAPP","Miembro no añadido error 10620");
        }
    }
    public void updateMember(JSONObject member) throws JSONException, ListSync.UnSyncedList {
        /*ContentValues aux = ProdUtils.getContentValues(member);
        if(aux != null){
            int result = mContext.getContentResolver().update(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+this.listID+"/member/"+member.getString("userID")), aux,null,null);
            if(result == 0){
                throw new ListSync.UnSyncedList(getListID());
            }
        }else{
            Log.d("COPRALIAPP","Miembro no actualizado error 10621");
        }*/
    }
    public void removeMember(JSONObject member) throws ListSync.UnSyncedList, JSONException {
        int result = mContext.getContentResolver().delete(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+this.listID+"/member/"+member.getString("userID")),null,null);
        if(result == 0){
            throw new ListSync.UnSyncedList(getListID());
        }
    }

    public void clear() {
        mContext.getContentResolver().delete(Uri.withAppendedPath(ListsProvider.URI_LISTAS,"list/"+this.listID+"/prods"),null,null);
    }
}
