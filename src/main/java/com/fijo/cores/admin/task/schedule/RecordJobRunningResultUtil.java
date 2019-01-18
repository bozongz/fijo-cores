/**
 * 
 */
package com.fijo.cores.admin.task.schedule;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.configs.CoreConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fijo.cores.admin.task.model.RecordJobRunningResult;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.configs.CoreConfig;

/**
 * @author Li
 *
 */
@Component
public class RecordJobRunningResultUtil {
	private static transient final Log log = LogFactory.getLog(RecordJobRunningResultUtil.class);
	
	@Autowired
	private CoreConfig coreConfig;
	
	@Autowired
	@Qualifier("recordJobRunningResultService")
	private IGenericService<RecordJobRunningResult, Long> recordService;
	
	public void record(String jobName, long startTime, long endTime, String content, Boolean result){		
		//写入文本
//		try {
//			Collection<String> lines = Lists.newArrayList();	
//			String startTimeStr = DateUtil.getTimestamp(new Date(startTime));
//			String endTimeStr = DateUtil.getTimestamp(new Date(endTime));
//			lines.add("开始时间："+startTimeStr);
//			lines.add("结束时间："+endTimeStr);
//			lines.add("耗时:"+(endTime-startTime)/1000+"秒");
//			lines.add("内容:"+content);
//			String jobFileName = System.getProperty(coreConfig.getValue("app.root"))+"/static/logs/jobs/"+jobName+DateUtil.getTimestamp(DateUtil.getCurrent(), "yyyy-MM-ddHHmmss");
//			FileUtils.writeLines(new File(jobFileName+".txt"), lines);
//		} catch (IOException e) {
//			Exceptions.printException(e);
//		}
		
		//写入数据库
		RecordJobRunningResult job = new RecordJobRunningResult();
		job.setJobName(jobName);
		job.setRunStartTime(new Date(startTime));
		job.setRunEndTime(new Date(endTime));
		job.setContent(StringUtils.substring(content, 0, 254)); //考虑异常信息太长，超过255个varchar
		job.setCreateDate(DateUtil.getCurrent());
		job.setResult(result);
		Long seconds = (endTime-startTime)/1000;
		job.setUseSeconds(seconds.intValue());
		int ret = recordService.create(job);
		log.debug(String.format("Record %s %s ......................", jobName, ret>0?"success":"fail"));
		
		if(!result){ //出现异常
			String jobFileName = System.getProperty(coreConfig.getValue("app.root"))+"/static/logs/jobs/"+jobName+DateUtil.getTimestamp(DateUtil.getCurrent(), "yyyy-MM-dd-HHmmss");
			try {
				FileUtils.writeStringToFile(new File(jobFileName+".txt"), content);
			} catch (IOException e) {
				Exceptions.printException(e);
			}
		}
	}
	
	
	public void record(String jobName, long startTime, long endTime, Collection<String> contents, Boolean result){						
		StringBuffer sb = new StringBuffer();
		for(String s:contents){
			sb.append(s+Constants.SPACE);
		}
		record(jobName, startTime, endTime, sb.toString(), result);
	}
}
