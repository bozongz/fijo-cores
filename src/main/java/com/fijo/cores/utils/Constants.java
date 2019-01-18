package com.fijo.cores.utils;

/**
 * 系统常量
 * 
 * @author zhangbo
 *
 */
public class Constants {
	public static final String CHARSET = "UTF-8";
	public static final String GB2312 = "GB2312";
    public static final String GBK = "GBK";
	public static final String ENCODE = "SHA-1";

	public static final String Linux="Linux";
	public static final String Windows="Windows";
	
	public final static int DEFAULT_QUERY_PAGESIZE = 100;
	public final static int DEFAULT_QUERY_STARTINDEX = 1;
	
	public final static String HTTPPROTOCAL = "http://";	
	public final static String HTTP = "http";
	public final static String HTTPS = "https";
	public final static String HTTPGET = "GET";
	public final static String HTTPPOST = "POST";
	public final static String COLON = ":";
	public final static String DOT = ".";
	public final static String COMMA = ",";
	public final static String STAR = "*";
	public final static String LINE = "-";
	public final static String SQUOTE = "'";
	public final static String MYSQL_SQUOTE = "`";
	public final static String SPACE = " ";
	public final static String UNDERLINE = "_";
	public final static String PERCENT = "%";
	public final static String EMPTY = "";
	public final static String NULL = "null";
	public final static String NEWLINE = "\n";
	public final static String LEFT_BRACKET="(";
	public final static String RIGHT_BRACKET=")";
	public final static String AND = "&";
	public final static String EQUAL = "=";
	public final static String SEPARATOR = System.getProperty("file.separator");
	public final static String SLASH = "/";
	public final static String VERTICAL = "|";
	
	public final static String CONTENT_TYPE = "Content-Type";
	public final static String OFFICE_WORD = ".doc";
	public final static String OFFICE_EXCEL = ".xls";
	public final static String OFFICE_PDF = ".pdf";
	public final static String OFFICE_SWF = ".swf";
	
	public final static String OFFICE_HOME = "OFFICE_HOME";
	public final static int OFFICE_PORT=8484;
	public final static String  OFFICE_CONNECTED_FAILED="连接OpenOffice失败，请查找8484占用端口，并尝试重启OpenOffice!";
	public final static String  OFFICE_SWFTOOLS_HOME = "SWFTOOLS_HOME";
	
	public final static String UNKNOW_ERROR = "发生未知异常，强制退出！";
	public final static String UPLOAD_MAX = "上传的文件太大，文件大小不能超过500KB！";
	public final static String UPLOAD_SUCCESS = "上传成功！";
	public final static String UPLOAD_FAILED = "上传失败！";
	public final static String FILE_EXIST = "文件已经存在，请勿重新上传！";
	
	//微信相关系统常量
	public final static String SIGNATURE = "signature";
	public final static String TIMESTAMP = "timestamp";
	public final static String NONCE = "nonce";
	public final static String ECHOSTR = "echostr";
	public final static String FromUserName = "FromUserName";
	public final static String ToUserName = "ToUserName";
	public final static String Content = "Content";
	public final static String CreateTime = "CreateTime";
	public final static String MsgType="MsgType";
	public final static String Recognition = "Recognition";
	public final static String EVENT = "Event";
	public final static String EventKey = "EventKey";
	// 地理位置维度   
	public final static String Latitude = "Latitude";  
    // 地理位置经度   
	public final static String Longitude = "Longitude";  
	public final static String EVENT_SUBSCRIBE_THANKS ="谢谢您的关注!";
	public final static String UNKNOWN_MSG_TYPE = "未知的消息类型！";
}
