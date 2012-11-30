package t90.com.github.wifilogin.SyncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import t90.com.github.wifilogin.WifiSyncChecker;


/**
 * User: VasiltsV
 * Date: 11/29/12
 * Time: 2:35 PM
 */
public class SyncAdapterImplementation extends AbstractThreadedSyncAdapter {
    private AccountManager _accountManager;
    private WifiSyncChecker _wifiSyncChecker;

    public SyncAdapterImplementation(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        _wifiSyncChecker = new WifiSyncChecker();
    }

    @Override
    public void onPerformSync(final Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        _wifiSyncChecker.onSync(account,authority,provider,syncResult,getContext());
    }
}
