package com.fijo.cores.admin.task.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.*;

import com.fijo.cores.utils.annotations.Unique;
import com.fijo.cores.model.GenericModel;
import com.fijo.cores.utils.annotations.Unique;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;


/**
 * Quartz2 CronTrigger uses "cron expressions", see
 * http://quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/crontrigger
 * Show some examples: 
 * "0 0 12 * * ?" 每天中午12点触发 
 * "0 15 10 ? * *" 每天上午10:15触发
 * "0 15 10 * * ?" 每天上午10:15触发 
 * "0 15 10 * * ? *" 每天上午10:15触发
 * "0 15 10 * * ? 2005" 2005年的每天上午10:15触发
 * "0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发
 * "0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发
 * "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发 "0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发
 * "0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发 
 * "0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发
 * 
 * "0 15 10 15 * ?" 每月15日上午10:15触发 (本项目主要参考该样例)
 * 
 * "0 15 10 L * ?" 每月最后一日的上午10:15触发 
 * "0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发
 * "0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发 
 * "0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发
 * 
 * Expression	Means
 * 0 0 12 * * ?	Fire at 12:00 PM (noon) every day
 * 0 15 10 ? * *	Fire at 10:15 AM every day
 * 0 15 10 * * ?	Fire at 10:15 AM every day
 * 0 15 10 * * ? *	Fire at 10:15 AM every day
 * 0 15 10 * * ? 2005	Fire at 10:15 AM every day during the year 2005
 * 0 * 14 * * ?	Fire every minute starting at 2:00 PM and ending at 2:59 PM, every day
 * 0 0/5 14 * * ?	Fire every 5 minutes starting at 2:00 PM and ending at 2:55 PM, every day
 * 0 0/5 14,18 * * ?	Fire every 5 minutes starting at 2:00 PM and ending at 2:55 PM, AND fire every 5 minutes starting at 6:00 PM and ending at 6:55 PM, every day
 * 0 0-5 14 * * ?	Fire every minute starting at 2:00 PM and ending at 2:05 PM, every day
 * 0 10,44 14 ? 3 WED	Fire at 2:10 PM and at 2:44 PM every Wednesday in the month of March
 * 0 15 10 ? * MON-FRI	Fire at 10:15 AM every Monday, Tuesday, Wednesday, Thursday and Friday
 * 0 15 10 15 * ?	Fire at 10:15 AM on the 15th day of every month
 * 0 15 10 L * ?	Fire at 10:15 AM on the last day of every month
 * 0 15 10 ? * 6L	Fire at 10:15 AM on the last Friday of every month
 * 0 15 10 ? * 6L	Fire at 10:15 AM on the last Friday of every month
 * 0 15 10 ? * 6L 2002-2005	Fire at 10:15 AM on every last friday of every month during the years 2002, 2003, 2004, and 2005
 * 0 15 10 ? * 6#3	Fire at 10:15 AM on the third Friday of every month
 * 0 0 12 1/5 * ?	Fire at 12 PM (noon) every 5 days every month, starting on the first day of the month
 * 0 11 11 11 11 ?	Fire every November 11 at 11:11 AM
 */
@Entity
@Table(name = "sys_task_trigger_def")
@ApiModel
public class TaskTriggerDefinition extends GenericModel<TaskTriggerDefinition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1670896687189702800L;

	@Id
    @SequenceGenerator(name="sys_task_trigger_def_seq", sequenceName="sys_task_trigger_def_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="sys_task_trigger_def_seq")
    @ApiModelProperty(value="主键Id")
	private Integer id;

	@Column(length = 100, nullable = false, unique = true)
	@Unique
    @ApiModelProperty(value="任务名称")
	private String jobname;

    @ApiModelProperty(value="月份")
	private String whenmonth;

    @ApiModelProperty(value="日期")
	private String whenday;

    @ApiModelProperty(value="小时")
	private String whenhour;

    @ApiModelProperty(value="分钟")
	private String whenminute;

    @ApiModelProperty(value="最大尝试次数")
	@Column(name = "maxFireTimes", nullable = true)
	private Integer maxFireTimes;
	
	public static List<String> MonthScope = Arrays.asList("1", "2", "3", "4",
			"5", "6", "7", "8", "9", "10", "11", "12", "*", "/");

	public static List<String> DayScope = Arrays.asList("1", "2", "3", "4",
			"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
			"17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
			"28", "29", "30", "31", "*", "/");

	public static List<String> HourScope = Arrays.asList("0", "1", "2", "3", "4",
			"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
			"17", "18", "19", "20", "21", "22", "23", "*", "/");

	public static List<String> MinuteScope = Arrays.asList("0", "1", "2", "3", "4",
			"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
			"17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
			"28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38",
			"39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
			"50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "*", "/");


	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the jobname
	 */
	public String getJobname() {
		return jobname;
	}

	/**
	 * @param jobname the jobname to set
	 */
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}

	/**
	 * @return the whenmonth
	 */
	public String getWhenmonth() {
		return whenmonth;
	}

	/**
	 * @param whenmonth the whenmonth to set
	 */
	public void setWhenmonth(String whenmonth) {
		this.whenmonth = whenmonth;
	}

	/**
	 * @return the whenday
	 */
	public String getWhenday() {
		return whenday;
	}

	/**
	 * @param whenday the whenday to set
	 */
	public void setWhenday(String whenday) {
		this.whenday = whenday;
	}

	/**
	 * @return the whenhour
	 */
	public String getWhenhour() {
		return whenhour;
	}

	/**
	 * @param whenhour the whenhour to set
	 */
	public void setWhenhour(String whenhour) {
		this.whenhour = whenhour;
	}

	/**
	 * @return the whenminute
	 */
	public String getWhenminute() {
		return whenminute;
	}

	/**
	 * @param whenminute the whenminute to set
	 */
	public void setWhenminute(String whenminute) {
		this.whenminute = whenminute;
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

}
