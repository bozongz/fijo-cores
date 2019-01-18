package com.fijo.cores.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.springframework.web.multipart.MultipartFile;

import com.fijo.cores.exceptions.Exceptions;

/**
 * 支持SHA-1/MD5消息摘要的工具类.
 * 
 * 返回ByteSource，可进一步被编码为Hex, Base64或UrlSafeBase64
 * 
 * @author zhangbo
 */
public class Digests {

	private static final String SHA1 = "SHA-1";
	private static final String MD5 = "MD5";

	private static SecureRandom random = new SecureRandom();

	public static void main(String[] args) {
		System.out.println(encryptSHA("1"));
		System.out.println(encryptSHA("A"));
		System.out.println(encryptSHA("zhangbo"));
		System.out.println(encryptSHA("李蜀毅"));
		System.out.println(encryptSHA("李"));
		System.out.println(encryptSHA("admin").length());
	}
	/**
	 * 对输入字符串进行sha1散列.
	 */
	public static byte[] sha1(byte[] input) {
		return digest(input, SHA1, null, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt) {
		return digest(input, SHA1, salt, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt, int iterations) {
		return digest(input, SHA1, salt, iterations);
	}

	/**
	 * 对字符串进行散列, 支持md5与sha1算法.
	 */
	private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			if (salt != null) {
				digest.update(salt);
			}

			byte[] result = digest.digest(input);

			for (int i = 1; i < iterations; i++) {
				digest.reset();
				result = digest.digest(result);
			}
			return result;
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 生成随机的Byte[]作为salt.
	 * 
	 * @param numBytes byte数组的大小
	 */
	public static byte[] generateSalt(int numBytes) {
		Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", numBytes);

		byte[] bytes = new byte[numBytes];
		random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * 对文件进行md5散列.
	 */
	public static byte[] md5(InputStream input) throws IOException {
		return digest(input, MD5);
	}

	/**
	 * 对文件进行sha1散列.
	 */
	public static byte[] sha1(InputStream input) throws IOException {
		return digest(input, SHA1);
	}

	private static byte[] digest(InputStream input, String algorithm) throws IOException {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			int bufferLength = 8 * 1024;
			byte[] buffer = new byte[bufferLength];
			int read = input.read(buffer, 0, bufferLength);

			while (read > -1) {
				messageDigest.update(buffer, 0, read);
				read = input.read(buffer, 0, bufferLength);
			}

			return messageDigest.digest();
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/** 
     * Encrypt string using MD5 algorithm 
     */  
    public final static String encryptMD5(String source) {  
        if (source == null) {  
            source = "";  
        }  
        String result = "";  
        try {  
            result = encrypt(source, MD5);  
        } catch (NoSuchAlgorithmException ex) {  
            // this should never happen  
            throw new RuntimeException(ex);  
        }  
        return result;  
    }  
    /** 
     * Encrypt string using SHA algorithm 
     */  
    public final static String encryptSHA(String source) {  
        if (source == null) {  
            source = "";  
        }  
        String result = "";  
        try {  
            result = encrypt(source, SHA1);  
        } catch (NoSuchAlgorithmException ex) {  
            // this should never happen  
            throw new RuntimeException(ex);  
        }  
        return result;  
    }  
    /** 
     * Encrypt string 
     */  
    private final static String encrypt(String source, String algorithm)  
            throws NoSuchAlgorithmException {  
        byte[] resByteArray = encrypt(source.getBytes(), algorithm);  
        return toHexString(resByteArray);  
    }  
    /** 
     * Encrypt byte array. 
     */  
    private final static byte[] encrypt(byte[] source, String algorithm)  
            throws NoSuchAlgorithmException {  
        MessageDigest md = MessageDigest.getInstance(algorithm);  
        md.reset();  
        md.update(source);  
        return md.digest();  
    }
    
    /** 
     * Get hex string from byte array 
     */  
    public final static String toHexString(final byte[] hash) {  
        StringBuffer sb = new StringBuffer(hash.length << 1);  
        for (int i = 0; i < hash.length; i++) {  
            String digit = Integer.toHexString(0xFF & hash[i]);  
            if (digit.length() == 1) {  
                digit = '0' + digit;  
            }  
            sb.append(digit);  
        }  
        return sb.toString();  
    } 

    public final static String getFileMd5(final File file) throws IOException{
    	FileInputStream fis= new FileInputStream(file);  
        String md5 = DigestUtils.md5Hex(IOUtils.toByteArray(fis));  
        IOUtils.closeQuietly(fis);  
        return md5;
    }
    
	public final static String getUploadFileMd5(MultipartFile upload){
		String md5 = null;
		try {
			byte[] uploadBytes = upload.getBytes();
		    md5 = DigestUtils.md5Hex(uploadBytes);
		} catch (IOException e) {
			Exceptions.printException(e);
		}
		if(md5 == null){
			throw new RuntimeException();
		}
	    return md5;
	}
	
//	public final static String getUploadFileMd5(MultipartFile upload) throws Exception {
//	    byte[] uploadBytes = upload.getBytes();
//	    MessageDigest md5 = MessageDigest.getInstance("MD5");
//	    byte[] digest = md5.digest(uploadBytes);
//	    String hashString = new BigInteger(1, digest).toString(16);
//	    return hashString.toUpperCase();
//	}
}

