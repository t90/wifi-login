package t90.com.github.wifilogin.util;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by IntelliJ IDEA.
 * User: c115991
 * Date: 4/25/12
 * Time: 11:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class X509TrustManagerExt implements X509TrustManager {
//    private X509TrustManager standardTrustManager = null;

    public X509TrustManagerExt(KeyStore keyStore) throws NoSuchAlgorithmException, KeyStoreException {
//        TrustManagerFactory factory = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
//        factory.init( keyStore );
//        TrustManager[] trustmanagers = factory.getTrustManagers();
//        if ( trustmanagers.length == 0 )
//        {
//            throw new NoSuchAlgorithmException( "no trust manager found" );
//        }
//        this.standardTrustManager = (X509TrustManager) trustmanagers[0];
    }
    
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
