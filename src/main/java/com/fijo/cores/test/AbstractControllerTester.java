package com.fijo.cores.test;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * @author zhangbo
 * 参考：http://jinnianshilongnian.iteye.com/blog/2004660
 */
//注解风格  
//@RunWith(SpringJUnit4ClassRunner.class)  
//@WebAppConfiguration(value = "src/main/webapp")  
//@ContextHierarchy({  
//    @ContextConfiguration(name = "parent", classes = AppConfig.class),  
//    @ContextConfiguration(name = "child", classes = MvcConfig.class)  
//})  
//XML风格  
@RunWith(SpringJUnit4ClassRunner.class)  
@WebAppConfiguration(value = "src/main/webapp")  
@ContextHierarchy({  
      @ContextConfiguration(name = "parent", locations = {
    			"classpath:conf/applicationContext-resources.xml",
    			"classpath:conf/applicationContext-dao.xml",
    			"classpath*:conf/applicationContext-cores.xml",
    			"classpath:conf/applicationContext.xml",
    			"classpath:conf/applicationContext-servlet.xml"}) 
})  
public class AbstractControllerTester extends AbstractJUnit4SpringContextTests {
	
	private MediaType jsonContentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
	
	private MediaType formUrlContentType = new MediaType(MediaType.APPLICATION_FORM_URLENCODED.getType(),
            MediaType.APPLICATION_FORM_URLENCODED.getSubtype(),
            Charset.forName("utf8"));
	
	@Autowired  
    private WebApplicationContext wac;
	
    private MockMvc mockMvc;

    @SuppressWarnings("rawtypes")
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
    	for(HttpMessageConverter<?> converter:requestMappingHandlerAdapter.getMessageConverters()){
    		if(converter instanceof MappingJackson2HttpMessageConverter){
    			mappingJackson2HttpMessageConverter = converter;
    			break;
    		}
    	}
        Assert.assertNotNull(this.mappingJackson2HttpMessageConverter);
    }
    
    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build(); 
        Assert.assertNotNull(mockMvc);
    }
    
    @SuppressWarnings("unchecked")
	protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

	/**
	 * @return the wac
	 */
	public WebApplicationContext getWac() {
		return wac;
	}

	/**
	 * @return the mockMvc
	 */
	public MockMvc getMockMvc() {
		return mockMvc;
	}

	/**
	 * @return the jsonContentType
	 */
	public MediaType getJsonContentType() {
		return jsonContentType;
	}

	/**
	 * @return the formUrlContentType
	 */
	public MediaType getFormUrlContentType() {
		return formUrlContentType;
	}


    
}
