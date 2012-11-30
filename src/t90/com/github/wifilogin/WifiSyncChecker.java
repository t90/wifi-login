package t90.com.github.wifilogin;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import t90.com.github.wifilogin.util.HttpUtil;
import t90.com.github.wifilogin.util.Util;
import tinyq.Query;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * User: VasiltsV
 * Date: 11/30/12
 * Time: 11:01 AM
 */
public class WifiSyncChecker {

    private static final String TAG = "wifilogin.WifiSyncChecker";

    public void onSync(Account account, String authority, ContentProviderClient provider, SyncResult syncResult, Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo;
        if(wifiManager != null && (connectionInfo = wifiManager.getConnectionInfo()) != null){
            String ssid = connectionInfo.getSSID();
            if(ssid != null && ssid.toLowerCase().equals("nguest")){
                BufferedReader reader = null;
                try{
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream("/storage/sdcard0/p.txt")));
                    String wifiUserName = reader.readLine();
                    String wifiPassword = reader.readLine();
                    HttpUtil httpUtil = new HttpUtil("https://securelogin.arubanetowrks.com/auth/index.html/u", null, null);
                    String result = httpUtil.get("https://securelogin.arubanetowrks.com/auth/index.html/u", new Query<String>(new String[]{"user=" + wifiUserName, "password=" + wifiPassword, "Login=I+ACCEPT"}));
                }
                catch (Exception e){
                    Log.e(TAG, Util.exceptionToString(e));
                }
            }
        }
    }
}
