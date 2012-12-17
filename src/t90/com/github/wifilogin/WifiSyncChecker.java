package t90.com.github.wifilogin;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.graphics.Path;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import org.apache.http.client.ClientProtocolException;
import t90.com.github.wifilogin.SyncAdapter.ContentProviderImplementation;
import t90.com.github.wifilogin.util.HttpUtil;
import t90.com.github.wifilogin.util.Util;
import tinyq.Query;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

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


    private static final String CACHE_FLAG = "use_cache.flg";

    private boolean useCache(){
        File cacheDir = _context.getCacheDir();
        return (new File(String.format("%s/%s", cacheDir.getPath(), CACHE_FLAG))).exists();
    }

    private void setUseCache(boolean v){
        File cacheDir = _context.getCacheDir();
        File cacheFile = new File(String.format("%s/%s", cacheDir.getPath(), CACHE_FLAG));
        if(v && !cacheFile.exists()){
            try {
                cacheFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG,Util.exceptionToString(e));
            }
        }
        else if(!v && cacheFile.exists()){
            cacheFile.delete();
        }
    }

    public void onSync(Account account, String authority, ContentProviderClient provider, SyncResult syncResult, Context context) {
        if(useCache()){
            return;
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null && wifiManager.getConnectionInfo() != null){
            try{
                if(isWalledGardenConnection()){

                    String activeSSID = wifiManager.getConnectionInfo().getSSID();
                    Cursor cursor = _context.getContentResolver().query(ContentProviderImplementation.WIFI_POINT_URI, new String[]{"_id","URL","METHOD"}, "SSID=?", new String[]{activeSSID}, null);
                    if(!cursor.moveToFirst()){
                        throw new ClientProtocolException("Credentials not found");
                    }

                    int wifiId = cursor.getInt(0);
                    String url = cursor.getString(1);
                    String method = cursor.getString(2);
                    cursor = _context.getContentResolver().query(ContentProviderImplementation.PROPERTIES_URI, new String[]{"Name", "Value"}, "SSID=?", new String[]{Integer.toString(wifiId)}, null);

                    String[] parameters = new String[cursor.getCount()];

                    int i = 0;

                    while(cursor.moveToNext()){
                        parameters[i] = String.format("%s=%s", cursor.getString(0), URLEncoder.encode(cursor.getString(1),"UTF-8"));
                        i++;
                    }

                    HttpUtil httpUtil = new HttpUtil(url, null, null);
                    if(method.equals("GET")){
                        httpUtil.get(url, new Query<String>(parameters));
                    }
                    else if(method.equals("POST")){
                        httpUtil.post(url, new Query<String>(parameters), null);
                    }


                }

                setUseCache(true);

            }
            catch(ClientProtocolException cpe){
                Intent intent = new Intent(_context, WifiWebLogin.class);
                intent.putExtra(WifiWebLogin.SSID,wifiManager.getConnectionInfo().getSSID());
                _context.startActivity(intent);
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
            setUseCache(false);
        }
    }
}
