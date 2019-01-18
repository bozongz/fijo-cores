package com.fijo.cores.app.service;

import com.fijo.cores.app.model.ProcessStatus;
import com.fijo.cores.service.ILogicService;
import com.fijo.cores.app.model.ProcessStatus;
import com.fijo.cores.service.ILogicService;

import java.util.List;

public interface IProcessStatusService extends ILogicService<ProcessStatus, Long> {

	ProcessStatus getOne(Integer processTypeId, Integer processHeaderId, Long receiptId);

    Integer getJoinCount(Integer createUserId);

    List<ProcessStatus> getJoin();
	
	boolean checkProcessRunning(Integer processTypeId, Integer processHeaderId, Integer processStepVersion);

    int updateOnCompleted(ProcessStatus status);
}
