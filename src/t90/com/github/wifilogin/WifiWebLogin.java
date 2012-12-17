package t90.com.github.wifilogin;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.*;
import t90.com.github.wifilogin.SyncAdapter.ContentProviderImplementation;
import t90.com.github.wifilogin.data.WifiWebPropertiesAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: VasiltsV
 * Date: 12/17/12
 * Time: 9:57 AM
 */
public class WifiWebLogin extends ListActivity implements IPropertiesEventHandler {

    public static final String SSID = "SSID";
    private String _ssid;

    private List<View> _propertyViews = new ArrayList<View>();
    private LinearLayout _propertiesLayout;
    private WifiWebPropertiesAdapter _wifiWebPropertiesAdapter;
    private ArrayList<Pair<String, String>> _properties;
    private int _methodIdx = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String strSSID = (String) getIntent().getExtras().get(SSID);

        Cursor cursor = getContentResolver().query(ContentProviderImplementation.WIFI_POINT_URI, new String[]{"_id","METHOD","URL"}, "SSID=?", new String[]{strSSID}, null);
        if(!cursor.moveToFirst()){
            return;
        }


        int wifiPointId = cursor.getInt(0);
        String method = cursor.getString(1);
        String url = cursor.getString(2);

        if(method != null){
            String[] methodsArray = getResources().getStringArray(R.array.http_methods);

            for(int i = 0; i < methodsArray.length; i++){
                if(method.equals(methodsArray[i])){
                    _methodIdx = i;
                    break;
                }
            }

        }


        Cursor query = getContentResolver().query(ContentProviderImplementation.PROPERTIES_URI, new String[]{"Name", "Value"}, "SSID=?", new String[]{Integer.toString(wifiPointId)}, null);
        _properties = new ArrayList<Pair<String, String>>();
        while(query.moveToNext()){
            Pair<String, String> pair = new Pair<String, String>(query.getString(0), query.getString(1));
            _properties.add(pair);
        }
        updateAdapter();
    }

    public void updateAdapter(){
        _wifiWebPropertiesAdapter = new WifiWebPropertiesAdapter(_properties,this);
        _wifiWebPropertiesAdapter.setEventHandler(this);
        if(_methodIdx >= 0){
            _wifiWebPropertiesAdapter.setHttpMethodIdx(_methodIdx);
        }
        setListAdapter(_wifiWebPropertiesAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final Pair<String, String> item = (Pair<String, String>) _wifiWebPropertiesAdapter.getItem(position);
        final int idx = position - 1;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);
        if(item.second != null && !item.second.equals("")){
            ((EditText)dialog.findViewById(R.id.input_value)).setText(item.second);
        }
        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText) dialog.findViewById(R.id.input_value);
                _properties.set(idx, new Pair<String, String>(item.first, text.getText().toString()));
                updateAdapter();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onSaveRequest(String url, String method) {
        ContentResolver contentResolver = getContentResolver();

        String strSSID = (String) getIntent().getExtras().get(SSID);
        Cursor query = contentResolver.query(ContentProviderImplementation.WIFI_POINT_URI, new String[]{"_id"}, "SSID=?", new String[]{strSSID}, null);
        int id = -1;
        if(query.moveToFirst()){
            id = query.getInt(0);
        }

        if(id > 0){
            contentResolver.delete(ContentProviderImplementation.PROPERTIES_URI, "SSID=?", new String[]{Integer.toString(id)});
            contentResolver.delete(ContentProviderImplementation.WIFI_POINT_URI, "_id=?", new String[]{Integer.toString(id)});
        }

        ContentValues ssidValues = new ContentValues();
        ssidValues.put("SSID", strSSID);
        ssidValues.put("URL",url);
        ssidValues.put("METHOD", method);

        Uri wifiPointUri = contentResolver.insert(ContentProviderImplementation.WIFI_POINT_URI, ssidValues);

        String wifiPointId = wifiPointUri.getQueryParameter("_id");

        for(Pair<String,String> item : _properties){
            ContentValues contentValues = new ContentValues();
            contentValues.put("Name", item.first);
            contentValues.put("Value", item.second);
            contentValues.put("SSID", Integer.parseInt(wifiPointId));
            contentResolver.insert(ContentProviderImplementation.PROPERTIES_URI,contentValues);
        }

    }

    @Override
    public void onNewItemRequest() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);
        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText) dialog.findViewById(R.id.input_value);
                Pair<String, String> pair = new Pair<String, String>(text.getText().toString(), null);
                _properties.add(pair);
                updateAdapter();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public void onRemoveItemRequest() {

    }
}
