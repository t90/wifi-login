package t90.com.github.wifilogin.util;

import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;

/**
 * Created by IntelliJ IDEA.
 * User: c115991
 * Date: 4/25/12
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class SSLSocketFactoryExt extends SSLSocketFactory {
    private SSLContext _sslContext;

    public SSLSocketFactoryExt(String algorithm, KeyStore keystore, String keystorePassword, KeyStore truststore, SecureRandom random, HostNameResolver nameResolver) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(algorithm, keystore, keystorePassword, truststore, random, nameResolver);
    }

    public SSLSocketFactoryExt(KeyStore keystore, String keystorePassword, KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(keystore, keystorePassword, truststore);
    }

    public SSLSocketFactoryExt(KeyStore keystore, String keystorePassword) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(keystore, keystorePassword);
    }

    public SSLSocketFactoryExt(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
    }

    @Override
    public Socket createSocket() throws IOException {
        return getSslContext().getSocketFactory().createSocket();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return getSslContext().getSocketFactory().createSocket(socket, host, port, autoClose);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private SSLContext getSslContext() throws IOException {
        try {
            if (_sslContext == null) {
                _sslContext = SSLContext.getInstance("TLS");
                _sslContext.init(null, new TrustManager[] {new X509TrustManagerExt(null)}, null);
            }
            return _sslContext;
        } catch (Exception ex) {
            throw new IOException(ex.getMessage() == null ? "" : ex.getMessage(), ex);
        }
    }
}
