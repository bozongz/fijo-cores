/**
 * 
 */
package com.fijo.cores.utils;

import com.fijo.cores.utils.http.LocalHttpClient;
import it.sauronsoftware.base64.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import com.fijo.cores.utils.http.LocalHttpClient;

/**
 * 百度地图操作类
 * 
 * @author zhangbo
 *
 */
public class BaiduMapUtil {
	
	/**
	 * 将国家GCJ-02坐标 转换成 百度BD-09坐标
	 * @param lng
	 * @param lat
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static Map<String,String> convertMapLocation(String lng, String lat) {
		String convertUrl = "http://api.map.baidu.com/ag/coord/convert?from=2&to=4&x={x}&y={y}";
		convertUrl = convertUrl.replace("{x}", lng);
		convertUrl = convertUrl.replace("{y}", lat);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setUri(convertUrl).build();
		@SuppressWarnings("unchecked")
		Map<String,String> map = LocalHttpClient.executeJsonResult(httpUriRequest, Map.class);
		// 百度经度
		map.put("bd09lng", Base64.decode(map.get("x"), Constants.CHARSET));
		// 百度维度
		map.put("bd09lat", Base64.decode(map.get("y"), Constants.CHARSET));
		return map;
	}
}
