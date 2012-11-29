package t90.com.github.wifilogin.SyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * User: VasiltsV
 * Date: 11/29/12
 * Time: 2:24 PM
 */
public class AccountAuthenticatorService extends Service {

    private Authenticator _authenticator = null;

    @Override
    public void onCreate() {
        if(_authenticator == null){
            _authenticator = new Authenticator(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(_authenticator != null)
            return _authenticator.getIBinder();
        throw new RuntimeException("Authenticator is null in AccountAuthenticatorService");
    }
}
