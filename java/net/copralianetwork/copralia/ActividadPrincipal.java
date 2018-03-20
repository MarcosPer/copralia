package net.copralianetwork.copralia;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.copralianetwork.copralia.Activities.Principal.ActividadInicio;
import net.copralianetwork.copralia.Auth.CopraliaAccount;

/**
 * Created by Marcos on 23/09/2015.
 */
public class ActividadPrincipal extends AppCompatActivity {

    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountManager = AccountManager.get(this);



        Account[] cuentas = AccountManager.get(getBaseContext()).getAccountsByType(CopraliaAccount.ACCOUNT_TYPE);
        if(cuentas.length == 0){
            addNewAccount();
        }else{
            startActivity(new Intent(this, ActividadInicio.class));
        }
    }

    private void addNewAccount() {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(CopraliaAccount.ACCOUNT_TYPE, CopraliaAccount.AUTHTOKEN_USER, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    startActivity(new Intent(getBaseContext(),ActividadInicio.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }





}
