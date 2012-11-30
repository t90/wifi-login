package t90.com.github.wifilogin.SyncAdapter;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import t90.com.github.wifilogin.R;

/**
 * User: VasiltsV
 * Date: 11/28/12
 * Time: 12:26 PM
 */
public class LoginActivity extends AccountAuthenticatorActivity implements View.OnClickListener {
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
    public static final String ACCOUNT_TYPE = "t90.com.github.wifilogin.account";
    private Button _button;
    private static final String TAG = "wifilogin.LoginActivity";

    public LoginActivity() {
        super();

    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.login);
        _button = (Button) findViewById(R.id.login_ok);
        _button.setOnClickListener(this);
    }


    /**
     * Executed when 'Ok' button gets clicked. Business meaning is creating a new account
     * @param view
     */
    @Override
    public void onClick(View view) {
        createAccount();


    }

    public static void createAccount(Context context, String userName, String password){
        AccountManager accountManager = AccountManager.get(context);
        Account[] myAccounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        for(int i = 0; i < myAccounts.length; i++){
            accountManager.removeAccount(myAccounts[i], null, null);
        }

        Account account = new Account(userName, ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, password, null);

        try{
            registerSyncAdapter(account);
        }
        catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    private void createAccount() {
        String userName = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        createAccount(this, userName,password);

        Intent i = new Intent();
        i.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
        i.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
        i.putExtra(AccountManager.KEY_AUTHTOKEN, ACCOUNT_TYPE);
        i.putExtra(AccountManager.KEY_PASSWORD, password);

        this.setAccountAuthenticatorResult(i.getExtras());
        this.setResult(RESULT_OK, i);


    }

    private static void registerSyncAdapter(Account account) {
        Bundle params = new Bundle();
        params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
        params.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
        params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);


        ContentResolver.setIsSyncable(account,"com.android.contacts",1);
        ContentResolver.addPeriodicSync(account, "com.android.contacts", params, 15);
        ContentResolver.setSyncAutomatically(account, "com.android.contacts", true);
        ContentResolver.requestSync(account,"com.android.contacts",params);

//        SharedPreferences generalPreferences = getSharedPreferences("GeneralPreferences", MODE_PRIVATE);
//        SharedPreferences.Editor edit = generalPreferences.edit();
//        edit.putString("URL",_serviceUrl.getText().toString());
//        edit.commit();

    }
}
