package com.fijo.cores.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Properties;

/**
 *
 * @author zhangbo
 * @version 1.0
 */
public class TextUtil {
    /**
     * 判断一个字符是否为整数
     * @param value String
     * @return boolean
     */
    public static boolean isInteger(String value) {
        return (formatInt(value) != null);
    }

    /**
     * 判断是否为Double
     * @param value String
     * @return boolean
     */
    public static boolean isDouble(String value) {
        return (formatDouble(value) != null);
    }

    /**
     * 是否为电子邮件
     * @param value String
     * @return boolean
     */
    public static boolean isEmail(String value) {
        if (null == value || value.trim().length() == 0) {
            return false;
        }
        Pattern pa = Pattern.compile("^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[_.0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT|cn|CN|cc|CC)$");
        Matcher ma = pa.matcher(value);
        return ma.matches();
    }

    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 是否为电话
     * @param str String
     * @return boolean
     */
    public static boolean isPhone(String str) {
        Pattern p1 = null,p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if(str.length() >9)
        {   m = p1.matcher(str);
            b = m.matches();
        }else{
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }


    /**
     * 把一个字符转换成Double类
     * @param value String
     * @return Double
     */
    public static Double formatDouble(String value) {
        try {
            return new Double(value);
        } catch(Exception e) {
            return null;
        }
    }
    /**
     * 把一个Double转换成整数
     * @param value String
     * @return Integer
     */
    public static Integer formatInt(Double value) {
        try {
            return value.intValue();
        } catch(Exception e) {
            return null;
        }

    }
    /**
     * 把一个字符转换成整数类
     * @param value String
     * @return Integer
     */
    public static Integer formatInt(String value) {
        try {
            return new Integer(value);
        } catch(Exception e) {
            return null;
        }

    }
   /**
     * 把一个字符转换成整数类
     * @param value String
     * @return Integer
     */
    public static Long formatLong(String value) {
        try {
            return new Long(value);
        } catch(Exception e) {
            return null;
        }

    }

    /**
     *  将 str 转换成 整数 默认 inc
     * @param str
     * @param inc
     * @return  Integer
     */
    public static Integer formatInt(String str,int inc){
        Integer reVal = formatInt(str);
        if(reVal==null){
            reVal =new Integer(inc);
        }
        return reVal;
    }
    public static Double formatDouble(String str,double inc) {
        Double   reVal = formatDouble(str);
        if(reVal==null){
            reVal =new Double(inc);
        }
        return reVal;
    }
    /**
     * 输入的校验日期是否合法，日期字符格串式必须为"yyyy-MM-dd"
     * @param date   2005-12-13
     * 需要校验的日期
     */
    public static boolean isDate(String date) {
        if (date == null || date.length() != 10) {
            return false;
        } else {
            String dates[] = date.split("-");
            if (dates.length != 3)
                return false;
            int yy = TextUtil.formatInt(dates[0],0).intValue();
            int mm = TextUtil.formatInt(dates[1],0).intValue();
            int dd = TextUtil.formatInt(dates[2],0).intValue();
            if (yy > 3000 || yy < 1000) {
                return false;
            }
            if (mm > 12 || mm < 1) {
                return false;
            }
            switch (mm) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    if(dd > 31 || dd < 1) {
                        return false;
                    } else {
                        return true;
                    }
                case 2:
                    if((yy % 4 == 0 && yy % 100 != 0) || yy % 400 == 0) {
                        if(dd > 29 || dd < 1) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        if(dd > 28 || dd < 1) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                default:
                    if(dd > 30 || dd < 1) {
                        return false;
                    } else {
                        return true;
                    }
            }
        }
    }
    /**
     * 判断是否为时间
     * @param value 格式：HH:mm:ss
     * @return boolean
     */
    public static boolean isTime(String value) {
        String[] tmp = value.split(":");
        if(tmp.length !=3 )
            return false;
        int h = formatInt(tmp[0],-1).intValue();
        int m = formatInt(tmp[1],-1).intValue();
        int s = formatInt(tmp[2],-1).intValue();
        if(h >= 24 || h < 0)
            return false;
        if(m >= 60 || h < 0)
            return false;
        if(s >= 60 || h < 0)
            return false;

        return true;
    }
    /**
     * 判断是否为日期时间
     * @param value 格式： yyyy-MM-dd HH:mm:ss
     * @return boolean
     */
    public static boolean isDateTime(String value) {
        String tmp[] = value.split(" ");
        if (tmp.length !=2)
            return false;
        if (!isDate(tmp[0]))
            return false;
        if (!isTime(tmp[1]))
            return false;
        return true;
    }
    /**
     * 获取操作系统目录分割符：unix ：/  windows : \\
     * 通过获得系统属性构造属性类 prop
     */
    public static String getFileSeparator(){
    Properties prop = new Properties(System.getProperties());
    //在标准输出中输出系统属性的内容
    String _file = "/";
    if (!prop.getProperty("file.separator").equals("/")) {
        _file = "\\";
    }
    return _file;
    }

    /**
	 * 字符串是否空或者NULL
	 * 
	 * @param str
	 *            strig to check
	 * @return the check result
	 */
	public static boolean blankOrNull(String str)
	{
		boolean rlt = true;

		if (str != null)
		{
			if (str.trim().length() > 0)
			{
				rlt = false;
			}
		}

		return rlt;
	}
}
