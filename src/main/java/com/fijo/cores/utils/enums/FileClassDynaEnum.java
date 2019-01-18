package com.fijo.cores.utils.enums;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class FileClassDynaEnum extends PropertiesDynaEnum {

	@PostConstruct
	public void init() {	
		init(FileClassDynaEnum.class);
	}
	
	public FileClassDynaEnum() {
		super();
	}
	
	protected FileClassDynaEnum(String name, String meaning, int ordinal) {
		super(name, meaning, ordinal);
	}
}
