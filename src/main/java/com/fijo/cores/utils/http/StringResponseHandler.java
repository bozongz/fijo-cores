package com.fijo.cores.utils.http;

import java.io.IOException;
import java.util.Set;

import com.fijo.cores.utils.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Sets;
import com.fijo.cores.utils.Constants;

public class StringResponseHandler{

	private static Set<ResponseHandler<String>> sets = Sets.newHashSet();
	
	public static  ResponseHandler<String> createResponseHandler(){
		return createResponseHandler(Constants.CHARSET);
	}

	public static  ResponseHandler<String> createResponseHandler(final String charset){
		if(sets.size() !=0){
			return sets.iterator().next();
		}else{
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
	                if (status >= 200 && status <= 302) {
	                    HttpEntity entity = response.getEntity();
	                    return EntityUtils.toString(entity, charset);	  
	                } else {
	                    throw new ClientProtocolException("Unexpected response status: " + status);
	                }
				}
			};
		sets.add(responseHandler);
		return responseHandler;
		}
	}
}
