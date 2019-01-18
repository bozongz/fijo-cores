package com.fijo.cores.app.model;

import javax.persistence.*;

import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.ProcessProperty;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.fijo.cores.utils.annotations.ProcessProperty;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 业务流程草稿
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_draft", uniqueConstraints={@UniqueConstraint(columnNames={"processTypeId", "processHeaderId", "receiptId"})})
@ApiModel
public class ProcessDraft extends ProcessModel<ProcessDraft> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6133954955712649528L;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="app_process_draft_seq", sequenceName="app_process_draft_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_process_draft_seq")
    @ProcessProperty
    @ApiModelProperty(value="主键Id")
    protected Long id;

	//所有业务单据的主键必须统一类型，否则无法写入待办和审批记录
	@NotNullColumn(value="业务单据Id")
	@Column(name = "receiptId", nullable = false)
    @ApiModelProperty(value="业务单据Id")
	private Long receiptId;
	
	public Long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}
	
}