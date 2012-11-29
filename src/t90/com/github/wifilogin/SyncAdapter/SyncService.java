package t90.com.github.wifilogin.SyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * User: VasiltsV
 * Date: 11/29/12
 * Time: 2:38 PM
 */
public class SyncService extends Service {

    private SyncAdapterImplementation _syncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        if(_syncAdapter == null){
            _syncAdapter = new SyncAdapterImplementation(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _syncAdapter.getSyncAdapterBinder();
    }
}
