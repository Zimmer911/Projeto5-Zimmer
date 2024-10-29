package br.com.aula.text;

import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class CustomTrustManager {
    public static OkHttpClient getOkHttpClient() {
        try {
            // Carregue o certificado do servidor
            X509Certificate certificate = loadCertificate();

            // Crie um truststore com o certificado do servidor
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            trustStore.setCertificateEntry("server-cert", certificate);

            // Crie um trust manager com o truststore
            TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            // Crie um SSLContext com o trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, null);

            // Crie um OkHttpClient com o SSLContext
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManager)
                    .build();

            return okHttpClient;
        } catch (Exception e) {
            // Trate a exceção aqui
            return null;
        }
    }

    private static X509Certificate loadCertificate() throws CertificateException {
        try {
            // Carregue o certificado do servidor de um arquivo
            InputStream inputStream = CadastroAtleta.class.getResourceAsStream("/server_cert.crt");
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(inputStream);
        } catch (CertificateException e) {
            // Trate a exceção aqui
            return null;
        }
    }
}