package com.fijo.cores.app.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.fijo.cores.model.GenericModel;
import com.fijo.cores.utils.annotations.NotNullColumn;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 并行流程跟踪
 * 
 * @author zhangbo
 *
 */
@Entity
@Table(name = "app_process_track")
@XmlRootElement
@ApiModel
public class ProcessTrack extends GenericModel<ProcessTrack> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4607690635955379584L;

	@Id
	@Column(name = "id")
    @SequenceGenerator(name="app_process_track_seq", sequenceName="app_process_track_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="app_process_track_seq")
    @ApiModelProperty(value="主键Id")
	private Long id;

	@Column(name = "typeId", nullable = false)
    @ApiModelProperty(value="业务类型Id")
	private Integer typeId;

	@Column(name = "headerId", nullable = false)
    @ApiModelProperty(value="流程头Id")
	private Integer headerId;
	
	//所有业务单据的主键必须统一类型，否则无法写入待办和审批记录
	@Column(name = "receiptId", nullable = false)
    @ApiModelProperty(value="业务单据Id")
	private Long receiptId;
		
	@Column(name = "forkFromId", nullable = true) //分支来源环节
    @ApiModelProperty(value="分支来源环节Id")
	private String forkFromId; 
		
	@Column(name = "currentStepCode", nullable = false)
    @ApiModelProperty(value="当前流程编码")
	private String currentStepCode;
	
	@Transient
	private String nextStepCode;
	
	public ProcessTrack() {
		super();
	}

	/**
	 * 查询fork到join的构造函数
	 * @param typeId
	 * @param headerId
	 * @param receiptId
	 * @param forkFromId
	 * @param currentStepCode
	 */
	public ProcessTrack(Integer typeId, Integer headerId, Long receiptId,
			String forkFromId) {
		super();
		this.typeId = typeId;
		this.headerId = headerId;
		this.receiptId = receiptId;
		this.forkFromId = forkFromId;
	}
	
	/**
	 * 新增构造函数
	 * @param typeId
	 * @param headerId
	 * @param receiptId
	 * @param forkFromId
	 * @param currentStepCode
	 */
	public ProcessTrack(Integer typeId, Integer headerId, Long receiptId,
			String forkFromId, String currentStepCode) {
		super();
		this.typeId = typeId;
		this.headerId = headerId;
		this.receiptId = receiptId;
		this.forkFromId = forkFromId;
		this.currentStepCode = currentStepCode;
	}
	
	/**
	 * 更新构造函数
	 * @param typeId
	 * @param headerId
	 * @param receiptId
	 * @param forkFromId
	 * @param currentStepCode
	 * @param nextStepCode
	 */
	public ProcessTrack(Integer typeId, Integer headerId, Long receiptId,
			String forkFromId, String currentStepCode, String nextStepCode) {
		super();
		this.typeId = typeId;
		this.headerId = headerId;
		this.receiptId = receiptId;
		this.forkFromId = forkFromId;
		this.currentStepCode = currentStepCode;
		this.nextStepCode = nextStepCode;
	}


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
	 * @return the typeId
	 */
	public Integer getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return the headerId
	 */
	public Integer getHeaderId() {
		return headerId;
	}

	/**
	 * @param headerId the headerId to set
	 */
	public void setHeaderId(Integer headerId) {
		this.headerId = headerId;
	}

	/**
	 * @return the receiptId
	 */
	public Long getReceiptId() {
		return receiptId;
	}

	/**
	 * @param receiptId the receiptId to set
	 */
	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}

	/**
	 * @return the forkFromId
	 */
	public String getForkFromId() {
		return forkFromId;
	}

	/**
	 * @param forkFromId the forkFromId to set
	 */
	public void setForkFromId(String forkFromId) {
		this.forkFromId = forkFromId;
	}

	/**
	 * @return the currentStepCode
	 */
	public String getCurrentStepCode() {
		return currentStepCode;
	}

	/**
	 * @param currentStepCode the currentStepCode to set
	 */
	public void setCurrentStepCode(String currentStepCode) {
		this.currentStepCode = currentStepCode;
	}

	/**
	 * @return the nextStepCode
	 */
	public String getNextStepCode() {
		return nextStepCode;
	}

	/**
	 * @param nextStepCode the nextStepCode to set
	 */
	public void setNextStepCode(String nextStepCode) {
		this.nextStepCode = nextStepCode;
	}

}