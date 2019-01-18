package com.fijo.cores.utils.office;

import com.fijo.cores.utils.AppFileUtils;
import com.fijo.cores.utils.Constants;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.google.common.collect.Lists;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.AppFileUtils;
import com.fijo.cores.utils.Constants;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 
 * ZIP压缩文件操作工具类 
 * 支持密码 
 * 依赖zip4j开源项目(http://www.lingala.net/zip4j/) 
 * 版本1.3.1 
 * @author ninemax 
 */  
public class CompressUtil {
    private static transient final org.apache.commons.logging.Log log = LogFactory.getLog(CompressUtil.class);


    public static void main(String[] args) throws ZipException, IOException {
        String defaultCharacterEncoding = System.getProperty("file.encoding");
        System.out.println("defaultCharacterEncoding by code: " + System.getProperty("file.encoding"));
        System.out.println("defaultCharacterEncoding by charSet: " + Charset.defaultCharset());
        System.setProperty("file.encoding", "UTF-16");
        System.out.println("defaultCharacterEncoding by property after updating file.encoding : " + System.getProperty("file.encoding"));
        unzip("E:\\01_Work\\01_DevSpace\\worksapce\\web-apps\\maip\\target\\maip\\budget\\测试数据.zip", "E:\\", null);
    }



    private static File[] unrar(File rarFile, String dest) throws IOException{
        List<File> fileList = Lists.newArrayList();
        Archive a=null;
        try {
            a=new Archive(new FileVolumeManager(rarFile));
        }
        catch (RarException | IOException e) {
            e.printStackTrace();
        }
        if (a != null) {
            a.getMainHeader().print();
            com.github.junrar.rarfile.FileHeader fh=a.nextFileHeader();
            File destDir = new File(dest);
            FileUtils.forceMkdir(destDir);
            while (fh != null) {
                try {
                    File out=new File(dest + fh.getFileNameString().trim());
                    FileOutputStream os=new FileOutputStream(out);
                    a.extractFile(fh,os);
                    os.close();
                    fileList.add(out);
                    log.debug("File to be extracted....." + out.getAbsolutePath());
                }
                catch (RarException |IOException e) {
                    e.printStackTrace();
                }
                fh=a.nextFileHeader();
            }
        }
        return fileList.toArray(new File[]{});
    }

    /**
     * 使用给定密码解压指定的ZIP压缩文件到指定目录 
     * <p> 
     * 如果指定目录不存在,可以自动创建,不合法的路径将导致异常被抛出 
     * @param zip 指定的ZIP压缩文件 
     * @param dest 解压目录 
     * @param passwd ZIP文件的密码 
     * @return 解压后文件数组 
     * @throws ZipException 压缩文件有损坏或者解压缩失败抛出 
     * @throws IOException 
     */  
    public static File [] unzip(String zip, String dest, String passwd) throws ZipException, IOException {
        File zipFile = new File(zip);
        return unzip(zipFile, dest, passwd);
    }

    /**
     * 使用给定密码解压指定的ZIP压缩文件到当前目录
     * @param zip 指定的ZIP压缩文件
     * @param passwd ZIP文件的密码
     * @return  解压后文件数组
     * @throws ZipException 压缩文件有损坏或者解压缩失败抛出
     * @throws IOException
     */
    public static File [] unzip(String zip, String passwd) throws ZipException, IOException {
        File zipFile = new File(zip);  
        File parentDir = zipFile.getParentFile();  
        return unzip(zipFile, parentDir.getAbsolutePath(), passwd);  
    }

    /** 
     * 使用给定密码解压指定的ZIP压缩文件到指定目录 
     * <p> 
     * 如果指定目录不存在,可以自动创建,不合法的路径将导致异常被抛出 
     * @param zip 指定的ZIP压缩文件 
     * @param dest 解压目录 
     * @param passwd ZIP文件的密码 
     * @return  解压后文件数组 
     * @throws ZipException 压缩文件有损坏或者解压缩失败抛出 
     * @throws IOException 
     */  
    public static File [] unzip(File zipFile, String dest, String passwd) throws ZipException, IOException {
        if(StringUtils.isEmpty(dest))
            throw new ZipException("@@@@Error: Unzip destination can not be empty!!!!");
        if(zipFile == null || !zipFile.exists())
            throw new ZipException("@@@@Error: Unzip file not exist!!!!");
        if(AppFileUtils.getContentType(zipFile.getName()).equals("rar")){
            return unrar(zipFile,dest);
        } else {
            File destDir = new File(dest);
            FileUtils.forceMkdir(destDir);
            ZipFile zFile = new ZipFile(zipFile);
            zFile.setFileNameCharset(Constants.GBK); //不可随意更换位置
            if (!zFile.isValidZipFile()) {
                throw new ZipException(String.format("@@@@Error: Unzip file: %s is broken!!!!",zipFile.getAbsolutePath()));
            }
            if (zFile.isEncrypted()) {
                zFile.setPassword(passwd.toCharArray());
            }
            zFile.extractAll(dest);

            @SuppressWarnings("unchecked")
            List<FileHeader> headerList = zFile.getFileHeaders();
            List<File> extractedFileList = new ArrayList<File>();
            for (FileHeader fileHeader : headerList) {
                if (!fileHeader.isDirectory()) {
                    File out = new File(destDir, fileHeader.getFileName());
                    extractedFileList.add(out);
                    log.debug("File to be extracted....." + out.getAbsolutePath());
                }
            }
            File[] extractedFiles = new File[extractedFileList.size()];
            extractedFileList.toArray(extractedFiles);
            return extractedFiles;
        }
    }  
      
    /** 
     * 压缩指定文件到当前文件夹 
     * @param src 要压缩的指定文件 
     * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败. 
     */  
    public static String zip(String src) {  
        return zip(src,null);  
    }  
      
    /** 
     * 使用给定密码压缩指定文件或文件夹到当前目录 
     * @param src 要压缩的文件 
     * @param passwd 压缩使用的密码 
     * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败. 
     */  
    public static String zip(String src, String passwd) {  
        return zip(src, null, passwd);  
    }  
      
    /** 
     * 使用给定密码压缩指定文件或文件夹到当前目录 
     * @param src 要压缩的文件 
     * @param dest 压缩文件存放路径 
     * @param passwd 压缩使用的密码 
     * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败. 
     */  
    public static String zip(String src, String dest, String passwd) {  
        return zip(src, dest, true, passwd);  
    }  
      
    /** 
     * 使用给定密码压缩指定文件或文件夹到指定位置. 
     * <p> 
     * dest可传最终压缩文件存放的绝对路径,也可以传存放目录,也可以传null或者"".<br /> 
     * 如果传null或者""则将压缩文件存放在当前目录,即跟源文件同目录,压缩文件名取源文件名,以.zip为后缀;<br /> 
     * 如果以路径分隔符(File.separator)结尾,则视为目录,压缩文件名取源文件名,以.zip为后缀,否则视为文件名. 
     * @param src 要压缩的文件或文件夹路径 
     * @param dest 压缩文件存放路径 
     * @param isCreateDir 是否在压缩文件里创建目录,仅在压缩文件为目录时有效.<br /> 
     * 如果为false,将直接压缩目录下文件到压缩文件. 
     * @param passwd 压缩使用的密码 
     * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败. 
     */  
    public static String zip(String src, String dest, boolean isCreateDir, String passwd) {  
        File srcFile = new File(src);  
        dest = buildDestinationZipFilePath(srcFile, dest);  
        ZipParameters parameters = new ZipParameters();  
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);           // 压缩方式  
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);    // 压缩级别  
        if (!StringUtils.isEmpty(passwd)) {
            parameters.setEncryptFiles(true);  
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); // 加密方式  
            parameters.setPassword(passwd.toCharArray());  
        }  
        try {  
            ZipFile zipFile = new ZipFile(dest);
            zipFile.setFileNameCharset(Constants.GBK); //不可随意更换位置
            if (srcFile.isDirectory()) {  
                // 如果不创建目录的话,将直接把给定目录下的文件压缩到压缩文件,即没有目录结构  
                if (!isCreateDir) {  
                    File [] subFiles = srcFile.listFiles();  
                    ArrayList<File> temp = new ArrayList<File>();
                    if(null != subFiles && subFiles.length > 0)
                        Collections.addAll(temp, subFiles);
                    zipFile.addFiles(temp, parameters);  
                    return dest;  
                }  
                zipFile.addFolder(srcFile, parameters);  
            } else {  
                zipFile.addFile(srcFile, parameters);  
            }  
            return dest;  
        } catch (ZipException e) {  
            Exceptions.printException(e);;  
        }  
        return null;  
    }  
      
    /** 
     * 构建压缩文件存放路径,如果不存在将会创建 
     * 传入的可能是文件名或者目录,也可能不传,此方法用以转换最终压缩文件的存放路径 
     * @param srcFile 源文件 
     * @param destParam 压缩目标路径 
     * @return 正确的压缩文件存放路径 
     */  
    private static String buildDestinationZipFilePath(File srcFile,String destParam) {  
        if (StringUtils.isEmpty(destParam)) {  
            if (srcFile.isDirectory()) {  
                destParam = srcFile.getParent() + File.separator + srcFile.getName() + ".zip";  
            } else {  
                String fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));  
                destParam = srcFile.getParent() + File.separator + fileName + ".zip";  
            }  
        } else {  
            createDestDirectoryIfNecessary(destParam);  // 在指定路径不存在的情况下将其创建出来  
            if (destParam.endsWith(File.separator)) {  
                String fileName = "";  
                if (srcFile.isDirectory()) {  
                    fileName = srcFile.getName();  
                } else {  
                    fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));  
                }  
                destParam += fileName + ".zip";  
            }  
        }  
        return destParam;  
    }  
      
    /** 
     * 在必要的情况下创建压缩文件存放目录,比如指定的存放路径并没有被创建 
     * @param destParam 指定的存放路径,有可能该路径并没有被创建 
     */  
    private static void createDestDirectoryIfNecessary(String destParam) {  
        File destDir = null;  
        if (destParam.endsWith(File.separator)) {  
            destDir = new File(destParam);  
        } else {  
            destDir = new File(destParam.substring(0, destParam.lastIndexOf(File.separator)));  
        }
        if (!destDir.exists()) {
            boolean created = destDir.mkdirs();
            log.debug(created);
        }
    }  

}