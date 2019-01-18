package com.fijo.cores.utils.http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.Constants;
 
/**
 * 封装HttpClient
 * 
 * @author zhangbo
 */
public class HttpClientUtil {
	protected transient final Log log = LogFactory.getLog(HttpClientUtil.class);
	
    private Integer socketTimeout            = 300 * 1000; // 5分钟
    private Integer connectTimeout           = 300 * 1000; // 5分钟
    private Integer connectionRequestTimeout = 180 * 1000; // 3分钟
    
    private enum VERBTYPE {
        GET, POST
    }
 
    private static CloseableHttpClient client;
    private RequestConfig requestConfig;
    private static PoolingHttpClientConnectionManager connManager = null;
 
    static {
        try {
            @SuppressWarnings("deprecation")
			SSLContext sslContext = SSLContexts.custom().useTLS().build();
            sslContext.init(null, new TrustManager[] { new X509TrustManager() {
 
                public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }
 
                public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }
 
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            } }, null);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext)).build();
            connManager                           = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            SocketConfig socketConfig             = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
                    .setMaxLineLength(2000).build();
            ConnectionConfig connectionConfig     = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8)
                    .setMessageConstraints(messageConstraints).build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(20);
        } catch (KeyManagementException e) {
 
        } catch (NoSuchAlgorithmException e) {
 
        }
    }
 
    private static final HttpClientUtil INSTANCE = new HttpClientUtil();  

	public static final HttpClientUtil getInstance() {  
	    return INSTANCE;  
	}  

    private HttpClientUtil() {
        super();
        //client                      = HttpClientBuilder.create().build();//不使用连接池
        client                        = HttpClients.custom().setConnectionManager(connManager).build();
        this.requestConfig            = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
    }
 
    public String postJsonRequest(String requestUrl, String jsonString){
    	log.debug("requestUrl:"+requestUrl);
    	log.debug("jsonString:"+jsonString);
    	ResponseContent ret = null;
        if (requestUrl == null)
            return null;
        HttpEntity entity = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try {
            httpPost = new HttpPost(requestUrl);
        	StringEntity stringEntity = new StringEntity(jsonString, Constants.CHARSET);
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
            response = client.execute(httpPost);
            //响应状态
            StatusLine statusLine = response.getStatusLine();
            // 获取响应对象
            entity = response.getEntity();
            ret = new ResponseContent();
            ret.setStatusCode(statusLine.getStatusCode());
            getResponseContent(entity, ret);
        } catch(Exception e){
        	Exceptions.printException(e);
        }finally {
            close(entity, httpPost, response);
        }
        return ret == null ? null : ret.getContent();
    }
    
    public String getRequest(String requestUrl){
    	log.debug("requestUrl:"+requestUrl);
        return this.getRequest(requestUrl, null);
    }

    public String getRequest(String requestUrl, Map<String, Object> params){
    	log.debug("requestUrl:"+requestUrl);
    	log.debug("params:"+params);
        return this.performRequest(requestUrl, VERBTYPE.GET, params);
    }
 
    public String postRequest(String requestUrl){
    	log.debug("requestUrl:"+requestUrl);
        return this.postRequest(requestUrl, null);
    }
 
    public String postRequest(String requestUrl, Map<String, Object> params) {
    	log.debug("requestUrl:"+requestUrl);
    	log.debug("params:"+params);
        return performRequest(requestUrl, VERBTYPE.POST, params);
    }
 
    public String performRequest(String requestUrl, VERBTYPE requestMethod, Map<String, Object> params){
    	log.debug("requestUrl:"+requestUrl);
    	log.debug("requestMethod:"+requestMethod);
    	log.debug("params:"+params);
    	return performRequest(requestUrl, requestMethod, null, params);
    }
    
    /**
     * 根据url编码，请求方式，请求参数，请求URL
     */
    public String performRequest(String requestUrl, VERBTYPE requestMethod, String contentType, Map<String, Object> params){
    	log.debug("requestUrl:"+requestUrl);
    	log.debug("requestMethod:"+requestMethod);
    	log.debug("contentType:"+contentType);
    	log.debug("params:"+params);
    	ResponseContent ret = null;
    	if (requestUrl == null)
            return null;
        HttpEntity entity = null;
        HttpRequestBase request = null;
        CloseableHttpResponse response = null;
        try {
            if (VERBTYPE.GET == requestMethod) {
            	URIBuilder builder = new URIBuilder(requestUrl);		
    			if(params != null){
    				Iterator<Entry<String, Object>> it = params.entrySet().iterator();
    				while(it.hasNext()){
    					Entry<String,Object> entry = it.next();
    					builder.setParameter(entry.getKey(), entry.getValue().toString());
    				}
    			}	
    			request = new HttpGet(builder.build());	
            } else if (VERBTYPE.POST == requestMethod) {
            	request = new HttpPost(requestUrl);
            	List<NameValuePair> formParams = new ArrayList<NameValuePair>(); 
    			Iterator<Entry<String, Object>> it = params.entrySet().iterator();
    			while(it.hasNext()){
    				Entry<String,Object> entry = it.next();
    				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
    			}
    			entity = new UrlEncodedFormEntity(formParams, Constants.CHARSET);    			
    			((HttpPost)request).setEntity(entity); 
            }
            if (contentType != null) {
                request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            }
            request.setConfig(requestConfig);
            request.addHeader(HttpHeaders.USER_AGENT,
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)");
            response = client.execute(request);
            entity = response.getEntity(); // 获取响应实体
            StatusLine statusLine = response.getStatusLine();
            ret = new ResponseContent();            
            ret.setStatusCode(statusLine.getStatusCode());
            getResponseContent(entity, ret);
        } catch(Exception e){
        	Exceptions.printException(e);
        }finally {
            close(entity, request, response);
        }
        return ret == null ? null : ret.getContent();
    }
 
    public String postFileRequest(String requestUrl, String filePath){
    	log.debug("requestUrl:"+requestUrl);
    	log.debug("filePath:"+filePath);
    	ResponseContent ret = null;
        if (requestUrl == null)
            return null;
        HttpEntity entity = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try {
            httpPost = new HttpPost(requestUrl);
            FileBody bin = new FileBody(new File(filePath));
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("media", bin).build();// 请求体. media为文件对于的key值
            httpPost.setEntity(reqEntity);
            response = client.execute(httpPost);
            //响应状态
            StatusLine statusLine = response.getStatusLine();
            // 获取响应对象
            entity = response.getEntity();
            ret = new ResponseContent();
            ret.setStatusCode(statusLine.getStatusCode());
            getResponseContent(entity, ret);
        } catch(Exception e){
        	Exceptions.printException(e);
        }finally {
            close(entity, httpPost, response);
        }
        return ret == null ? null : ret.getContent();
    }
    
    private void getResponseContent(HttpEntity entity, ResponseContent ret) throws IOException {
        Header enHeader = entity.getContentEncoding();
        if (enHeader != null) {
            String charset = enHeader.getValue().toLowerCase();
            ret.setEncoding(charset);
        }
        String contenttype = this.getResponseContentType(entity);
        ret.setContentType(contenttype);
        ret.setContentTypeString(this.getResponseContentTypeString(entity));
        ret.setContentBytes(EntityUtils.toByteArray(entity));
    }
 
    private void close(HttpEntity entity, HttpRequestBase request, CloseableHttpResponse response){
    	try{
        if (request != null)
            request.releaseConnection();
        if (entity != null)
            entity.getContent().close();
        if (response != null)
            response.close();
    	}catch(Exception e){
    		Exceptions.printException(e);
    	}
    }
 
    private String getResponseContentType(HttpEntity method) {
        Header contenttype = method.getContentType();
        if (contenttype == null)
            return null;
        String ret = null;
        try {
            HeaderElement[] hes = contenttype.getElements();
            if (hes != null && hes.length > 0) {
                ret = hes[0].getName();
            }
        } catch (Exception e) {
        }
        return ret;
    }
 
    private String getResponseContentTypeString(HttpEntity method) {
        Header contenttype = method.getContentType();
        if (contenttype == null)
            return null;
        return contenttype.getValue();
    }
 
   
 
}
