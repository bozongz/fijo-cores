package com.fijo.cores.app.model;

import javax.persistence.*;

import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.ProcessProperty;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.ProcessProperty;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 业务流程主状态
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_status", uniqueConstraints={@UniqueConstraint(columnNames={"processTypeId", "processHeaderId", "receiptId"})})
@ApiModel
public class ProcessStatus extends ProcessModel<ProcessStatus> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5973898392541378419L;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="app_process_status_seq", sequenceName="app_process_status_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_process_status_seq")
    @ProcessProperty
    @ApiModelProperty(value="主键Id")
    protected Long id;

	//所有业务单据的主键必须统一类型，否则无法写入待办和审批记录
	@NotNullColumn(value="业务单据Id")
	@Column(name = "receiptId", nullable = false)
    @ApiModelProperty(value="业务单据Id")
	private Long receiptId;

	//为了快速检索流程是否都已结束，因此主单据冗余存储环节类型、环节版本(当前流程版本下，是否有未完结的流程)
	@NotNullColumn(value="环节类型")
	@Column(name = "processStepType", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
    @ApiModelProperty(value="环节类型")
	private ProcessEnum processStepType;
	
	@Column(name = "processStepVersion", nullable = false)
    @ApiModelProperty(value="环节版本号")
    private Integer processStepVersion;

	public Long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}

	public ProcessEnum getProcessStepType() {
		return processStepType;
	}

	public void setProcessStepType(ProcessEnum processStepType) {
		this.processStepType = processStepType;
	}

	public Integer getProcessStepVersion() {
		return processStepVersion;
	}

	public void setProcessStepVersion(Integer processStepVersion) {
		this.processStepVersion = processStepVersion;
	}
	
	
}