package t90.com.github.wifilogin;

import android.accounts.Account;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import t90.com.github.wifilogin.util.HttpUtil;
import t90.com.github.wifilogin.util.Util;
import tinyq.Query;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.Arrays;
import java.util.Calendar;

/**
 * User: VasiltsV
 * Date: 11/30/12
 * Time: 11:01 AM
 */
public class WifiSyncChecker extends BroadcastReceiver {

    private Context _context;

    public WifiSyncChecker(Context context) {
        _context = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        _context.registerReceiver(this,intentFilter);
    }

    private static final String TAG = "wifilogin.WifiSyncChecker";

    private boolean _useCache = false;


    private static final String DEFAULT_WALLED_GARDEN_URL = "http://clients3.google.com/generate_204";
    private static final int WALLED_GARDEN_SOCKET_TIMEOUT_MS = 10000;


    /**
     * Code from WifiWatchdogStateMachine ...
     *
     * DNS based detection techniques do not work at all hotspots. The one sure
     * way to check a walled garden is to see if a URL fetch on a known address
     * fetches the data we expect
     */
    private boolean isWalledGardenConnection() {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(DEFAULT_WALLED_GARDEN_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setConnectTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
            urlConnection.setReadTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
            urlConnection.setUseCaches(false);
            urlConnection.getInputStream();
            // We got a valid response, but not from the real google
            return urlConnection.getResponseCode() != 204;
        } catch (IOException e) {
            Log.e(TAG,e.toString());
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


    public void onSync(Account account, String authority, ContentProviderClient provider, SyncResult syncResult, Context context) {
        if(_useCache){
            return;
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null && wifiManager.getConnectionInfo() != null){
            try{
                if(isWalledGardenConnection()){
                    if("NGuest".equals(wifiManager.getConnectionInfo().getSSID())){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/storage/sdcard0/p.txt")));
                        String wifiUserName = reader.readLine();
                        String wifiPassword = reader.readLine();
                        HttpUtil httpUtil = new HttpUtil("https://securelogin.arubanetowrks.com/auth/index.html/u", null, null);
                        httpUtil.get("https://securelogin.arubanetowrks.com/auth/index.html/u", new Query<String>(new String[]{"user=" + wifiUserName, "password=" + wifiPassword, "Login=I+ACCEPT"}));


                    }

                }

//              Show login screen
//                if(isWalledGardenConnection()){
//                    new Intent()
//                }

                _useCache = true;

            }
            catch (Exception e){
                Log.e(TAG, Util.exceptionToString(e));
            }

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)){
            _useCache = false;
        }
    }
}
