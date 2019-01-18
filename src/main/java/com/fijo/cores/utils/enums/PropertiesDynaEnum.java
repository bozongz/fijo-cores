package com.fijo.cores.utils.enums;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Properties;

import com.fijo.cores.utils.Constants;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.fijo.cores.utils.Constants;

public class PropertiesDynaEnum extends DynaEnum<PropertiesDynaEnum> {
	
	protected PropertiesDynaEnum(){
	}
	
	protected PropertiesDynaEnum(String name, String meaning, int ordinal) {
		super(name, meaning, ordinal);
	}
    
    protected <E> void init(Class<E> clazz) {
    	try {
			initProps(clazz);
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
    }   
    
    private  <E> void initProps(Class<E> clazz) throws Exception {
    	String rcName = clazz.getSimpleName() + ".properties";
    	File rcFile = ResourceUtils.getFile("classpath:"+rcName);
    	log.debug(rcFile.exists());
    	Properties prop=new Properties();  
    	prop.load(new InputStreamReader(new FileInputStream(rcFile), Constants.CHARSET));
    	Constructor<E> minimalConstructor = getConstructor(clazz, new Class[] {String.class, int.class});
    	Constructor<E> additionalConstructor = getConstructor(clazz, new Class[] {String.class, String.class, int.class});
    	int ordinal = 0;
    	Iterator<Object> keys = prop.keySet().iterator();
    	while(keys.hasNext()){
    		String key = keys.next().toString();
    		String value = prop.getProperty(key);
    		if(StringUtils.isEmpty(value))
    			minimalConstructor.newInstance(key, ordinal++);
    		else
    			additionalConstructor.newInstance(key, value, ordinal++);
    	}
    }
    
    @SuppressWarnings("unchecked")
	private static <E> Constructor<E> getConstructor(Class<E> clazz, Class<?>[] argTypes) {
    	for(Class<?> c = clazz;  c != null;  c = c.getSuperclass()) {
        	try {
        		return (Constructor<E>)c.getDeclaredConstructor(argTypes);
        	} catch(Exception e) {
        		continue;
        	}
    	}
    	return null;
    }


}
