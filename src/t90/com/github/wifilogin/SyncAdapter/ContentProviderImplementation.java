package t90.com.github.wifilogin.SyncAdapter;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;

/**
 * User: VasiltsV
 * Date: 11/29/12
 * Time: 2:41 PM
 */
public class ContentProviderImplementation extends ContentProvider {

    public static final String PROVIDER_NAME = "t90.com.github.wifilogin";
    public static final Uri CONTENT_URI = Uri.parse(String.format("content://%s", PROVIDER_NAME));
    public static final Uri PROPERTIES_URI = Uri.parse(String.format("content://%s/Properties", PROVIDER_NAME));
    public static final Uri WIFI_POINT_URI = Uri.parse(String.format("content://%s/WifiPoint", PROVIDER_NAME));

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        try{
            getDb().beginTransaction();
            ContentProviderResult[] contentProviderResults = super.applyBatch(operations);
            getDb().setTransactionSuccessful();
            return contentProviderResults;
        }
        finally {
            getDb().endTransaction();
        }
    }

    private static ContentProvider _instance = null;
    private static SQLiteDatabase _db = null;
    private static MyDbHelper _dbHelper = null;

    @Override
    public boolean onCreate() {
        _instance = this;
        _dbHelper = new MyDbHelper(getContext());
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (_db != null) {
            _db.close();
            _db = null;
        }
    }

    private synchronized SQLiteDatabase getDb() {
        if (_db == null) {
            _db = getDbHelper().getWritableDatabase();
        }
        return _db;
    }

    protected SQLiteOpenHelper getDbHelper() {
        return _dbHelper;
    }


    @Override
    public Cursor query(Uri uri, String[] columns, String where, String[] whereArgs, String sortOrder) {
        where = ensureWhere(uri,where);
        return getDb().query(getTableName(uri),columns,where,whereArgs,null,null,sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = getDb().insert(getTableName(uri), null, contentValues);
        return Uri.parse(String.format("%s?_id=%d", uri.toString(), id));
    }


    private static String getTableName(Uri uri){
        return uri.getLastPathSegment();
    }

    @Override
    public int delete(Uri uri, String where, String[] strings) {
        where = ensureWhere(uri, where);
        return getDb().delete(getTableName(uri),where,strings);
    }

    private String ensureWhere(Uri uri, String where) {
        String strUri = uri.toString();
        if(strUri.contains("?")){
            if(where == null){
                where = strUri.split("\\?")[1];
            }
            else{
                where += " AND " + strUri.split("\\?")[1];
            }

        }
        return where;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String where, String[] strings) {
        where = ensureWhere(uri,where);
        return getDb().update(getTableName(uri),contentValues,where,strings);
    }

    private class MyDbHelper extends SQLiteOpenHelper {
        private static final int version = 2;

        public MyDbHelper(Context context) {
            super(context, "wifilogin", null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE Properties (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Value TEXT, SSID INTEGER);");
            sqLiteDatabase.execSQL("CREATE TABLE WifiPoint (_id INTEGER PRIMARY KEY AUTOINCREMENT, SSID TEXT, URL TEXT, METHOD TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Properties");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS WifiPoint");
            onCreate(sqLiteDatabase);
        }
    }
}
