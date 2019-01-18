package com.fijo.cores.model;

import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fijo.cores.exceptions.AppException;
import com.fijo.cores.utils.annotations.ProcessProperty;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@MappedSuperclass
@ApiModel
public abstract class SystemModel<T> extends GenericModel<T>{
    private static final long serialVersionUID = -1643479703949912244L;
	
	@Temporal(TemporalType.TIMESTAMP) 
	@Column(name = "createDate", nullable = false)
	@ProcessProperty
    @ApiModelProperty(value="创建时间")
	protected Date createDate;

	@Temporal(TemporalType.TIMESTAMP) 
	@Column(name = "updateDate")
	@ProcessProperty
    @ApiModelProperty(value="更新时间")
	protected Date updateDate;
	
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
	 * @return the updateDate
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	public Date getUpdateDate() {
		return updateDate;
	}
	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
   
	/**
	 * 最后更新日期越大，返回值越小，排序越靠前（参考物料集合查询排序）
	 */
	@Override
	public int compareTo(T obj) {			
		SystemModel<?> o = (SystemModel<?>)obj;
		// 最后更新日期 全部为空，以实体主键为准
		if(o.getUpdateDate() == null && this.getUpdateDate() == null){
			Field id = null;
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Id.class)) {
					id = field;
				}
			}
			return compareById(id, this, obj);
		}			
		else if(o.getUpdateDate() == null && this.getUpdateDate() != null)
			return -1;
		else if(this.getUpdateDate() == null && o.getUpdateDate() != null)
			return 1;
		else {
			int ret = this.getUpdateDate().compareTo(o.getUpdateDate())*-1; 
			return ret;
		}//日期越大，返回值越小
	}
	
	private int compareById(Field id, SystemModel<?> obj1, T obj){
		id.setAccessible(true);
		String typeStr = id.getGenericType().toString();
		try {
			switch(typeStr){
				case "class java.lang.Integer": 			
					return ((Integer)id.get(obj1)).compareTo(((Integer)id.get(obj)));			
				case "class java.lang.Long": 
					return ((Long)id.get(obj1)).compareTo(((Long)id.get(obj)));
				case "class java.lang.String": 
					return ((String)id.get(obj1)).compareTo(((String)id.get(obj)));
				default: throw new AppException("101", "Invalidate Type");
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new AppException("101", "Invalidate Type");
		}
	}
}
