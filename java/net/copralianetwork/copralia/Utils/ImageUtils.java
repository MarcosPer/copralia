package net.copralianetwork.copralia.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Marcos on 02/12/2015.
 */
public class ImageUtils {
    public static void saveToInternalSorage(Context ctx,Bitmap bitmapImage,String ruta, String filename){
        ContextWrapper cw = new ContextWrapper(ctx);
        File directory = cw.getDir(ruta, Context.MODE_PRIVATE);
        File mypath=new File(directory,filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static Bitmap loadImageFromStorage(Context ctx,String path,String filename)
    {
        try {
            ContextWrapper cw = new ContextWrapper(ctx);
            File directory = cw.getDir(path, Context.MODE_PRIVATE);

            File f=new File(directory.getPath(), filename);

            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }
}
