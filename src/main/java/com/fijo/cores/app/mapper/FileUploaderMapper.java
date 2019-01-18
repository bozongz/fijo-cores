package com.fijo.cores.app.mapper;
import com.fijo.cores.mapper.ILogicMapper;
import org.apache.ibatis.annotations.Param;

import com.fijo.cores.app.model.FileUploader;
import com.fijo.cores.mapper.ILogicMapper;

public interface FileUploaderMapper extends ILogicMapper<FileUploader,Long> {
	
	int deleteById(@Param("id") Long id);
}