package com.fijo.cores.app.service;

import com.fijo.cores.service.IGenericService;
import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.service.IGenericService;

public interface IProcessTaskService extends IGenericService<ProcessTask, Long> {

	ProcessTask getCurrentUserTask(Integer typeId, Integer headerId,
			Long receiptId, Integer currentUserId);

    int deleteById(Long id);

}
