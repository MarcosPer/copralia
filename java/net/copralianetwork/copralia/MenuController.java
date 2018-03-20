package net.copralianetwork.copralia;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Marcos on 22/09/2015.
 */

http://stackoverflow.com/questions/30649714/android-how-to-change-menu-item-at-page-scrolling-with-viewpager


public class MenuController {

    private static int[] fijos = { R.id.menu_principal_busqueda,R.id.menu_principal_settings};

    public static void hideAll(Menu menu){
        for(int i=0; i<menu.size();i++){//mientras que sea verdad se ejecuta
            MenuItem item = menu.getItem(i);
            if(!checkInArray(item.getItemId(),fijos)){
                item.setVisible(false);
            }
        }
    }

    public static void setIcon(Menu menu, int Resource){
        for(int i=0; i<menu.size();i++){//mientras que sea verdad se ejecuta
            MenuItem item = menu.getItem(i);
            if(!checkInArray(item.getItemId(),fijos)){
                if(item.getItemId() == Resource){
                    item.setVisible(true);
                }else{
                    item.setVisible(false);
                }
            }
        }
    }
    private static boolean checkInArray(int currentState, int[] myArray) {
        boolean found = false;
        for (int i = 0; i < myArray.length; i++) {
            if(myArray[i] == currentState) {
                found = true;
                break;
            }
        }
        return found;
    }
}
