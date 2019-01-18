/**
 * 
 */
package com.fijo.cores.admin.task.model;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fijo.cores.model.GenericModel;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * @author Li
 *
 */
@Entity
@Table(name = "sys_task_record_job_running")
@ApiModel
public class RecordJobRunningResult extends GenericModel<RecordJobRunningResult>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7465124274626826203L;

	@Id
    @SequenceGenerator(name="sys_task_record_job_run_seq", sequenceName="sys_task_record_job_run_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="sys_task_record_job_run_seq")
    @ApiModelProperty(value="主键Id")
	private Long id;

    @ApiModelProperty(value="任务名称")
	private String jobName;

    @ApiModelProperty(value="任务启动时间")
	private Date runStartTime;

    @ApiModelProperty(value="任务结束时间")
	private Date runEndTime;

    @ApiModelProperty(value="用时/秒")
	private Integer useSeconds;

    @ApiModelProperty(value="运行情况")
	private String content;

    @ApiModelProperty(value="记录写入时间")
	private Date createDate;

    @ApiModelProperty(value="运行结果")
	private Boolean result;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * @return the runStartTime
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	public Date getRunStartTime() {
		return runStartTime;
	}

	/**
	 * @param runStartTime the runStartTime to set
	 */
	public void setRunStartTime(Date runStartTime) {
		this.runStartTime = runStartTime;
	}

	/**
	 * @return the runEndTime
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	public Date getRunEndTime() {
		return runEndTime;
	}

	/**
	 * @param runEndTime the runEndTime to set
	 */
	public void setRunEndTime(Date runEndTime) {
		this.runEndTime = runEndTime;
	}

	/**
	 * @return the useSeconds
	 */
	public Integer getUseSeconds() {
		return useSeconds;
	}

	/**
	 * @param useSeconds the useSeconds to set
	 */
	public void setUseSeconds(Integer useSeconds) {
		this.useSeconds = useSeconds;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the createDate
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the result
	 */
	public Boolean getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(Boolean result) {
		this.result = result;
	}
	
	
}
