package com.fijo.cores.utils.pages;

import java.util.List;

import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.Constants;

/**
 * 
 * @author zhangbo
 *
 * 通用关系型数据库分页帮助类
 */
public class PageSupport<T> {

	private List<T> items;

	private int totalRecords; 

	private int startIndex; 

	private int totalPages;

	private int currentPage;

	private int pageSize;	


	/**
	 * @param items
	 * @param totalRecords
	 */
	public PageSupport(List<T> items, int totalRecords) {
		this(items, totalRecords, Constants.DEFAULT_QUERY_STARTINDEX, Constants.DEFAULT_QUERY_PAGESIZE);
	}

	/**
	 * @param items
	 * @param totalRecords
	 * @param startIndex
	 */
	public PageSupport(List<T> items, int totalRecords, int startIndex) {
		this(items, totalRecords, startIndex, Constants.DEFAULT_QUERY_PAGESIZE);
	}

	
	/**
	 * @param items
	 * @param totalRecords
	 * @param startIndex
	 * @param pageSize
	 */
	public PageSupport(List<T> items, int totalRecords, int startIndex,
			int pageSize) {
		this.items = items;
		this.totalRecords = totalRecords;
		this.startIndex = startIndex;
		this.pageSize = pageSize;
		setTotalPages();
		setCurrentPage();
	}

	/**
	 * @return the items
	 */
	public List<T> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<T> items) {
		this.items = items;
	}

	/**
	 * @return  the totalRecords
	 */
	public int getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @param totalRecords  the totalRecords to set
	 */
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * @return  the totalPages
	 */
	public int getTotalPages() {
		return totalPages;
	}

	/**
	 * Calculate By totalRecords and pageSize
	 */
	public void setTotalPages() {
		if (getTotalRecords() > 0) {
			this.totalPages = totalRecords / pageSize;
			if (totalRecords % pageSize > 0)
				this.totalPages++;
		} else{
			this.totalPages = 0;
		}
	}

	/**
	 * @return  the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * Calculate By startIndex and pageSize
	 */
	public void setCurrentPage() {
		if (getStartIndex() > 0) {
			this.currentPage = startIndex / pageSize;
			if (startIndex % pageSize > 0)
				this.currentPage++;
		} else{
			this.currentPage = 1;
		}
	}

	/**
	 * @return  the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize  the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return  the startIndex
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * @param startIndex  the startIndex to set
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

}
