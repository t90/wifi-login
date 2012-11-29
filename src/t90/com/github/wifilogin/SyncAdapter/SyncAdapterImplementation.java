package t90.com.github.wifilogin.SyncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * User: VasiltsV
 * Date: 11/29/12
 * Time: 2:35 PM
 */
public class SyncAdapterImplementation extends AbstractThreadedSyncAdapter {
    private Context _context;
    private AccountManager _accountManager;

    public SyncAdapterImplementation(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        _context = context;
        _accountManager = AccountManager.get(_context);
    }

    @Override
    public void onPerformSync(final Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    }
}
