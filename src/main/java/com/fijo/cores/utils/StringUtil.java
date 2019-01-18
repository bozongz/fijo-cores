package com.fijo.cores.utils;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.fijo.cores.app.model.FileUploader;
import com.fijo.cores.app.model.ProcessStep;

public class StringUtil {

	public static void main(String[] args) throws IOException {
//		URL url = StringUtil.class.getClassLoader().getResource(
//				"opensimbest.properties");
//		File file = new File(url.getPath());
//		@SuppressWarnings("unchecked")
//		List<String> lines = FileUtils.readLines(file, Constants.CHARSET);
//		for (String line : lines) {
//			System.out.println(UnicodeToCH(line));
//		}
		System.out.println(filterEmoji("张宇儿💋"));
		ProcessStep step = new ProcessStep();
		FileUploader f = new FileUploader();
		Object[] arr = new Object[]{step, f};
		String str = arrayToDelimitedString(arr, ",");
		System.out.println(str);
	}

	 /**
     * 过滤emoji 或者 其他非文字类型的字符
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        if(StringUtils.isNotEmpty(source)){  
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");  
        }else{  
            return source;  
        }  
    } 
    
	

	public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (arr == null || arr.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}
	
	/**
	 * 返回字符串的右边的count个字符
	 * 
	 * @param str
	 *            原字符串
	 * @param count
	 *            取的字符个数
	 * @return 截取的字符串
	 */
	public static String getRight(String str, int count) {
		return str.substring(str.length() - count, str.length());
	}

	/**
	 * 返回字符串的左边的count个字符
	 * 
	 * @param str
	 *            原字符串
	 * @param count
	 *            取的字符个数
	 * @return 截取的字符串
	 */
	public static String getLeft(String str, int count) {
		return str.substring(0, count);
	}

	/**
	 * 补0
	 * 
	 * @param str
	 *            参数：9
	 * @param len
	 *            参数：4
	 * @return 返回：0009
	 */
	public static String leadZeros(String str, int len) {
		if (str == null || str.length() == 0) {
			str = "";
			for (int i = 0; i < len; i++) {
				str += "0";
			}
			return str;
		} else {
			str = str.trim();
			int strLen = str.length();
			for (int i = 0; i < len - strLen; i++) {
				str = "0" + str;
			}
			return str;
		}
	}

	/**
	 * 以提供的字符往后补位
	 * 
	 * @param str
	 *            参数：9
	 * @param len
	 *            参数：4
	 * @return 返回：9000
	 */
	public static String endPadding(String str, char c, int len) {
		if (str == null || str.length() == 0) {
			str = "";
			for (int i = 0; i < len; i++) {
				str += c;
			}
			return str;
		} else {
			str = str.trim();
			int strLen = str.length();
			for (int i = 0; i < len - strLen; i++) {
				str += c;
			}
			return str;
		}
	}

	/**
	 * 降序排序字符串数组
	 * 
	 * @param strs
	 *            An array of String
	 * @return String[] the sorted array
	 */
	public static String[] sort(String[] strs) {
		int i = 0, j = 1, len = strs.length;
		if (len <= 1)
			return strs;
		String strTmp = null;
		for (i = 0; i < len - 1; i++) {
			for (j = i + 1; j < len; j++) {
				if (strs[i].compareTo(strs[j]) < 0) {
					strTmp = strs[i];
					strs[i] = strs[j];
					strs[j] = strTmp;
				}
			}
		}
		return strs;
	}

	/**
	 * 将字符传前补指定字符 c 到指定长度 length, 如将abcd以*号替换成8位的字符串,即****abcd
	 * 
	 * @param str
	 * @param length
	 * @param c
	 * @return String ****abcd
	 */
	public static String padding(String str, int length, char c) {
		int i, len;
		len = length - str.length();

		for (i = 0; i < len; i++) {
			str = c + str;
		}
		return str;
	}

	public static String firstCharToUpperCase(String srcStr) {
		return srcStr.substring(0, 1).toUpperCase().concat(srcStr.substring(1));
	}

	/**
	 * 替换特殊字符
	 * 
	 * @param inStr
	 * @return String
	 */
	public static String replaceSpecialChars(String inStr) {
		String outStr = "";
		if (inStr == null) {
			return outStr;
		} else {
			String[] replaceStr = { "\"", "\'" };
			String[] toStr = { "“", "‘" };
			for (int i = 0; i < replaceStr.length; i++) {
				if (i != 0) {
					inStr = outStr;
				}
				outStr = inStr.replaceAll(replaceStr[i], toStr[i]);
			}
			outStr = replaceMark(outStr, "\\", "＼");
			return outStr;
		}
	}

	/**
	 * 全局替换
	 * 
	 * @param str
	 *            用来替换的字符串
	 * @param destStr
	 *            需要被查找替换的字符串
	 * @param srcStr
	 *            需要进行此操作的源字符串
	 * @return String
	 */
	public static String replaceMark(String str, String destStr, String srcStr) {
		// 返回值
		StringBuffer retVal = new StringBuffer();
		// 记录查找到相似字符的位置
		int findStation = str.indexOf(destStr);
		int resumStation = 0;
		while (findStation > -1) {
			retVal.append(str.substring(resumStation, findStation));
			retVal.append(srcStr);
			resumStation = findStation + destStr.length();
			findStation = str.indexOf(destStr, resumStation);
		}
		retVal.append(str.substring(resumStation));
		return retVal.toString();
	}

	/**
	 * 数组转换成字符串
	 *
	 * @param strs
	 *            An array of String
	 * @return String
	 */
	public static String getString(String[] strs) {
		String strTmp = "";
		if (strs == null) {
			return "";
		}
		for (int i = 0; i < strs.length; i++) {
			if (!strs[i].equals("")) {
				strTmp += strs[i] + ",";
			}
		}
		strTmp = strTmp.substring(0, strTmp.length() - 1);

		return strTmp;
	}


	private final static String[] hex = { "00", "01", "02", "03", "04", "05",
			"06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", "10",
			"11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B",
			"1C", "1D", "1E", "1F", "20", "21", "22", "23", "24", "25", "26",
			"27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F", "30", "31",
			"32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C",
			"3D", "3E", "3F", "40", "41", "42", "43", "44", "45", "46", "47",
			"48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52",
			"53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D",
			"5E", "5F", "60", "61", "62", "63", "64", "65", "66", "67", "68",
			"69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73",
			"74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E",
			"7F", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
			"8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94",
			"95", "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F",
			"A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA",
			"AB", "AC", "AD", "AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5",
			"B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF", "C0",
			"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB",
			"CC", "CD", "CE", "CF", "D0", "D1", "D2", "D3", "D4", "D5", "D6",
			"D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1",
			"E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC",
			"ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7",
			"F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF" };
	private final static byte[] val = { 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x00, 0x01,
			0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F };

	/**
	 * 模拟Javascript escape函数
	 */
	public static String escape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ch = s.charAt(i);
			if (ch == ' ') {// space : map to '+'
				sbuf.append('+');
			} else if ('A' <= ch && ch <= 'Z') {// 'A'..'Z' : as it was
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') {// 'a'..'z' : as it was
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') {// '0'..'9' : as it was
				sbuf.append((char) ch);
			} else if (ch == '-'
					|| ch == '_' // unreserved : as it was
					|| ch == '.' || ch == '!' || ch == '~' || ch == '*'
					|| ch == '\'' || ch == '(' || ch == ')') {
				sbuf.append((char) ch);
			} else if (ch <= 0x007F) {// other ASCII : map to %XX
				sbuf.append('%');
				sbuf.append(hex[ch]);
			} else {// unicode : map to %uXXXX
				sbuf.append('%');
				sbuf.append('u');
				sbuf.append(hex[(ch >>> 8)]);
				sbuf.append(hex[(0x00FF & ch)]);
			}
		}
		return sbuf.toString();
	}

	/**
	 * 模拟Javascript unescape函数
	 */
	public static String unescape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int i = 0;
		int len = s.length();
		while (i < len) {
			int ch = s.charAt(i);
			if (ch == '+') {// + : map to ' '
				sbuf.append(' ');
			} else if ('A' <= ch && ch <= 'Z') {// 'A'..'Z' : as it was
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') {// 'a'..'z' : as it was
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') {// '0'..'9' : as it was
				sbuf.append((char) ch);
			} else if (ch == '-'
					|| ch == '_' // unreserved : as it was
					|| ch == '.' || ch == '!' || ch == '~' || ch == '*'
					|| ch == '\'' || ch == '(' || ch == ')') {
				sbuf.append((char) ch);
			} else if (ch == '%') {
				int cint = 0;
				if ('u' != s.charAt(i + 1)) { // %XX : map to ascii(XX)
					cint = (cint << 4) | val[s.charAt(i + 1)];
					cint = (cint << 4) | val[s.charAt(i + 2)];
					i += 2;
				} else {// %uXXXX : map to unicode(XXXX)
					cint = (cint << 4) | val[s.charAt(i + 2)];
					cint = (cint << 4) | val[s.charAt(i + 3)];
					cint = (cint << 4) | val[s.charAt(i + 4)];
					cint = (cint << 4) | val[s.charAt(i + 5)];
					i += 5;
				}
				sbuf.append((char) cint);
			}
			i++;
		}
		return sbuf.toString();
	}

}