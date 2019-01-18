package com.fijo.cores.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * 业务代办回调日志记录
 * 
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_task_callback_log")
@ApiModel
public class ProcessTaskCallbackLog extends GenericModel<ProcessTaskCallbackLog> {


    private static final long serialVersionUID = 5167156979941299089L;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="app_callback_log_seq", sequenceName="app_callback_log_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_callback_log_seq")
    @ApiModelProperty(value="主键Id")
    private Integer id;

    @ApiModelProperty(value="任务Id")
    private Long taskId;

    @Column(name = "callbackType", nullable = false)
    @ApiModelProperty(value="回调类型")
	private String callbackType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "callbackStartDate", nullable = false)
    @ApiModelProperty(value="回调开始时间")
	private Date callbackStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "callbackEndDate", nullable = false)
    @ApiModelProperty(value="回调结束时间")
    private Date callbackEndDate;

    @Column(name = "callbackDuration", nullable = false)
    @ApiModelProperty(value="回调持续时间")
    private Long callbackDuration;

    @Column(name = "callbackResult", nullable = false, columnDefinition = "int default 1")
    @ApiModelProperty(value="回调结果")
    protected Boolean callbackResult;

    @Column(name = "callbackError", nullable = true, length = 2000)
    @ApiModelProperty(value="回调错误信息")
    private String callbackError;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getCallbackType() {
        return callbackType;
    }

    public void setCallbackType(String callbackType) {
        this.callbackType = callbackType;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    public Date getCallbackStartDate() {
        return callbackStartDate;
    }

    public void setCallbackStartDate(Date callbackStartDate) {
        this.callbackStartDate = callbackStartDate;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    public Date getCallbackEndDate() {
        return callbackEndDate;
    }

    public void setCallbackEndDate(Date callbackEndDate) {
        this.callbackEndDate = callbackEndDate;
    }

    public Long getCallbackDuration() {
        return callbackDuration;
    }

    public void setCallbackDuration(Long callbackDuration) {
        this.callbackDuration = callbackDuration;
    }

    public Boolean getCallbackResult() {
        return callbackResult;
    }

    public void setCallbackResult(Boolean callbackResult) {
        this.callbackResult = callbackResult;
    }

    public String getCallbackError() {
        return callbackError;
    }

    public void setCallbackError(String callbackError) {
        this.callbackError = callbackError;
    }
}