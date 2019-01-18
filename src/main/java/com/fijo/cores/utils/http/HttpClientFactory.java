package com.fijo.cores.utils.http;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.fijo.cores.exceptions.Exceptions;

/**
 * httpclient 4.3.x
 * @author Yi
 *
 */
public class HttpClientFactory{

//	public static HttpClient createHttpClient() {
//		try {
//			SSLContext sslContext = SSLContexts.custom().useSSL().build();
//			SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			return HttpClientBuilder.create().setSSLSocketFactory(sf).build();
//		} catch (KeyManagementException e) {
//			Exceptions.printException(e);;
//		} catch (NoSuchAlgorithmException e) {
//			Exceptions.printException(e);;
//		}
//		return null;
//	}

//	public static HttpClient createHttpClient(int maxTotal,int maxPerRoute) {
//		try {
//			SSLContext sslContext = SSLContexts.custom().useSSL().build();
//			SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
//			poolingHttpClientConnectionManager.setMaxTotal(maxTotal);
//			poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
//			return HttpClientBuilder.create()
//									.setConnectionManager(poolingHttpClientConnectionManager)
//									.setSSLSocketFactory(sf)
//									.build();
//		} catch (KeyManagementException e) {
//			Exceptions.printException(e);;
//		} catch (NoSuchAlgorithmException e) {
//			Exceptions.printException(e);;
//		}
//		return null;
//	}

	/**
	 * 解决：HttpClient WARNING: Cookie rejected: Illegal domain attribute
	 * http://stackoverflow.com/questions/7459279/httpclient-warning-cookie-rejected-illegal-domain-attribute
	 * @param maxTotal
	 * @param maxPerRoute
	 * @return
	 */
	public static HttpClient createHttpClient(int maxTotal,int maxPerRoute) {
		try {
			SSLContext sslContext = SSLContexts.custom().useSSL().build();
			SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
			poolingHttpClientConnectionManager.setMaxTotal(maxTotal);
			poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
			RequestConfig customizedRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
			HttpClientBuilder customizedClientBuilder = HttpClients.custom().setDefaultRequestConfig(customizedRequestConfig);
			CloseableHttpClient client = customizedClientBuilder.setConnectionManager(poolingHttpClientConnectionManager)
					.setSSLSocketFactory(sf)
					.build();
			return client;
		} catch (KeyManagementException e) {
			Exceptions.printException(e);;
		} catch (NoSuchAlgorithmException e) {
			Exceptions.printException(e);;
		}
		return null;
	}
	
	/**
	 * Key store 类型HttpClient
	 * @param keystore
	 * @param keyPassword
	 * @param supportedProtocols
	 * @param maxTotal
	 * @param maxPerRoute
	 * @return
	 */
	public static HttpClient createKeyMaterialHttpClient(KeyStore keystore,String keyPassword,String[] supportedProtocols) {
		try {
			SSLContext sslContext = SSLContexts.custom().useSSL().loadKeyMaterial(keystore, keyPassword.toCharArray()).build();
			SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext,supportedProtocols,
	                null,SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			return HttpClientBuilder.create().setSSLSocketFactory(sf).build();
		} catch (KeyManagementException e) {
			Exceptions.printException(e);;
		} catch (NoSuchAlgorithmException e) {
			Exceptions.printException(e);;
		} catch (UnrecoverableKeyException e) {
			Exceptions.printException(e);;
		} catch (KeyStoreException e) {
			Exceptions.printException(e);;
		}
		return null;
	}


}
