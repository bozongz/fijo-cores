package com.fijo.cores.app.mapper;

import com.fijo.cores.mapper.ILogicMapper;
import org.apache.ibatis.annotations.Param;

import com.fijo.cores.app.model.ProcessDraft;
import com.fijo.cores.mapper.ILogicMapper;

public interface ProcessDraftMapper extends ILogicMapper<ProcessDraft,Long> {
	ProcessDraft getOne(@Param(value = "processTypeId") Integer processTypeId,
			@Param(value = "processHeaderId") Integer processHeaderId,
			@Param(value = "receiptId") Long receiptId);
}