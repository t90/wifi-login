package t90.com.github.wifilogin.data;

import android.content.Context;
import android.content.res.Resources;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import t90.com.github.wifilogin.IPropertiesEventHandler;
import t90.com.github.wifilogin.R;
import t90.com.github.wifilogin.WifiWebLogin;

import java.util.List;

/**
 * User: VasiltsV
 * Date: 12/17/12
 * Time: 10:51 AM
 */
public class WifiWebPropertiesAdapter extends BaseAdapter {

    private List<Pair<String, String>> _propertiesList;
    private Context _context;
    private IPropertiesEventHandler _eventHandler;
    private int _methodIdx;

    public WifiWebPropertiesAdapter(List<Pair<String,String> > propertiesList, Context context) {

        _propertiesList = propertiesList;
        _context = context;
    }

    @Override
    public int getCount() {
        return _propertiesList.size() + 1;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    @Override
    public Object getItem(int i) {
        return _propertiesList.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i - 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0){
            View v = null;
            try{
                if(convertView != null && convertView.getClass().equals(LinearLayout.class)){
                    v = convertView;
                    return convertView;
                }
                else{
                    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View inflate = inflater.inflate(R.layout.web_login, parent, false);
                    v = inflate;
                    return inflate;
                }
            }
            finally {
                final Spinner methodSelector = (Spinner) v.findViewById(R.id.method_selector);
                if(_methodIdx >= 0){
                    methodSelector.setSelection(_methodIdx);
                }
                if(_eventHandler != null){

                    v.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String method = (String) methodSelector.getSelectedItem();
                            _eventHandler.onSaveRequest(
                                    "https://securelogin.arubanetowrks.com/auth/index.html/u",
                                    method
                                    );
                        }
                    });
                    v.findViewById(R.id.add_entry).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            _eventHandler.onNewItemRequest();
                        }
                    });
                    //todo add on remove item listener
                }
            }
        }

        if(convertView != null && convertView.getClass().equals(TextView.class)){
            ((TextView)convertView).setText(getItemText(position - 1));
            return convertView;
        }

        TextView textView = new TextView(_context);
        textView.setTextAppearance(_context,android.R.style.TextAppearance_Medium);
        textView.setLineSpacing(0f, 2f);
        textView.setText(getItemText(position - 1));
        return textView;
    }

    public String getItemText(int i) {
        Pair<String, String> property = _propertiesList.get(i);
        return String.format("%s:%s", property.first, property.second);
    }

    public void setEventHandler(IPropertiesEventHandler handler) {
        _eventHandler = handler;
    }

    public void setHttpMethodIdx(int methodIdx) {
        _methodIdx = methodIdx;
    }
}
