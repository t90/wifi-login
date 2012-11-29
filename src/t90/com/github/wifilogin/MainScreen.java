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
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null){
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if(connectionInfo != null){
                String macAddress = connectionInfo.getMacAddress();
//                String urlStr = "https://securelogin.arubanetworks.com/cgi-bin/login?cmd=login&mac=$$MAC$$&ip=$$IP$$&essid=$$ESSID$$&url=http%3A%2Fwww%2Egoogle%2Ecom%2F";
//
//                urlStr = urlStr.replace("$$MAC$$",connectionInfo.getMacAddress());
//                int ipAddress = connectionInfo.getIpAddress();
//                urlStr = urlStr.replace("$$IP$$",String.format("%d.%d.%d.%d",
//                        (ipAddress & 0xff),
//                        (ipAddress >> 8 & 0xff),
//                        (ipAddress >> 16 & 0xff),
//                        (ipAddress >> 24 & 0xff)));
//                urlStr = urlStr.replace("$$ESSID$$",connectionInfo.getSSID());

                BufferedReader reader = null;
                OutputStreamWriter writer = null;
                try{

                    reader = new BufferedReader(new InputStreamReader(new FileInputStream("/storage/sdcard0/p.txt")));
                    String userName = reader.readLine();
                    String password = reader.readLine();

//                    File nguest = File.createTempFile("nguest", ".html");
                    File nguest = new File("/storage/sdcard0/login.html");

                    writer = new OutputStreamWriter(new FileOutputStream(nguest));
                    String htmlStr = "<html><head><meta id='meta' name='viewport' content='width=320px; initial-scale=1.0' /></head><body>" +
                            "<form action='https://securelogin.arubanetowrks.com/auth/index.html/u'>" +
                            "<input type='text' name='user' size='25' value='$$USER$$' /><br />" +
                            "<input type='text' name='password' size='25' value='$$PASSWORD$$' /><br />" +
                            "<input type='submit' name='Login' value='I ACCEPT' class='button' />" +
                            "</form>" +
                            "</body></html>";

                    htmlStr = htmlStr.replace("$$USER$$",userName).replace("$$PASSWORD$$",password);


                    writer.write(htmlStr);

                    String fileName = "file://" + nguest.getAbsolutePath();
                    Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(fileName));
                    browser.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    startActivity(browser);


                }
                catch (Exception e){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainScreen.this);
                    alertDialog.setTitle("Can not read password or create temp html: " + e.toString());
                    alertDialog.show();
                }
                finally {
                    if(reader != null){
                        try{
                            reader.close();
                        }
                        catch (Exception e){

                        }
                    }

                    if(writer != null){
                        try{
                            writer.close();
                        }
                        catch (Exception e){

                        }
                    }
                }



            }
        }

    }
}
