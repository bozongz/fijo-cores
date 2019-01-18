package com.fijo.cores.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringEscapeUtils;

import com.fijo.cores.exceptions.Exceptions;
/**
 * 封装各种格式的编码解码工具类.
 * 
 * 1.Commons-Codec的 hex/base64 编码
 * 2.自制的base62 编码
 * 3.Commons-Lang的xml/html escape
 * 4.JDK提供的URLEncoder
 * 
 * @author zhangbo
 */
public class Encodes {
	public static void main(String[] args) {
		BigInteger base = new BigInteger("20151231");
		String aaa = encodeBigInteger(base,DICTIONARY_32);
		System.out.println(aaa);
		System.out.println(decodeBigInteger(aaa, DICTIONARY_32));
		System.out.println("-----------------------------------------");
		String test = "23293207C1_1";
		String e1 = encodeHex(test.getBytes());
		System.out.println(e1);
		byte[] d1 = decodeHex(e1);
		System.out.println(new String(d1));
		
		String e2 = encodeBase64(test.getBytes());
		System.out.println(e2);
		byte[] d2 = decodeBase64(e2);
		System.out.println(new String(d2));
	}
	
	
	private static final String DEFAULT_URL_ENCODING = "UTF-8";
	private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	/**
	 * contains hexadecimals 0-F only.
	 */
	public static final char[] DICTIONARY_16 = new char[] { '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', 'A', 'K', 'C', 'H', 'Y', 'W' };

	/**
	 * contains only alphanumerics, in capitals and excludes letters/numbers
	 * which can be confused, eg. 0 and O or L and I and 1.
	 */
	public static final char[] DICTIONARY_32 = new char[] { '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
			'Y', 'Z' };

	/**
	 * contains only alphanumerics, including both capitals and smalls.
	 */
	public static final char[] DICTIONARY_62 = new char[] { '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z' };

	/**
	 * contains alphanumerics, including both capitals and smalls, and the
	 * following special chars: +"@*#%&/|()=?'~[!]{}-_:.,; (you might not be
	 * able to read all those using a browser!
	 */
	public static final char[] DICTIONARY_89 = new char[] { '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', '+', '"', '@', '*', '#', '%', '&',
			'/', '|', '(', ')', '=', '?', '~', '[', ']', '{', '}', '$', '-',
			'_', '.', ':', ',', ';', '<', '>' };
	
	/**
	 * Hex编码.
	 */
	public static String encodeHex(byte[] input) {
		return Hex.encodeHexString(input);
	}

	/**
	 * Hex解码.
	 */
	public static byte[] decodeHex(String input) {
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (DecoderException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * Base64编码.
	 */
	public static String encodeBase64(byte[] input) {
		return Base64.encodeBase64String(input);
	}

	/**
	 * Base64编码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548).
	 */
	public static String encodeUrlSafeBase64(byte[] input) {
		return Base64.encodeBase64URLSafeString(input);
	}

	/**
	 * Base64解码.
	 */
	public static byte[] decodeBase64(String input) {
		return Base64.decodeBase64(input);
	}

	/**
	 * Base62编码。
	 */
	public static String encodeBase62(byte[] input) {
		char[] chars = new char[input.length];
		for (int i = 0; i < input.length; i++) {
			chars[i] = BASE62[((input[i] & 0xFF) % BASE62.length)];
		}
		return new String(chars);
	}

	/**
	 * Html 转码.
	 */
	public static String escapeHtml(String html) {
		return StringEscapeUtils.escapeHtml(html);		
	}

	/**
	 * Html 解码.
	 */
	public static String unescapeHtml(String htmlEscaped) {
		return StringEscapeUtils.unescapeHtml(htmlEscaped);
	}

	/**
	 * Xml 转码.
	 */
	public static String escapeXml(String xml) {
		return StringEscapeUtils.escapeXml(xml);
	}

	/**
	 * Xml 解码.
	 */
	public static String unescapeXml(String xmlEscaped) {
		return StringEscapeUtils.unescapeXml(xmlEscaped);
	}

	/**
	 * URL 编码, Encode默认为UTF-8.
	 */
	public static String urlEncode(String part) {
		try {
			return URLEncoder.encode(part, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	public static String urlEncode(String part, String urlEncoding) {
		try {
			return URLEncoder.encode(part, urlEncoding);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}
	
	/**
	 * URL 解码, Encode默认为UTF-8.
	 */
	public static String urlDecode(String part) {

		try {
			return URLDecoder.decode(part, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}
	
	public static String urlDecode(String part, String urlEncoding) {

		try {
			return URLDecoder.decode(part, urlEncoding);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}
	
	/**
	 * encodes the given string into the base of the dictionary provided in the
	 * constructor.
	 * 
	 * @param value
	 *            the number to encode.
	 * @return the encoded string.
	 */
	public static String encodeBigInteger(BigInteger value, char[] dictionary) {

		List<Character> result = new ArrayList<Character>();
		BigInteger base = new BigInteger("" + dictionary.length);
		int exponent = 1;
		BigInteger remaining = value;
		while (true) {
			BigInteger a = base.pow(exponent); // 16^1 = 16
			BigInteger b = remaining.mod(a); // 119 % 16 = 7 | 112 % 256 = 112
			BigInteger c = base.pow(exponent - 1);
			BigInteger d = b.divide(c);

			// if d > dictionary.length, we have a problem. but BigInteger
			// doesnt have
			// a greater than method :-( hope for the best. theoretically, d is
			// always
			// an index of the dictionary!
			result.add(dictionary[d.intValue()]);
			remaining = remaining.subtract(b); // 119 - 7 = 112 | 112 - 112 = 0

			// finished?
			if (remaining.equals(BigInteger.ZERO)) {
				break;
			}

			exponent++;
		}

		// need to reverse it, since the start of the list contains the least
		// significant values
		StringBuffer sb = new StringBuffer();
		for (int i = result.size() - 1; i >= 0; i--) {
			sb.append(result.get(i));
		}
		return sb.toString();
	}

	/**
	 * decodes the given string from the base of the dictionary provided in the
	 * constructor.
	 * 
	 * @param str
	 *            the string to decode.
	 * @return the decoded number.
	 */
	public static BigInteger decodeBigInteger(String str, char[] dictionary) {

		// reverse it, coz its already reversed!
		char[] chars = new char[str.length()];
		str.getChars(0, str.length(), chars, 0);

		char[] chars2 = new char[str.length()];
		int i = chars2.length - 1;
		for (char c : chars) {
			chars2[i--] = c;
		}

		// for efficiency, make a map
		Map<Character, BigInteger> dictMap = new HashMap<Character, BigInteger>();
		int j = 0;
		for (char c : dictionary) {
			dictMap.put(c, new BigInteger("" + j++));
		}

		BigInteger bi = BigInteger.ZERO;
		BigInteger base = new BigInteger("" + dictionary.length);
		int exponent = 0;
		for (char c : chars2) {
			BigInteger a = dictMap.get(c);
			BigInteger b = base.pow(exponent).multiply(a);
			bi = bi.add(new BigInteger("" + b));
			exponent++;
		}

		return bi;

	}
}