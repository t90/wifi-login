package t90.com.github.wifilogin;

/**
 * User: VasiltsV
 * Date: 12/17/12
 * Time: 3:20 PM
 */
public interface IPropertiesEventHandler {
    void onSaveRequest(String url, String method);
    void onNewItemRequest();
    void onRemoveItemRequest();
}
