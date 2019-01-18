package com.fijo.cores.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;
import com.fijo.cores.exceptions.Exceptions;

public class MapUtil {
	private static transient final Log log = LogFactory.getLog(MapUtil.class);
	
	/**
	 * Map key 排序
	 * @param map
	 * @return
	 */
	public static Map<String,String> order(Map<String, String> map){
		HashMap<String, String> tempMap = new LinkedHashMap<String, String>();
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(	map.entrySet());

		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,Map.Entry<String, String> o2) {
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		for (int i = 0; i < infoIds.size(); i++) {
			Map.Entry<String, String> item = infoIds.get(i);
			tempMap.put(item.getKey(), item.getValue());
		}
		return tempMap;
	}


	/**
	 * 转换对象为map
	 * @param object
	 * @param ignore
	 * @return
	 */
	public static Map<String,String> objectToMap(Object object,String... ignore){
		Map<String,String> tempMap = new LinkedHashMap<String, String>();
		for(Field f : getAllFields(object.getClass())){
			if(!f.isAccessible()){
				f.setAccessible(true);
			}
			boolean ig = false;
			if(ignore!=null&&ignore.length>0){
				for(String i : ignore){
					if(i.equals(f.getName())){
						ig = true;
						break;
					}
				}
			}
			if(ig){
				continue;
			}else{
				Object o = null;
				try {
					o = f.get(object);
				} catch (IllegalArgumentException e) {
					Exceptions.printException(e);;
				} catch (IllegalAccessException e) {
					Exceptions.printException(e);;
				}
				tempMap.put(f.getName(), o==null?"":o.toString());
			}
		}
		return tempMap;
	}

	/**
	 * 获取所有Fields,包含父类field
	 * @param clazz
	 * @return
	 */
	private static List<Field> getAllFields(Class<?> clazz){
		if(!clazz.equals(Object.class)){
			List<Field> fields = new ArrayList<Field>(Arrays.asList(clazz.getDeclaredFields()));
			List<Field> fields2 = getAllFields(clazz.getSuperclass());
			if(fields2!=null){
				fields.addAll(fields2);
			}
			return fields;
		}else{
			return null;
		}
	}

	/**
	 * url 参数串连
	 * @param map
	 * @param keyLower
	 * @param valueUrlencode
	 * @return
	 */
	public static String mapJoin(Map<String, String> map,boolean keyLower,boolean valueUrlencode){
		StringBuilder stringBuilder = new StringBuilder();
		for(String key :map.keySet()){
			if(map.get(key)!=null&&!"".equals(map.get(key))){
				try {
					String temp = (key.endsWith("_")&&key.length()>1)?key.substring(0,key.length()-1):key;
					stringBuilder.append(keyLower?temp.toLowerCase():temp)
								 .append("=")
								 .append(valueUrlencode?URLEncoder.encode(map.get(key),"utf-8").replace("+", "%20"):map.get(key))
								 .append("&");
				} catch (UnsupportedEncodingException e) {
					Exceptions.printException(e);;
				}
			}
		}
		if(stringBuilder.length()>0){
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
		}
		return stringBuilder.toString();
	}

	/**
	 * 简单 xml 转换为 Map
	 * @param reader
	 * @return
	 */
	public static Map<String,String> xmlToMap(String xml){
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
			Element element = document.getDocumentElement();
			NodeList nodeList = element.getChildNodes();
			Map<String, String> map = new LinkedHashMap<String, String>();
			for(int i=0;i<nodeList.getLength();i++){
				Element e = (Element) nodeList.item(i);
				map.put(e.getNodeName(),e.getTextContent());
			}
			return map;
		} catch (DOMException e) {
			Exceptions.printException(e);;
		} catch (ParserConfigurationException e) {
			Exceptions.printException(e);;
		} catch (SAXException e) {
			Exceptions.printException(e);;
		} catch (IOException e) {
			Exceptions.printException(e);;
		}
		return null;
	}

	public static Map<String, String> getRequestParameters(HttpServletRequest request){
		Map<String, String[]> requestParams = request.getParameterMap();
		Map<String, String> params = Maps.newHashMap();
		for (String key : requestParams.keySet()) {  
			String value = "";
            String[] values = requestParams.get(key);  
            for (int i = 0; i < values.length; i++) {  
                value += values[i];  
            }  
            if(StringUtils.isNotEmpty(value)){
				params.put(key, value);
			}
        }  	
		return params;
	}
	
	/**
	 * 按照腾讯官方要求，将请求参数拼装至URL中，以便计算签名
	 * http://mp.weixin.qq.com/wiki/7/1c97470084b73f8e224fe6d9bab1625b.html#.E9.99.84.E5.BD.951-JS-SDK.E4.BD.BF.E7.94.A8.E6.9D.83.E9.99.90.E7.AD.BE.E5.90.8D.E7.AE.97.E6.B3.95
	 * 
	 * 朋友圈   from=timeline&isappinstalled=0
	 * 微信群   from=groupmessage&isappinstalled=0
	 * 好友分享 from=singlemessage&isappinstalled=0
	 * @param request
	 * @param pageUrl
	 * @return
	 */
	public static String signatureWeChatUrl(HttpServletRequest request, String pageUrl) {
		String queryString = request.getQueryString();
		log.debug("signatureWeChatUrl QueryString: " + queryString);
		if(StringUtils.isNotEmpty(queryString)){
			String[] queryStrings = StringUtils.split(queryString, "&");
			if(queryStrings.length > 0){
				pageUrl += "?";
				for(String q:queryStrings){
					if(!StringUtils.startsWith(q, "#") && !StringUtils.startsWith(q, "code=") && !StringUtils.startsWith(q, "state="))
						pageUrl += q+"&";
				}
			}
			pageUrl = StringUtils.removeEnd(pageUrl, "&");
		}
		log.debug("signatureWeChatUrl pageUrl: " + pageUrl);
		return pageUrl;
	}
}
