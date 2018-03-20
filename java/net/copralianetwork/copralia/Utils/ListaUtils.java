package net.copralianetwork.copralia.Utils;

import org.json.JSONObject;

/**
 * Created by Marcos on 29/11/2015.
 */
public class ListaUtils {
    public static class Producto{
        private int idProd;
        private String Nombre;
        private String Marca;
        private String Formato;
        private float Precio;
        private float Cantidad;
        private int superID;
        public Producto(int id){
            this.idProd = id;
        }
        public void setNombre(String nombre) {Nombre = nombre;}
        public void setMarca(String marca) {Marca = marca;}
        public void setFormato(String formato) {Formato = formato;}
        public void setPrecio(float precio) {Precio = precio;}
        public void setCantidad(float cantidad) {Cantidad = cantidad;}
        public void setSuperID(int superID) {this.superID = superID;}
        public static  Producto fromJSON(JSONObject data){
            try {
                Producto prod = new Producto(Integer.valueOf(data.getString("prodID")));
                prod.setNombre(data.getString("nombre"));
                prod.setMarca(data.getString("marca"));
                prod.setFormato(data.getString("formato"));
                prod.setPrecio(Float.valueOf(data.getString("precio")));
                prod.setCantidad(Float.valueOf(data.getString("cantidad")));
                prod.setSuperID(Integer.valueOf(data.getString("superID")));
                return prod;
            }catch (Exception e){
                return null;
            }
        }
    }


    /*public static void addList(Context ctx,String listID,String name){
        Cursor c = ctx.getContentResolver().query(SyncListas.URI_LISTAS, null, "ID = " + listID, null, null);
        c.getCount()

    }*/
/*
    public static void addElementToList(Context ctx, Producto prod, String listaid){

        Uri uri_lista = Uri.withAppendedPath(SyncListas.URI_LISTAS,listaid);//
        //TODO REVISAR NULL POINTER
        ctx.getContentResolver().delete(uri_lista, String.valueOf(prod.idProd), null);//ELIMINAR
        ContentValues values;
        values = new ContentValues(0);
        values.put("listID",listaid);
        values.put("prodID",prod.idProd);
        values.put("nombre",prod.Nombre);
        values.put("marca",prod.Marca);
        values.put("formato",prod.Formato);
        values.put("precio",prod.Precio);
        values.put("cantidad",prod.Cantidad);
        values.put("superID",prod.superID);
        values.put("date", DateUtils.getDateTime());
        //values.put("lastMod", DateUtils.getDateTime());
        ctx.getContentResolver().insert(uri_lista, values);//INSERTO
    }
    */
}
