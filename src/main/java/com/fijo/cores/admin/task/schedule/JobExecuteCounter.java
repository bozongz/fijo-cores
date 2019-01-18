/**
 * 
 */
package com.fijo.cores.admin.task.schedule;

/**
 * @author Li
 *
 */
public class JobExecuteCounter{
	
	private Integer maxFireTimes;

	private int fireCounter = 0;

	public JobExecuteCounter(Integer maxFireTimes, int fireCounter) {
		super();
		this.maxFireTimes = maxFireTimes;
		this.fireCounter = fireCounter;
	}

	/**
	 * @return the maxFireTimes
	 */
	public Integer getMaxFireTimes() {
		return maxFireTimes;
	}

	/**
	 * @param maxFireTimes the maxFireTimes to set
	 */
	public void setMaxFireTimes(Integer maxFireTimes) {
		this.maxFireTimes = maxFireTimes;
	}

	/**
	 * @return the fireCounter
	 */
	public int getFireCounter() {
		return fireCounter;
	}

	/**
	 * @param fireCounter the fireCounter to set
	 */
	public void setFireCounter(int fireCounter) {
		this.fireCounter = fireCounter;
	}

	
	
}
