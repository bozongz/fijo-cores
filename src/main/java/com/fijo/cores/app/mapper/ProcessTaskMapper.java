package com.fijo.cores.app.mapper;

import com.fijo.cores.mapper.IGenericMapper;
import org.apache.ibatis.annotations.Param;

import com.fijo.cores.app.model.ProcessTask;
import com.fijo.cores.mapper.IGenericMapper;

public interface ProcessTaskMapper extends IGenericMapper<ProcessTask, Long> {
	ProcessTask getCurrentUserTask(@Param("typeId") Integer typeId,
			@Param("headerId") Integer headerId,
			@Param("receiptId") Long receiptId,
			@Param("currentUserId") Integer currentUserId);

    int deleteById(@Param("id") Long id);
}
