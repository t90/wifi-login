package t90.com.github.wifilogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.*;

public class MainScreen extends Activity implements AdapterView.OnItemClickListener {
    private ListView _wifiSelector;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _wifiSelector = (ListView) findViewById(R.id.wifi_selector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.wifi_points, android.R.layout.simple_list_item_1);
        _wifiSelector.setAdapter(adapter);
        _wifiSelector.setOnItemClickListener(this);

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null){
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if(connectionInfo != null){
                String ssid = connectionInfo.getSSID();
                CharSequence[] textArray = getResources().getTextArray(R.array.wifi_points);
                if(textArray != null){
                    int idx = 0;
                    boolean notFound = true;
                    for(CharSequence chars : textArray){
                        if(!chars.toString().equals(ssid)){
                            idx++;
                        }
                        else{
                            notFound = false;
                            break;
                        }
                    }
                    if(!notFound){
                        _wifiSelector.setSelection(idx);
                    }
                }
            }
        }


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String networkName = (String) adapterView.getItemAtPosition(i);
        Intent intent = new Intent(this, WifiWebLogin.class);
        intent.putExtra(WifiWebLogin.SSID,networkName);
        startActivity(intent);
    }
}
