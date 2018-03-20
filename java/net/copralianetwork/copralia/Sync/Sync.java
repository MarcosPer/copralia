package net.copralianetwork.copralia.Sync;

import android.content.Context;
import android.widget.Toast;

import net.copralianetwork.copralia.R;
import net.copralianetwork.copralia.Sync.Account.Favs.FavsSync;
import net.copralianetwork.copralia.Sync.Account.Favs.ProdFav;
import net.copralianetwork.copralia.Sync.Lists.ListSync;

/**
 * Created by marcos on 26/08/16.
 */
public class Sync {

    public static void FullSync(Context ctx){
        /**
         *
         * TODOOOOO: PONER sistema que nen la exception no diga error conectando con el servidor si no hay información en la peticion
         *
         */
        ListSync.RemoteListsSync(ctx);
        FavsSync.RemoveFavsSync(ctx);
        Toast.makeText(ctx,"Sincronizando",Toast.LENGTH_SHORT).show();
    }


}
