package t90.com.github.wifilogin.SyncAdapter;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * User: VasiltsV
 * Date: 11/29/12
 * Time: 2:37 PM
 */
public class SyncCompleteObserver extends ContentObserver {
    private Handler _handler;

    public SyncCompleteObserver(Handler handler) {
        super(handler);
        _handler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        _handler.sendEmptyMessage(0);
    }
}
