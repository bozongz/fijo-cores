package com.fijo.cores.model;

import java.io.Serializable;


public class KeyValue<K extends Serializable, V> implements Serializable{    

	/**
	 * 
	 */
	private static final long serialVersionUID = -2074657237790011078L;
	private K key;
	private V value;
	private String optional;
	
	public KeyValue() {
		super();		
	}

	public KeyValue(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public KeyValue(K key, V value, String optional) {
		super();
		this.key = key;
		this.value = value;
		this.optional = optional;
	}

	/**
	 * @return the key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public V getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * @return the optional
	 */
	public String getOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(String optional) {
		this.optional = optional;
	}

	

}
