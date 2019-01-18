package com.fijo.cores.utils.enums;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class ProcessHeaderCodeDynaEnum extends PropertiesDynaEnum {

	@PostConstruct
	public void init() {	
		init(ProcessHeaderCodeDynaEnum.class);
	}
	
	public ProcessHeaderCodeDynaEnum() {
		super();
	}
	
	protected ProcessHeaderCodeDynaEnum(String name, String meaning, int ordinal) {
		super(name, meaning, ordinal);
	}
}
