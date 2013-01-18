package t90.com.github.wifilogin.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import t90.com.github.wifilogin.R;

/**
 * User: VasiltsV
 * Date: 1/16/13
 * Time: 2:36 PM
 */
public class HtmlViewFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.html_view, container, false);
    }
}
