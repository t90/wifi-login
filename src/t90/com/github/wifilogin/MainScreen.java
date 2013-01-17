package t90.com.github.wifilogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import t90.com.github.wifilogin.util.Util;

import java.io.*;

public class MainScreen extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView _wifiSelector;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, NetworkInfoScreen.class);
        startActivity(intent);


        setContentView(R.layout.main);
        _wifiSelector = (ListView) findViewById(R.id.wifi_selector);
        ((Button)findViewById(R.id.new_network)).setOnClickListener(this);
        bind();
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

    public void bind() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, Util.savedWifiNetworkNames(this));
        _wifiSelector.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String networkName = (String) adapterView.getItemAtPosition(i);
        Intent intent = new Intent(this, WifiWebLogin.class);
        intent.putExtra(WifiWebLogin.SSID,networkName);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        // on new network

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo;
        if(wifiManager != null && (connectionInfo = wifiManager.getConnectionInfo()) != null){
            final String ssid = connectionInfo.getSSID();

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.new_network_dialog);
            dialog.setTitle(R.string.new_network);
            ((EditText)dialog.findViewById(R.id.network_name)).setText(ssid);
            dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText networkName = (EditText) dialog.findViewById(R.id.network_name);
                    Util.createNewNetwork(MainScreen.this,networkName.getText().toString());
                    MainScreen.this.bind();
                    dialog.dismiss();
                }
            });

            dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }


    }
}
