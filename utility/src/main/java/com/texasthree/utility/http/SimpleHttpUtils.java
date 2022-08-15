package com.texasthree.utility.http;

import com.texasthree.utility.utlis.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * http
 */
public class SimpleHttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(SimpleHttpUtils.class);

    /**
     * 默认字符编码
     */
    public static final String DEFAULT_CHARSET = "utf-8";

    public static final String METHOD_POST = "POST";

    public static final String METHOD_GET = "GET";

    public static final String HTTP_ERROR_MESSAGE = "http_error_message";

    /**
     * 默认超时设置(20秒)
     */
    public static final int DEFAULT_READ_TIMEOUT = 20000;

    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;


    public static final String HTTP_PREFIX = "http://";

    public static final String HTTPS_PREFIX = "https://";

    //最多只读取5000字节
    public static final int MAX_FETCHSIZE = 5000;

    private static TrustManager[] trustAnyManagers = new TrustManager[]{new TrustAnyTrustManager()};

    static {
        System.setProperty("sun.net.inetaddr.ttl", "3600");
    }

    public static String httpPost(String url, Map params) {
        return httpRequest(url, params, METHOD_POST, DEFAULT_CHARSET, null);
    }

    public static String httpGet(String url, Map params) {
        return httpRequest(url, params, METHOD_GET, DEFAULT_CHARSET, null);
    }

    /**
     * 以建立HttpURLConnection方式发送请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param method  请求方式
     * @param charSet
     * @return 通讯失败返回null, 否则返回服务端输出
     */
    public static String httpRequest(String url,
                                     Map<String, Object> params,
                                     String method,
                                     String charSet,
                                     Map<String, String> headers) {
        SimpleHttpParam param = new SimpleHttpParam(url);
        if (null != params) {
            param.setParameters(params);
        }
        if (null != headers) {
            param.setHeaders(headers);
        }
        param.setCharSet(charSet);
        param.setMethod(method);
        SimpleHttpResult result = httpRequest(param);
        if (result == null || !result.isSuccess()) {
            return null;
        } else {
            return result.getContent();
        }
    }

    public static SimpleHttpResult httpRequest(SimpleHttpParam httpParam) {
        var url = httpParam.getUrl();
        if (url == null || url.trim().length() == 0) {
            throw new IllegalArgumentException("invalid url : " + url);
        }

        var baseUrl = url.trim();
        if (!baseUrl.toLowerCase().startsWith(HTTPS_PREFIX) && !baseUrl.toLowerCase().startsWith(HTTP_PREFIX)) {
            baseUrl = HTTP_PREFIX + baseUrl;
        }

        var charSet = httpParam.getCharSet();
        Charset.forName(charSet);
        var index = baseUrl.indexOf("?");
        if (index > 0) {
            baseUrl = urlEncode(baseUrl, charSet);
        } else if (index == 0) {
            throw new IllegalArgumentException("invalid url : " + url);
        }

        var method = httpParam.getMethod() != null ? httpParam.getMethod().toUpperCase() : null;
        if (!METHOD_POST.equals(method) && !METHOD_GET.equals(method)) {
            throw new IllegalArgumentException("invalid http method : " + method);
        }

        var queryString = StringUtils.toQueryString(httpParam.getParameters(), charSet);
        String targetUrl;
        if (method.equals(METHOD_POST)) {
            targetUrl = baseUrl;
        } else {
            if (index > 0) {
                targetUrl = baseUrl + "&" + queryString;
            } else {
                targetUrl = baseUrl + "?" + queryString;
            }
        }

        HttpURLConnection urlConn = null;
        try {
            var destURL = new URL(targetUrl);
            urlConn = (HttpURLConnection) destURL.openConnection();

            setSSLSocketFactory(urlConn, httpParam.isSslVerify(), httpParam.isHostnameVerify(), httpParam.getTrustKeyStore(), httpParam.getClientKeyStore());

            var hasContentType = false;
            var hasUserAgent = false;
            var headers = httpParam.getHeaders();
            if (headers == null) {
                headers = new HashMap<>();
            }
            for (var key : headers.keySet()) {
                if ("Content-Type".equalsIgnoreCase(key)) {
                    hasContentType = true;
                }
                if ("merchant-agent".equalsIgnoreCase(key)) {
                    hasUserAgent = true;
                }
            }
            if (!hasContentType) {
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=" + charSet);
            }
            if (!hasUserAgent) {
                headers.put("user-agent", "PlatSystem");
            }

            for (var entry : headers.entrySet()) {
                for (var v : StringUtils.makeStringList(entry.getValue())) {
                    urlConn.addRequestProperty(entry.getKey(), v);
                }
            }
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setAllowUserInteraction(false);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod(method);
            urlConn.setConnectTimeout(httpParam.getConnectTimeout());
            urlConn.setReadTimeout(httpParam.getReadTimeout());
            if (!httpParam.isFollowRedirects()) {
                urlConn.setInstanceFollowRedirects(false);
            }

            if (method.equals(METHOD_POST)) {
                var postData = queryString.length() == 0 ? httpParam.getPostData() : queryString;
                if (postData != null && postData.trim().length() > 0) {
                    var os = urlConn.getOutputStream();
                    var osw = new OutputStreamWriter(os, charSet);
                    osw.write(postData);
                    osw.flush();
                    osw.close();
                }
            }

            int responseCode = urlConn.getResponseCode();
            var responseHeaders = urlConn.getHeaderFields();
            var contentType = urlConn.getContentType();

            var result = new SimpleHttpResult(responseCode);
            result.setHeaders(responseHeaders);
            result.setContentType(contentType);

            if (responseCode != 200 && httpParam.isIgnoreContentIfUnsuccess()) {
                return result;
            }

            var inputStream = urlConn.getInputStream();
            if (httpParam.isGzip()) {
                inputStream = new GZIPInputStream(inputStream);
            }
            var temp = new byte[1024];
            var byteArrayOutputStream = new ByteArrayOutputStream();
            var readBytes = inputStream.read(temp);
            while (readBytes > 0) {
                byteArrayOutputStream.write(temp, 0, readBytes);
                readBytes = inputStream.read(temp);
            }
            var resultString = new String(byteArrayOutputStream.toByteArray(), charSet);
            byteArrayOutputStream.close();
            result.setContent(resultString);
            return result;
        } catch (Exception e) {
            logger.error("connection error : " + e.getMessage());
            return new SimpleHttpResult(e);
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
    }

    /**
     * @param url
     * @param charSet
     * @return
     */
    private static String urlEncode(String url, String charSet) {
        if (url == null || url.trim().length() == 0) {
            return url;
        }
        int splitIndex = url.indexOf("?");
        if (splitIndex <= 0) {
            return url;
        }
        String serviceUrl = url.substring(0, splitIndex);
        String queryString = url.substring(splitIndex + 1, url.length());
        String newQueryString = "";
        if (queryString.length() > 0) {
            String[] nameValues = queryString.split("&");
            for (String nameValue : nameValues) {
                int index = nameValue.indexOf("=");
                String pname = null;
                String pvalue = null;
                if (index < 0) {
                    pname = nameValue;
                    pvalue = "";
                } else {
                    pname = nameValue.substring(0, index);
                    pvalue = nameValue.substring(index + 1, nameValue.length());
                    try {
                        pvalue = URLEncoder.encode(pvalue, charSet);
                    } catch (UnsupportedEncodingException e) {
                        throw new IllegalArgumentException("invalid charset : " + charSet);
                    }
                }
                newQueryString += pname + "=" + pvalue + "&";
            }
            newQueryString = newQueryString.substring(0, newQueryString.length() - 1);
        }
        return serviceUrl + "?" + newQueryString;
    }

    /**
     * @param urlConn
     * @param sslVerify
     * @param hostnameVerify
     * @param trustCertFactory
     * @param clientKeyFactory
     */
    private static void setSSLSocketFactory(HttpURLConnection urlConn, boolean sslVerify, boolean hostnameVerify, TrustKeyStore trustCertFactory, ClientKeyStore clientKeyFactory) {
        try {
            SSLSocketFactory socketFactory = null;
            if (trustCertFactory != null || clientKeyFactory != null || !sslVerify) {
                SSLContext sc = SSLContext.getInstance("SSL");
                TrustManager[] trustManagers = null;
                KeyManager[] keyManagers = null;
                if (trustCertFactory != null) {
                    trustManagers = trustCertFactory.getTrustManagerFactory().getTrustManagers();
                }
                if (clientKeyFactory != null) {
                    keyManagers = clientKeyFactory.getKeyManagerFactory().getKeyManagers();
                }
                if (!sslVerify) {
                    trustManagers = trustAnyManagers;
                    hostnameVerify = false;
                }
                sc.init(keyManagers, trustManagers, new java.security.SecureRandom());
                socketFactory = sc.getSocketFactory();
            }

            if (urlConn instanceof HttpsURLConnection) {
                HttpsURLConnection httpsUrlCon = (HttpsURLConnection) urlConn;
                if (socketFactory != null) {
                    httpsUrlCon.setSSLSocketFactory(socketFactory);
                }
                //设置是否验证hostname
                if (!hostnameVerify) {
                    httpsUrlCon.setHostnameVerifier(new TrustAnyHostnameVerifier());
                }
            }
            if (urlConn instanceof HttpsURLConnection) {
                HttpsURLConnection httpsUrlCon = (HttpsURLConnection) urlConn;
                if (socketFactory != null) {
                    httpsUrlCon.setSSLSocketFactory(socketFactory);
                }
                //设置是否验证hostname
                if (!hostnameVerify) {
                    httpsUrlCon.setHostnameVerifier(new TrustAnyHostnameVerifierOld());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     *
     */
    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class TrustAnyHostnameVerifierOld implements HostnameVerifier {
        public boolean verify(String arg0, SSLSession session) {
            return true;
        }
    }

    public static ClientKeyStore loadClientKeyStore(String keyStorePath, String keyStorePass, String privateKeyPass) {
        try {
            return loadClientKeyStore(new FileInputStream(keyStorePath), keyStorePass, privateKeyPass);
        } catch (Exception e) {
            logger.error("loadClientKeyFactory fail : " + e.getMessage(), e);
            return null;
        }
    }

    public static ClientKeyStore loadClientKeyStore(InputStream keyStoreStream, String keyStorePass, String privateKeyPass) {
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(keyStoreStream, keyStorePass.toCharArray());
            kmf.init(ks, privateKeyPass.toCharArray());
            return new ClientKeyStore(kmf);
        } catch (Exception e) {
            logger.error("loadClientKeyFactory fail : " + e.getMessage(), e);
            return null;
        }
    }

    public static TrustKeyStore loadTrustKeyStore(String keyStorePath, String keyStorePass) {
        try {
            return loadTrustKeyStore(new FileInputStream(keyStorePath), keyStorePass);
        } catch (Exception e) {
            logger.error("loadTrustCertFactory fail : " + e.getMessage(), e);
            return null;
        }
    }

    public static TrustKeyStore loadTrustKeyStore(InputStream keyStoreStream, String keyStorePass) {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(keyStoreStream, keyStorePass.toCharArray());
            tmf.init(ks);
            return new TrustKeyStore(tmf);
        } catch (Exception e) {
            logger.error("loadTrustCertFactory fail : " + e.getMessage(), e);
            return null;
        }
    }

}