package com.fijo.cores.model;

/**
 * zjs 返回数据列表
 * 
 * @author zhangbo
 *
 */
public class ZjsListData<T> extends BaseObject<ZjsListData<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2377444812320347069L;

	private Integer totalPages;// 记录总页数

	private Integer totalRows; // 记录总数

	private T[] datas;

	/**
	 * @return the totalPages
	 */
	public Integer getTotalPages() {
		return totalPages;
	}

	/**
	 * @param totalPages the totalPages to set
	 */
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * @return the totalRows
	 */
	public Integer getTotalRows() {
		return totalRows;
	}

	/**
	 * @param totalRows the totalRows to set
	 */
	public void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows;
	}

	/**
	 * @return the datas
	 */
	public T[] getDatas() {
		return datas;
	}

	/**
	 * @param datas the datas to set
	 */
	public void setDatas(T[] datas) {
		this.datas = datas;
	}

}
