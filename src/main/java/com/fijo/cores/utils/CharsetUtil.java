/**
 * 
 */
package com.fijo.cores.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * 字符集转换工具
 * 
 * @author zhangbo
 *
 */
public class CharsetUtil {
	public static void main(String[] args) {
		String str = "{\"aaaa\":\"中国花\"}";
		System.out.println(GBK2UTF8(str));
		System.out.println(UTF82GBK(str));
		System.out.println(UTF8ToUnicode(str));
		System.out.println(GBK2Unicode(str));
	}

	public static String GBK2UTF8(String gbk) {
		String l_temp = GBK2Unicode(gbk);
		l_temp = UnicodeToUTF8(l_temp);
		return l_temp;
	}

	public static String UTF82GBK(String utf) {
		String l_temp = UTF8ToUnicode(utf);
		l_temp = Unicode2GBK(l_temp);
		return l_temp;
	}

	public static String GBK2Unicode(String str) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char chr1 = (char) str.charAt(i);
			if (!isNeedConvert(chr1)) {
				result.append(chr1);
				continue;
			}
			result.append("\\u" + Integer.toHexString((int) chr1));
		}
		return result.toString();
	}

	public static String Unicode2GBK(String dataStr) {
		int index = 0;
		StringBuffer buffer = new StringBuffer();

		int li_len = dataStr.length();
		while (index < li_len) {
			if (index >= li_len - 1
					|| !"\\u".equals(dataStr.substring(index, index + 2))) {
				buffer.append(dataStr.charAt(index));

				index++;
				continue;
			}
			String charStr = "";
			charStr = dataStr.substring(index + 2, index + 6);

			char letter = (char) Integer.parseInt(charStr, 16);

			buffer.append(letter);
			index += 6;
		}

		return buffer.toString();
	}

	public static String UTF8ToUnicode(String inStr) {
		// char[] myBuffer = inStr.toCharArray();
		// StringBuffer sb = new StringBuffer();
		// for (int i = 0; i < inStr.length(); i++) {
		// UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
		// if (ub == UnicodeBlock.BASIC_LATIN) {
		// sb.append(myBuffer[i]);
		// } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
		// int j = (int) myBuffer[i] - 65248;
		// sb.append((char) j);
		// } else {
		// short s = (short) myBuffer[i];
		// s = 32079;
		// String hexS = Integer.toHexString(s);
		// String unicode = "\\u" + hexS;
		// sb.append(unicode.toLowerCase());
		// }
		// }
		// return sb.toString();
		char[] myBuffer = inStr.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < myBuffer.length; i++) {
			sb.append(unicodeEscaped(myBuffer[i]));
		}
		return sb.toString();
	}

	public static String UnicodeToUTF8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	private static boolean isNeedConvert(char para) {
		return ((para & (0x00FF)) != para);
	}

	private static String unicodeEscaped(char ch) {
		if (isChineseChar(ch)) {
			if (ch < 0x10) {
				return "\\u000" + Integer.toHexString(ch);
			} else if (ch < 0x100) {
				return "\\u00" + Integer.toHexString(ch);
			} else if (ch < 0x1000) {
				return "\\u0" + Integer.toHexString(ch);
			}
			return "\\u" + Integer.toHexString(ch);
		} else {
			return String.valueOf(ch);
		}
	}

	public static boolean isChineseChar(char c) {
		boolean temp = false;
		if ((c >= 0x4e00) && (c <= 0x9fbb)) {
			temp = true;
		}
		return temp;
	}

	public static String changeCharset(String str, String newCharset)
			throws UnsupportedEncodingException {
		if (str != null) {
			// 用默认字符编码解码字符串。
			byte[] bs = str.getBytes();
			// 用新的字符编码生成字符串
			return new String(bs, newCharset);
		}
		return null;
	}

	public static String changeCharset(String str, String oldCharset, String newCharset)
			throws UnsupportedEncodingException {
		if (str != null) {
			// 用旧的字符编码解码字符串。解码可能会出现异常。
			byte[] bs = str.getBytes(oldCharset);
			// 用新的字符编码生成字符串
			return new String(bs, newCharset);
		}
		return null;
	}

	public static String getStringCharset(String str) throws IOException {
		return getInputStreamCharset(new ByteArrayInputStream(str.getBytes()));
	}
	
	public static String getInputStreamCharset(InputStream is) throws IOException {
		UniversalDetector detector = new UniversalDetector(null);
		byte[] buf = new byte[4096];
		int nread;
		while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		detector.dataEnd();
		return detector.getDetectedCharset();
	}

	public static String getFileCharset(File file) throws IOException {
		byte[] buf = new byte[4096];
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(file));
		final UniversalDetector universalDetector = new UniversalDetector(null);
		int numberOfBytesRead;
		while ((numberOfBytesRead = bufferedInputStream.read(buf)) > 0
				&& !universalDetector.isDone()) {
			universalDetector.handleData(buf, 0, numberOfBytesRead);
		}
		universalDetector.dataEnd();
		String encoding = universalDetector.getDetectedCharset();
		universalDetector.reset();
		bufferedInputStream.close();
		return encoding;
	}
}
