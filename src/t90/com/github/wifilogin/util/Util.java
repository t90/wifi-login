package t90.com.github.wifilogin.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import t90.com.github.wifilogin.SyncAdapter.ContentProviderImplementation;
import tinyq.BufferedReaderIterator;
import tinyq.CursorIterator;
import tinyq.Query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {
	/**
	 * Join string array to a string using delimeter. Example
	 * str[0] = "test"
	 * str[1] = "me"
	 *
	 * join(str,";");
	 *
	 * result
	 *
	 * "test;me"
	 */
	public static String join(final Collection<?> s,final String delimiter) {
		if(s == null) return "";
	     StringBuilder builder = new StringBuilder();
	     Iterator<?> iter = s.iterator();
	     while (iter.hasNext()) {
	    	 Object val = iter.next();
	    	 if (val != null)
	    		 builder.append(val);
	         if (!iter.hasNext()) {
	           break;
	         }
	         builder.append(delimiter);
	     }
	     return builder.toString();
	}

    public static String join(final Query<String> strings, final String delimiter){
        String r = strings.aggregate(new StringBuilder(), new Query.Accum<String, StringBuilder>() {
            public StringBuilder run(String item, StringBuilder accumulator) {
                if (item != null) {
                    accumulator.append(item);
                    accumulator.append(delimiter);
                }
                return accumulator;
            }
        }).toString();
        if(r.length() == 0) return r;
        return r.substring(0, r.length() - delimiter.length());
    }

    public static String readToEnd(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String str = null;
        StringBuilder sb = new StringBuilder();
        while((str = reader.readLine()) != null){
            sb.append(str);
            sb.append("\n");
        }
        return sb.toString();
    }

    final static Query.F<StackTraceElement, String> _exceptionToStringStackTraceElemToString = new Query.F<StackTraceElement, String>() {
        public String run(StackTraceElement in) {
            return in.toString();
        }
    };
    final static Query.Accum<String, StringBuilder> _joinAllStackTraceDescriptionsFoldFunc = new Query.Accum<String, StringBuilder>() {
        public StringBuilder run(String item, StringBuilder accumulator) {
            if (item != null) {
                accumulator.append(item);
                accumulator.append("\n");
            }
            return accumulator;
        }
    };

    public static String exceptionToString(Exception e){
        if(e == null) return "";
        String message = e.getMessage();
        StringBuilder sb = new StringBuilder();
        if(message != null){
            sb.append(message);
            sb.append("\n");
        }


        StackTraceElement[] stackTrace = e.getStackTrace();
        if(stackTrace != null){
            (new Query<StackTraceElement>(stackTrace)).
                    select(_exceptionToStringStackTraceElemToString).
                    aggregate(sb,_joinAllStackTraceDescriptionsFoldFunc);
        }
        return sb.toString();
    }


    public static interface PureFunc<IN,OUT>{
        OUT run(IN param);
    }

    public static <T> List<T> where(List<T> source, PureFunc<T, Boolean> selector){
        ArrayList<T> retVal = new ArrayList<T>();
        for(T item : source){
            if(selector.run(item)){
                retVal.add(item);
            }
        }
        return retVal;
    }

    public static <T,T1> List<T1> select(List<T> source, PureFunc<T, T1> mapper){
        ArrayList<T1> retVal = new ArrayList<T1>();
        for(T item : source){
            retVal.add(mapper.run(item));
        }
        return retVal;
    }


    public static void confirmation(Activity a, String message,final Runnable onOk, final Runnable onCancel){
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setMessage(message)
        .setCancelable(true)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if(onOk != null)
                    onOk.run();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if(onCancel != null)
                    onCancel.run();
            }
        });
        builder.show();
    }

    public static void alert(Activity a, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setMessage(message)
       .setCancelable(true)
       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
           }
       });
        builder.show();
    }

    public final static String readAllLines(BufferedReader bufferedReader) {
        return Util.join(new Query<String>(new BufferedReaderIterator(bufferedReader)), "\r\n");
    }



	public final static SimpleDateFormat XmlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss-05:00");
    public final static SimpleDateFormat XmlDateFormatZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	public final static SimpleDateFormat HumanReadableDateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
	
	public static String getHumanFormatedDateString(String unformatedDateString){
		if(unformatedDateString == null || "".equals(unformatedDateString)) return "1900-01-01 01:01 AM";
		final String dateString = Html.fromHtml(unformatedDateString).toString().trim();
		Date date;
		try {
			date = XmlDateFormat.parse(dateString);
		} catch (ParseException e) {
			try{
				return HumanReadableDateFormat.format(HumanReadableDateFormat.parse(dateString));
			}
			catch(ParseException ex){
				return "1900-01-01 01:01 AM";
			}
			
		}
		return HumanReadableDateFormat.format(date);
	}
	
	public static String getXmlFormatedDate(String unformatedDateString){
		if(unformatedDateString == null || "".equals(unformatedDateString)) return "1900-01-01T01:01-05:00";
		final String dateString = Html.fromHtml(unformatedDateString).toString().trim();
		Date date;
		try {
			date = HumanReadableDateFormat.parse(dateString);
		} catch (ParseException e) {
			try{
				
				return XmlDateFormat.format(XmlDateFormat.parse(dateString));
			}
			catch(ParseException ex){
				return "1900-01-01T01:01-05:00";
			}
		}
		return XmlDateFormat.format(date);
	}
	
	
	public static String getResourceString(Context context, int id){
		return context.getResources().getString(id);
	}

//	public static <T1,T2> T2[] map(T1[] inArray, Class outArrayType, Func<T1,T2> mapFunction) throws Exception{
//		T2[] retVal = (T2[])Array.newInstance(outArrayType, inArray.length);
//		for(int i = 0; i < inArray.length; ++i){
//			retVal[i] = mapFunction.run(inArray[i]);
//		}
//		return retVal;
//	}
	
	public static String stripHtml(String htmlString){
		if(htmlString == null) return null;
		Spanned retVal = Html.fromHtml(htmlString);
		if(retVal == null) return null;
		return retVal.toString().trim();
	}
	
	public static void setControlText(TextView control,String text) {
		if(control == null) throw new NullPointerException("Control is null");
		if(text == null) text = "";
		control.setText(Html.fromHtml(text));
	}
	
	public static void setControlText(EditText control, String text) throws NullPointerException{
		if(control == null) throw new NullPointerException("Control is null");
		if(text == null) text = "";
		control.setText(Html.fromHtml(text));
	}
	
	public static String getControlText(EditText control) throws NullPointerException{
		if(control == null) throw new NullPointerException("Control is null");
		final Editable text = control.getText();
		if(text == null) return null;
		return Html.toHtml(text);
	}
	
	public static void notifyChange(Context context, String uriStr){
		Uri uri = Uri.parse(uriStr);
		context.getContentResolver().notifyChange(uri,null);
	}

    public static class Pair<T1,T2>{
        public final T1 First;
        public final T2 Second;

        public Pair(T1 first, T2 second){

            First = first;
            Second = second;
        }

    }

    public static final List<String> savedWifiNetworkNames(Context c){
        Cursor cursor = c.getContentResolver().query(ContentProviderImplementation.WIFI_POINT_URI, new String[]{"SSID"}, null, null, null);
        CursorIterator<String> iterator = new CursorIterator<String>(new Query.F<Cursor, String>() {
            @Override
            public String run(Cursor in) {
                return in.getString(0);
            }
        }, cursor);
        return (new Query<String>(iterator)).toList();
    }

    public static final void createNewNetwork(Context c, String ssid){
        ContentValues values = new ContentValues();
        values.put("SSID",ssid);
        c.getContentResolver().insert(ContentProviderImplementation.WIFI_POINT_URI,values);
    }



    public static class ArrayCollection<T> implements Collection<T>{
		private class ArrayIterator<T> implements Iterator<T>{
			
			private final ArrayCollection<T> _collection;
			private int _index;

			public ArrayIterator(ArrayCollection<T> collection){
				this._collection = collection;
				_index = 0;
			}

			public boolean hasNext() {
				return _collection.size() > _index; 
			}

			public T next() {
				return _collection._array[_index++];
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		}
		
		private T[] _array;

		public ArrayCollection(T[] array){
			_array = array;
		}

		public boolean add(T arg0) {
			throw new UnsupportedOperationException();
		}

		public boolean addAll(Collection<? extends T> arg0) {
			throw new UnsupportedOperationException();
		}

		public void clear() {
			throw new UnsupportedOperationException();
		}

		public boolean contains(Object arg0) {
			throw new UnsupportedOperationException();
			
		}

		public boolean containsAll(Collection<?> arg0) {
			throw new UnsupportedOperationException();
		}

		public boolean isEmpty() {
			return _array == null || _array.length == 0;
		}

		public Iterator<T> iterator() {
			return new ArrayIterator(this);
		}

		public boolean remove(Object arg0) {
			throw new UnsupportedOperationException();
		}

		public boolean removeAll(Collection<?> arg0) {
			throw new UnsupportedOperationException();
		}

		public boolean retainAll(Collection<?> arg0) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			return _array.length;
		}

		public Object[] toArray() {
			return _array;
		}

		public <T> T[] toArray(T[] arg0) {
			throw new UnsupportedOperationException();
		}
		
	}


}
