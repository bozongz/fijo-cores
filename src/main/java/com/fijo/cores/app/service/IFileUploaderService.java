package com.fijo.cores.app.service;

import com.fijo.cores.service.ILogicService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import com.fijo.cores.app.model.FileUploader;
import com.fijo.cores.service.ILogicService;

public interface IFileUploaderService extends ILogicService<FileUploader,Long> {
	
	String createMd5File(MultipartFile file, String fileClass);
	
	/**
	 * 既删除文件，又删除数据库记录(请谨慎使用!)
	 */
	@Deprecated
	int delete(FileUploader o);
	
	/**
	 * 仅删除数据库记录
	 */
	int deleteRecord(FileUploader o);
	
	/**
	 * 仅删除数据库记录
	 */
	int deleteById(@Param("id") Long id);
}
