package t90.com.github.wifilogin.util;

import android.net.Uri;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import tinyq.Query;

import java.io.*;

public final class HttpUtil {
    private final DefaultHttpClient _httpClient;

    public static class HttpUtilException extends Exception{
        private final int _httpCode;
        public HttpUtilException(String detailMessage, int httpCode) {
            super(detailMessage);
            _httpCode = httpCode;
            if(httpCode == 200) throw new IndexOutOfBoundsException("Can not throw exception for 200 (Ok) code");
        }

        public int getHttpCode(){
            return _httpCode;
        }
    }

    public static class HttpUtilNullEntityException extends Exception{
        public HttpUtilNullEntityException(String detailMessage) {
            super(detailMessage);
        }
    }

    public HttpUtil(String baseUri, String userName, String password) throws IOException {
        try {
            HttpParams httpParams = new BasicHttpParams();
            int connection_Timeout = 20000;
            HttpConnectionParams.setConnectionTimeout(httpParams, connection_Timeout);
            HttpConnectionParams.setSoTimeout(httpParams, connection_Timeout);
    
            Uri uri = Uri.parse(baseUri);
            
            SchemeRegistry schemaReg = new SchemeRegistry();
            String schema = uri.getScheme();
            int port = uri.getPort();
            if(port <= 0){
                if("http".equals(schema)){
                    port = 80;
                }
                else if("https".equals(schema)){
                    port = 443;
                }
                else {
                    throw new IndexOutOfBoundsException("Invalid port and schema");
                }
            }
            schemaReg.register(new Scheme(schema, schema.equals("http") ? new PlainSocketFactory() : new SSLSocketFactoryExt(null), port));
            ThreadSafeClientConnManager connMngr = new ThreadSafeClientConnManager(httpParams, schemaReg);
    
            _httpClient = new DefaultHttpClient(connMngr, httpParams);
    
            if(userName == null && password == null){
                return;
            }
    
            _httpClient.getCredentialsProvider().setCredentials(
                    new AuthScope(null, -1),
                    new UsernamePasswordCredentials(userName,password));
        } catch (Exception e) {
            throw new IOException(e.getMessage() == null ? "" : e.getMessage(), e);
        }
    }

    public final String getUri(String base, Query<String> parameters){
        return String.format("%s?%s", base, parameters == null ? "" : Util.join(parameters, "&"));
    }

    public static class QueryAttributes{
        public QueryAttributes(String stringEntity, String contentType) throws UnsupportedEncodingException {
            RequestEntity = new StringEntity(stringEntity);
            ContentType = contentType;
            RequestEntity.setContentType(ContentType);
        }

        public QueryAttributes(AbstractHttpEntity requestEntity, String contentType) {
            this.RequestEntity = requestEntity;
            this.ContentType = contentType;
        }

        public QueryAttributes(JSONObject jsonObject) throws UnsupportedEncodingException {
            RequestEntity = new StringEntity(jsonObject.toString());
            ContentType = "application/json";
            RequestEntity.setContentType(ContentType);
        }

        public AbstractHttpEntity RequestEntity;
        private String ContentType;
    }

    private final String processHttpResponse(HttpResponse response) throws IOException, HttpUtilException, HttpUtilNullEntityException {
        HttpEntity responseEntity = throwExceptionsOnErrors(response);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
        String result = Util.readAllLines(bufferedReader);
        return result;

    }


    private HttpEntity throwExceptionsOnErrors(HttpResponse response) throws HttpUtilNullEntityException, HttpUtilException, IOException {
        final StatusLine statusLine = response.getStatusLine();
        final int code = statusLine.getStatusCode();
        final HttpEntity responseEntity = response.getEntity();
        if(responseEntity == null){
            throw new HttpUtilNullEntityException("null response entity");
        }
        if(code != 200){
            throw new HttpUtilException(Util.readAllLines(new BufferedReader(new InputStreamReader(responseEntity.getContent()))), code);
        }
        return responseEntity;
    }

    public final String post(String uri, Query<String> parameters, QueryAttributes attributes) throws HttpUtilException, IOException, HttpUtilNullEntityException {
        HttpPost httpPost = new HttpPost(getUri(uri,parameters));
        if(attributes != null)
            httpPost.setEntity(attributes.RequestEntity);
        HttpResponse response = _httpClient.execute(httpPost);
        return processHttpResponse(response);
    }

    public final String get(String uri, Query<String> parameters) throws HttpUtilException, IOException, HttpUtilNullEntityException {
        HttpGet httpGet = new HttpGet(getUri(uri, parameters));
        HttpResponse response = _httpClient.execute(httpGet);
        return processHttpResponse(response);
    }

    public final String put(String uri, Query<String> parameters, QueryAttributes attributes) throws IOException, HttpUtilException, HttpUtilNullEntityException {
        HttpPut httpPut = new HttpPut(getUri(uri, parameters));
        if(attributes != null)
            httpPut.setEntity(attributes.RequestEntity);
        HttpResponse response = _httpClient.execute(httpPut);
        return processHttpResponse(response);
    }

    public final InputStream download(String uri, Query<String> parameters) throws IOException, HttpUtilException, HttpUtilNullEntityException {
        HttpGet httpGet = new HttpGet(getUri(uri, parameters));
        HttpResponse response = _httpClient.execute(httpGet);
        final HttpEntity responseEntity = throwExceptionsOnErrors(response);
        return responseEntity.getContent();
    }



//    public final int upload(String uri, Query<String> parameters, File file) throws IOException, HttpUtilNullEntityException, HttpUtilException {
//        HttpPost httpPost = new HttpPost(getUri(uri, parameters));
//        FileBody fileBody = new FileBody(file);
//        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//        entity.addPart(file.getName(), fileBody);
//        httpPost.setEntity(entity);
//        HttpResponse response = _httpClient.execute(httpPost);
//        HttpEntity httpEntity = throwExceptionsOnErrors(response);
//        String responseString = Util.readAllLines(new BufferedReader(new InputStreamReader(httpEntity.getContent())));
//        return Integer.parseInt(responseString);
//    }

}
