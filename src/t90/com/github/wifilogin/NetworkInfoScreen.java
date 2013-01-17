package t90.com.github.wifilogin;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.TabHost;

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


    }

    @Override
    public void onTabChanged(String tabId) {
        updateTab(tabId);
        tabId.toString();
    }

    private void updateTab(String tabId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.findFragmentById(R.id.)
    }
}
