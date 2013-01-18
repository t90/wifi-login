package t90.com.github.wifilogin;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import t90.com.github.wifilogin.fragments.HtmlViewFragment;
import t90.com.github.wifilogin.fragments.PropertiesViewFragment;
import tinyq.Query;

import java.util.HashMap;
import java.util.List;

/**
 * User: VasiltsV
 * Date: 1/16/13
 * Time: 2:25 PM
 */
public class NetworkInfoScreen extends android.support.v4.app.FragmentActivity implements TabHost.OnTabChangeListener {
    private TabHost _tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_info);
        _tabHost = (TabHost) findViewById(android.R.id.tabhost);
        _tabHost.setup();
        _tabHost.addTab(_tabHost.newTabSpec("properties").setIndicator("properties").setContent(android.R.id.tabcontent));
        _tabHost.addTab(_tabHost.newTabSpec("html").setIndicator("HTML").setContent(android.R.id.tabcontent));
        _tabHost.setOnTabChangedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().
                replace(R.id.tab1, new PropertiesViewFragment(),"properties").
                replace(R.id.tab2, new HtmlViewFragment(),"html").
                commit();

    }

    private static HashMap<String, Integer> _tabNameToIdMap = new HashMap<String, Integer>();
    static {
        _tabNameToIdMap.put("properties",R.id.tab1);
        _tabNameToIdMap.put("html",R.id.tab2);
    }

    @Override
    public void onTabChanged(String tabId) {
        final String tabIdInt = tabId;
        Query<String> nonSelected = (new Query<String>(_tabNameToIdMap.keySet())).where(new Query.F<String, Boolean>() {
            @Override
            public Boolean run(String in) {
                return !in.equals(tabIdInt);
            }
        });

        for(String id : nonSelected){
            findViewById(_tabNameToIdMap.get(id)).setVisibility(View.INVISIBLE);
        }

        findViewById(_tabNameToIdMap.get(tabId)).setVisibility(View.VISIBLE);

    }

}
