package t90.com.github.wifilogin.SyncAdapter;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;
import t90.com.github.wifilogin.R;

/**
 * User: VasiltsV
 * Date: 11/28/12
 * Time: 12:26 PM
 */
public class LoginActivity extends AccountAuthenticatorActivity {
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    public LoginActivity() {
        super();
        setContentView(R.layout.login);
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }
}
