/**
 * 
 */
package com.fijo.cores.utils.http;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Maps;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.Constants;

/**
 * Http 请求调用工具类（不推荐使用，建议使用HttpClientUtil）
 * 
 * @author zhangbo
 *
 */
public class HttpRequestUtil {
	private static int timeout = 120 * 1000; // 2分钟超时
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String convertUrl = "http://api.map.baidu.com/ag/coord/convert?from=2&to=4&x={x}&y=22";
		HttpClientUtil client = HttpClientUtil.getInstance();
	    String res = client.getRequest(convertUrl);
	    System.out.println(res);
	    Map<String,Object> params = Maps.newHashMap();
	    params.put("username", "admin");
	    params.put("token", "8e9c9c7fe1d28e4bb3e9515ab7d5c0ea");
	    res = client.postRequest("http://localhost:8080/eintelligence/action/sso/einfo/content/list/checkReadContent", params);
	    System.out.println(res);
	    String url2 = "http://localhost:8080/eintelligence/action/sso/admin/authority/sysorg/query?username=admin&token=8e9c9c7fe1d28e4bb3e9515ab7d5c0ea";
	    res = client.postJsonRequest(url2, "{\"orgCode\":\"1012\"}");
	    System.out.println(res);
	    res = client.postFileRequest("https://api.weixin.qq.com/cgi-bin/media/upload?access_token=MJ_COtAB9Z21grEFCfxi2uSy-PyPiX302CU3MglxrgS6007g39HyW66rWEzrCxqiJM5ACMrkWk3g7RcVD8Rl8liOn2pG6rSjPpAKLxDn6dQ&type=image", "C:/Users/Li/Desktop/健货.jpg");
	    System.out.println(res);
        
	} 
	
	
	/**
	 * 发起HttpGet 请求
	 * @param requestUrl（请求URL）
	 * @param params （可选）
	 * @return 返回响应字符串
	 */
	@Deprecated
	public static String submitHttpGetRequest(String protocal, String requestUrl, Map<String,Object> params){
		URIBuilder builder;
		HttpGet httpget = null;
		try {
			builder = new URIBuilder(requestUrl);		
			if(params != null){
				Iterator<Entry<String, Object>> it = params.entrySet().iterator();
				while(it.hasNext()){
					Entry<String,Object> entry = it.next();
					builder.setParameter(entry.getKey(), entry.getValue().toString());
				}
			}	
			httpget = new HttpGet(builder.build());	
		} catch (URISyntaxException e) {
			httpget = null;
			Exceptions.printException(e);
		}
        return submitHttpRequest(protocal, httpget);
	}
	
	/**
	 * 以application/x-www-form-urlencoded协议提交Form表单请求
	 * @param requestUrl
	 * @param params
	 * @return
	 */
	@Deprecated
	public static String submitHttpPostRequest(String protocal, String requestUrl, Map<String,Object> params){
		HttpPost httpPost = new HttpPost(requestUrl);
		if(params != null){
			List<NameValuePair> formParams = new ArrayList<NameValuePair>(); 
			Iterator<Entry<String, Object>> it = params.entrySet().iterator();
			while(it.hasNext()){
				Entry<String,Object> entry = it.next();
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
			}
			UrlEncodedFormEntity entity;
			try {
				entity = new UrlEncodedFormEntity(formParams, Constants.CHARSET);
				httpPost.setEntity(entity); 
			} catch (UnsupportedEncodingException e) {
				httpPost = null;
				Exceptions.printException(e);
			} 
		}	
		return submitHttpRequest(protocal, httpPost);
	}
	
	/**
	 * 以application/json协议提交Json请求
	 * @param requestUrl
	 * @param jsonString
	 * @return
	 */
	@Deprecated
	public static String submitHttpPostJsonRequest(String protocal, String requestUrl, String jsonString){
		HttpPost httpPost = new HttpPost(requestUrl);
		StringEntity stringEntity = new StringEntity(jsonString, Constants.CHARSET);
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
		return submitHttpRequest(protocal, httpPost);
	}
	
	/**
	 * 包装Http请求
 	 * @param protocal http协议或https协议
	 * @param request  get请求或post请求
	 * @return
	 */
	private static String submitHttpRequest(String protocal, HttpRequestBase request){
		String result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		if(request != null){
			try{
				httpClient = HttpClients.createDefault();   
				if(protocal.equals(Constants.HTTPS))
					httpClient = (CloseableHttpClient) initHttpClient(httpClient);
				RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();//设置请求和传输超时时间
				request.setConfig(requestConfig);			
				response = httpClient.execute(request);
				// 获取响应实体    
		        HttpEntity entity = response.getEntity();
		        if (entity != null) {  
		            result = EntityUtils.toString(entity);
		            EntityUtils.consume(entity);
		        }
			}catch(IOException e){
				Exceptions.printException(e);
			}catch(Exception e){
				Exceptions.printException(e);
			}finally{
				if(response != null){
					try {
						response.close();
					} catch (IOException ie) {
						Exceptions.printException(ie);
					}  
				}
	            if(httpClient != null){
					try {
						httpClient.close();
					} catch (IOException ie1) {
						Exceptions.printException(ie1);
					}
	            }
			}
		}
        return result;
	}
	
	/**
	 * 微信自定义信任管理器X509TrustManager
	 * @param httpclient
	 * @return
	 */
	private static HttpClient initHttpClient(HttpClient httpclient) {
        try {
        	TrustManager[] tm = { new MyX509TrustManager() };
			// 取得SSL的SSLContext实例
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			// 初始化SSLContext
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SocketFactory ssf = (SocketFactory) sslContext.getSocketFactory();	
            ClientConnectionManager ccm = new DefaultHttpClient().getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 8443));
            HttpParams params = new BasicHttpParams();
            params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
            params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
            params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
            params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
            httpclient = new DefaultHttpClient(ccm, params);
        } catch (Exception ex) {
            Exceptions.printException(ex);
        }
        return httpclient;
    }
	
	@Deprecated
	public static String uploadFileFromPath(String requestUrl, String requestMethod, String filePath) throws ClientProtocolException, IOException{
		String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();  
        try {  
            HttpPost httpPost = new HttpPost(requestUrl);  
            // 把文件转换成流对象FileBody  
            File file = new File(filePath);  
            FileBody media = new FileBody(file);  
            
            /*StringBody uploadFileName = new StringBody("my.png", 
                    ContentType.create("text/plain", Consts.UTF_8));*/  
            // 以浏览器兼容模式运行，防止文件名乱码。  
            HttpEntity reqEntity = MultipartEntityBuilder.create()  
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)  
                    .addPart("file", media) // media对应服务端类的同名属性<File类型>  
                    .setCharset(CharsetUtils.get("UTF-8")).build();  
  
            httpPost.setEntity(reqEntity);  
            // 发起请求 并返回请求的响应  
            CloseableHttpResponse response = httpClient.execute(httpPost);  
            try {  
                // 获取响应对象  
                HttpEntity resEntity = response.getEntity();  
                if (resEntity != null) {  
                	result = EntityUtils.toString(resEntity, Charset.forName("UTF-8"));  
                }  
                // 销毁  
                EntityUtils.consume(resEntity);  
            } finally {  
                response.close();  
            }  
        } finally {  
            httpClient.close();  
        }
		return result;  
    }  
	
	/**
	 * 发起https请求并获取结果
	 *
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param requestData
	 *            提交的数据
	 * @throws IOException
	 */
	@Deprecated
	public static String invokeHttpsRequest(String requestUrl,
			String requestMethod, String requestData) throws IOException {
		StringBuffer buffer = new StringBuffer();
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		InputStream inputStream = null;
		HttpsURLConnection httpUrlConn = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化(证书过滤)
			TrustManager[] tm = { new MyX509TrustManager() };
			// 取得SSL的SSLContext实例
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			// 初始化SSLContext
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			// 当有数据需要提交时(当outputStr不为null时，向输出流写数据)
			if (null != requestData) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(requestData.getBytes(Constants.CHARSET));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			inputStream = httpUrlConn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream,
					Constants.CHARSET);
			bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}

			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			httpUrlConn.disconnect();
			bufferedReader = null;
			inputStreamReader = null;
			inputStream = null;
			httpUrlConn = null;
		} catch (ConnectException ce) {
			Exceptions.printException(ce);
		} catch (Exception e) {
			Exceptions.printException(e);
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
				bufferedReader = null;
			}
			if (inputStreamReader != null) {
				inputStreamReader.close();
				inputStreamReader = null;
			}
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			if (httpUrlConn != null) {
				httpUrlConn.disconnect();
				httpUrlConn = null;
			}
		}
		return buffer.toString();
	}

	/**
	 * 发起http 请求并获取结果
	 * @param requestUrl
	 * @param requestMethod
	 * @param requestData
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static String invokeHttpRequest(String requestUrl,
			String requestMethod, String requestData) throws IOException {
		StringBuffer buffer = new StringBuffer();
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		InputStream inputStream = null;
		HttpURLConnection httpUrlConn = null;
		try {
			URL url = new URL(requestUrl);
			httpUrlConn = (HttpURLConnection) url.openConnection();
			httpUrlConn.setDoInput(true);
			httpUrlConn.setRequestMethod(requestMethod);
			httpUrlConn.connect();
			inputStream = httpUrlConn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream,
					Constants.CHARSET);
			bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			httpUrlConn.disconnect();
			bufferedReader = null;
			inputStreamReader = null;
			inputStream = null;
			httpUrlConn = null;
		} catch (Exception e) {
			Exceptions.printException(e);
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
				bufferedReader = null;
			}
			if (inputStreamReader != null) {
				inputStreamReader.close();
				inputStreamReader = null;
			}
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			if (httpUrlConn != null) {
				httpUrlConn.disconnect();
				httpUrlConn = null;
			}
		}
		return buffer.toString();
	}

	@Deprecated
	public String invokeFileRequest(String requestUrl, String requestMethod,
			File file) {
		String result = null;  
		try {
			URL uploadUrl = new URL(requestUrl);
			HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl
					.openConnection();
			uploadConn.setDoOutput(true);
			uploadConn.setDoInput(true);
			uploadConn.setRequestMethod(requestMethod);
			// 设置请求头Content-Type
			// 定义数据分隔符
			String BOUNDARY = "----------" + System.currentTimeMillis();
			uploadConn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + BOUNDARY);

			// 请求正文信息
			// 第一部分：
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"file\";filename=\""
					+ file.getName() + "\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			byte[] head = sb.toString().getBytes(Constants.CHARSET);
			// 获得输出流
			OutputStream out = new DataOutputStream(
					uploadConn.getOutputStream());
			// 输出表头
			out.write(head);
			// 文件正文部分
			// 把文件已流文件的方式 推入到url中
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();

			// 结尾部分
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
			out.write(foot);
			out.flush();
			out.close();

			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = null;
			try {
				// 定义BufferedReader输入流来读取URL的响应
				reader = new BufferedReader(new InputStreamReader(uploadConn.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					// System.out.println(line);
					buffer.append(line);
				}
				if (result == null) {
					result = buffer.toString();
				}
			} catch (IOException e) {
				System.out.println("发送POST请求出现异常！" + e);
				Exceptions.printException(e);;
				throw new IOException("数据读取异常");
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

			System.out.println(result);

		} catch (Exception e) {
			
		}
		return result;
	}
	

}
