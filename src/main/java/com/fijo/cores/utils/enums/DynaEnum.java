package com.fijo.cores.utils.enums;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("rawtypes")
public class DynaEnum<E extends DynaEnum> {
	
	public final Log log = LogFactory.getLog(getClass());
	
	private static Map<Class<? extends DynaEnum<?>>, Map<String, DynaEnum<?>>> elements = 
		new LinkedHashMap<Class<? extends DynaEnum<?>>, Map<String, DynaEnum<?>>>();
	
    private String name;

    private String meaning;
    
    private int ordinal;
    
    public String name() {
    	return name;
    }

    public int ordinal() {
    	return ordinal;
    }

	public String meaning() {
		return meaning;
	}
	
	protected DynaEnum(){		
	}
	
	protected DynaEnum(String name, String meaning, int ordinal) {
		this.name = name;
		this.meaning = meaning;
		this.ordinal = ordinal;
		Map<String, DynaEnum<? extends DynaEnum>> typeElements = elements.get(getClass());
		if (typeElements == null) {
			typeElements = new LinkedHashMap<String, DynaEnum<? extends DynaEnum>>();
			elements.put(getDynaEnumClass(), typeElements);
		}
		typeElements.put(name, this);
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends DynaEnum<?>> getDynaEnumClass() {
		return (Class<? extends DynaEnum<?>>) getClass();
	}

    @Override
	public String toString() {
    	return name;
    }

    @Override
	public final boolean equals(Object other) { 
        return this == other;
    }

    @Override
	public final int hashCode() {
        return super.hashCode();
    }

    @Override
	protected final Object clone() throws CloneNotSupportedException {
    	throw new CloneNotSupportedException();
    }

    public final int compareTo(E other) {
		DynaEnum<?> self = this;
		if (self.getClass() != other.getClass() && // optimization
	            self.getDeclaringClass() != other.getDeclaringClass())
		    throw new ClassCastException();
		return self.ordinal - other.ordinal();
    }

	public final Class getDeclaringClass() {
		Class clazz = getClass();
		Class zuper = clazz.getSuperclass();
		return (zuper == DynaEnum.class) ? clazz : zuper;
    }

    @SuppressWarnings("unchecked")
	public <T extends DynaEnum<T>> T valueOf(Class<T> enumType, String name) {
    	return (T)elements.get(enumType).get(name);
    }

	private void readObject(ObjectInputStream in) throws IOException,
        ClassNotFoundException {
            throw new InvalidObjectException("can't deserialize enum");
    }

    @SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize enum");
    }

    @Override
	protected final void finalize() { }
    
	public Map<String, DynaEnum<?>> values() {
    	return elements.get(getClass());
    }
	
    public DynaEnum<?> value(String name) {
    	Map<String, DynaEnum<? extends DynaEnum>> values = values();
    	return values.get(name);
    }
}
