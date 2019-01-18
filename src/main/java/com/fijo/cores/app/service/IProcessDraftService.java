package com.fijo.cores.app.service;

import com.fijo.cores.app.model.ProcessDraft;
import com.fijo.cores.service.ILogicService;
import com.fijo.cores.app.model.ProcessDraft;
import com.fijo.cores.service.ILogicService;

public interface IProcessDraftService extends ILogicService<ProcessDraft, Long> {
	ProcessDraft getOne(Integer processTypeId, Integer processHeaderId, Long receiptId);
}
