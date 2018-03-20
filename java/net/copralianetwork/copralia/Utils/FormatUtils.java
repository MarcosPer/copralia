package net.copralianetwork.copralia.Utils;

/**
 * Created by Marcos on 01/12/2015.
 */
public class FormatUtils {
    /**
    * ELIMINA EL .0 de los float si es incesario
     */
    public static String formatFloat(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }
}
