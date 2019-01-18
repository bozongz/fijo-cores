package com.fijo.cores.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * 业务代办回调任务
 * 
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_task_callbackretry")
@ApiModel
public class ProcessTaskCallbackRetry extends GenericModel<ProcessTaskCallbackRetry>{


    private static final long serialVersionUID = 406815278844587818L;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="app_callback_retry_seq", sequenceName="app_callback_retry_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_callback_retry_seq")
    @ApiModelProperty(value="主键Id")
    private Integer id;

    @Column(name = "processServiceClass", nullable = false)
    @ApiModelProperty(value="流程服务Bean")
	private String processServiceClass;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastExecuteDate", nullable = false)
    @ApiModelProperty(value="最后执行时间")
    private Date lastExecuteDate;

    @Column(name = "executeTimes", nullable = false)
    @ApiModelProperty(value="执行次数")
    private Integer executeTimes;

    @Column(name = "callbackType", nullable = false)
    @ApiModelProperty(value="回调类型")
    private String callbackType;

    @ApiModelProperty(value="任务Id")
    private Long taskId;

    @Column(name = "typeId", nullable = false)
    @ApiModelProperty(value="流程类型Id")
    private Integer typeId;

    @Column(name = "headerId", nullable = false)
    @ApiModelProperty(value="流程头Id")
    private Integer headerId;

    @Column(name = "receiptId", nullable = false)
    @ApiModelProperty(value="业务单据Id")
    private Long receiptId;

    @Column(name = "stepId", nullable = false)
    @ApiModelProperty(value="当前环节Id")
    private Integer stepId;

    @Column(name = "currentUserId", nullable = true, length = 50)
    @ApiModelProperty(value="受理人Id")
    private Integer currentUserId;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProcessServiceClass() {
        return processServiceClass;
    }

    public void setProcessServiceClass(String processServiceClass) {
        this.processServiceClass = processServiceClass;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    public Date getLastExecuteDate() {
        return lastExecuteDate;
    }

    public void setLastExecuteDate(Date lastExecuteDate) {
        this.lastExecuteDate = lastExecuteDate;
    }

    public Integer getExecuteTimes() {
        return executeTimes;
    }

    public void setExecuteTimes(Integer executeTimes) {
        this.executeTimes = executeTimes;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Integer headerId) {
        this.headerId = headerId;
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public Integer getStepId() {
        return stepId;
    }

    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Integer currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getCallbackType() {
        return callbackType;
    }

    public void setCallbackType(String callbackType) {
        this.callbackType = callbackType;
    }
}