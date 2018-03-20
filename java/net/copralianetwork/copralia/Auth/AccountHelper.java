package net.copralianetwork.copralia.Auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Marcos on 14/10/2015.
 */
public class AccountHelper {

    public static boolean accountExists(Context mContext) {
        Account[] accounts = AccountManager.get(mContext).getAccountsByType(CopraliaAccount.ACCOUNT_TYPE);
        return accounts != null && accounts.length > 0;
    }

    public static String getToken(Context context){

        Account[] cuentas = AccountManager.get(context).getAccountsByType(CopraliaAccount.ACCOUNT_TYPE);

        if(cuentas.length == 0){
            return null;
        }
        Account cuenta = AccountManager.get(context).getAccountsByType(CopraliaAccount.ACCOUNT_TYPE)[0];
        return AccountManager.get(context).getUserData(cuenta,AccountManager.KEY_AUTHTOKEN);
        //TODO: Comprobar y manejar la situacion de que el token sea nulo
    }

    public static String getUserID(Context context){

        Account[] cuentas = AccountManager.get(context).getAccountsByType(CopraliaAccount.ACCOUNT_TYPE);

        if(cuentas.length == 0){
            return null;
        }
        Account cuenta = AccountManager.get(context).getAccountsByType(CopraliaAccount.ACCOUNT_TYPE)[0];
        return AccountManager.get(context).getUserData(cuenta,"userID");
        //TODO: Comprobar y manejar la situacion de que el token sea nulo
    }
}
