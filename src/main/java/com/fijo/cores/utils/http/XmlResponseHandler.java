package com.fijo.cores.utils.http;

import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.xml.XMLConverUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XmlResponseHandler{
	private static transient final Log log = LogFactory.getLog(XmlResponseHandler.class);

	private static Map<String, ResponseHandler<?>> map = new HashMap<String, ResponseHandler<?>>();
	
	public static <T> ResponseHandler<T> createResponseHandler(final Class<T> clazz){
		return createResponseHandler(clazz, Constants.CHARSET);		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ResponseHandler<T> createResponseHandler(final Class<T> clazz, final String charset){
		if(map.containsKey(clazz.getName())){
			return (ResponseHandler<T>)map.get(clazz.getName());
		}else{
			ResponseHandler<T> responseHandler = new ResponseHandler<T>() {
				@Override
				public T handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
	                if (status >= 200 && status < 300) {
	                    HttpEntity entity = response.getEntity();
                        String str = EntityUtils.toString(entity, charset);
	                    log.debug(clazz.getSimpleName()+"  Serializable String value is:"+str);
	                    T o = XMLConverUtil.convertToObject(clazz, str);
	                    log.debug(clazz.getSimpleName()+"  Deserializable Object value is:"+o);
	                   return o;
	                } else {
	                    throw new ClientProtocolException("Unexpected response status: " + status);
	                }

				}
			};
			map.put(clazz.getName(), responseHandler);
			return responseHandler;
		}
	}


}
