package com.fijo.cores.app.model;

import java.io.Serializable;

import com.fijo.cores.service.ILogicService;
import com.fijo.cores.service.ILogicService;

public class ProcessStatusView{

	private ILogicService<? extends ProcessModel<?>,? extends Serializable> processService;
	
	private Class<? extends ProcessModel<?>> processModel;

	/**
	 * 
	 * @param processService
	 * @param processModel
	 */
	public ProcessStatusView(ILogicService<? extends ProcessModel<?>,? extends Serializable> processService,
			Class<? extends ProcessModel<?>> processModel) {
		super();
		this.processService = processService;
		this.processModel = processModel;
	}

	public ILogicService<? extends ProcessModel<?>, ? extends Serializable> getProcessService() {
		return processService;
	}

	public void setProcessService(
			ILogicService<? extends ProcessModel<?>, ? extends Serializable> processService) {
		this.processService = processService;
	}

	public Class<? extends ProcessModel<?>> getProcessModel() {
		return processModel;
	}

	public void setProcessModel(Class<? extends ProcessModel<?>> processModel) {
		this.processModel = processModel;
	}	
	
}