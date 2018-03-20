package net.copralianetwork.copralia.Models;

import org.json.JSONObject;

/**
 * Created by marcos on 27/09/16.
 */

public class Producto {

    public static Producto newFromJson(JSONObject prod){
        Producto aux = new Producto();
        aux.fromJson(prod);
        return aux;
    }

    public void fromJson(JSONObject prod){

    }
}
