package net.copralianetwork.copralia.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Marcos on 02/12/2015.
 */
public class SuperUtils {

    public static String IMAGESRUTE = "/supers/";
    public static void getSuperImage(Context ctx,String superid,LocalNetworkImageView imagen){
        Bitmap aux;
        aux = ImageUtils.loadImageFromStorage(ctx,IMAGESRUTE,superid+".jpg");
        if(aux != null){
            imagen.setLocalImageBitmap(aux);
        }
        Log.d("MS","no hay imagen en local, descargamos");

        //return aux;
    }
}
