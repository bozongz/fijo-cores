/**
 *
 */
package com.fijo.cores.utils;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.*;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.configs.CoreConfig;

import net.mikesu.fastdfs.FastdfsClient;
import net.mikesu.fastdfs.FastdfsClientFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 文件上传工具类
 *
 * @author zhangbo
 */
@Component
public class AppFileUtils {
    private static transient final Log log = LogFactory.getLog(AppFileUtils.class);

    public enum StoreLocation {Cloud, Disk, FastDFS}

    @Autowired
    private CoreConfig coreConfig;

    private static String host = "http://bj.bcebos.com";
    private String apikey = null;
    private String secretKey = null;
    private String bucket = null;
    private StoreLocation location = null;
    private BosClient bosClient = null;

    private FastdfsClient fastdfsClient = null;

    @PostConstruct
    public void init() {
        location = Enum.valueOf(StoreLocation.class, coreConfig.getValue("app.upload.file.store"));
        switch (location) {
            case Cloud: {
                apikey = coreConfig.getValue("bae.app.apikey");
                secretKey = coreConfig.getValue("bae.app.secretkey");
                bucket = coreConfig.getValue("bae.bcs.bucket");

                BosClientConfiguration config = new BosClientConfiguration();
                config.setCredentials(new DefaultBceCredentials(apikey, secretKey));
                config.setEndpoint(host);
                // config.setEndpoint(coreConfig.getValue("app.domain"));
                // 设置HTTP最大连接数为10
                config.setMaxConnections(10);
                bosClient = new BosClient(config);
                break;
            }
            case FastDFS: {
                fastdfsClient = FastdfsClientFactory.getFastdfsClient();
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        AppFileUtils utils = new AppFileUtils();
        utils.apikey = "e46c7e6c281a4d3da7545c685bb5db1c";
        utils.secretKey = "89d1c208bee145f49ca88ef1bbd12302";
        utils.bucket = "onegymbucket";
        utils.location = StoreLocation.Disk;
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(utils.apikey, utils.secretKey));
        config.setEndpoint(host);
        // config.setEndpoint(coreConfig.getValue("app.domain"));
        // 设置HTTP最大连接数为10
        config.setMaxConnections(10);
        BosClient bosClient = new BosClient(config);
        // 构造ListObjectsRequest请求
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(utils.bucket);
        // 递归列出fun目录下的所有文件
        listObjectsRequest.setPrefix("static/uploadFiles/manager1/2015-11-12");
        ListObjectsResponse listing = bosClient.listObjects(listObjectsRequest);
        // 遍历所有Object
        System.out.println("Objects:");
        for (BosObjectSummary objectSummary : listing.getContents()) {
            System.out.println(objectSummary.getKey());
        }

        System.out.println(StringUtils.substringAfterLast("http://bj.bcebos.com/v1/onegymbucket/static/uploadFiles/manager1/2015-11-12/483bdc2b9ee8413d99e91293237615c9/ccc.jpg", "onegymbucket/"));

        String fileUrl = "http://10.87.13.157:9011/fileWeb/CSAOFWeb/魏总2修.jpg";
        URL url = new URL(FilenameUtils.getFullPath(fileUrl) + Encodes.urlEncode(getFileName(fileUrl)));
        String storeFile = "D:\\data\\files\\images\\oa\\news\\魏总2修1111111111111.jpg";
        FileUtils.copyURLToFile(url, new File(storeFile));
        utils.uploadCompressImage(new File(storeFile), 0.7f, "D:\\data\\files\\images\\oa\\news\\");
    }

    public File downloadFromUrl(String fileUrl, String fileName, String storePath) {
        File targetFile = null;
        HttpURLConnection conn = null;
        try {
            log.debug("----Info fileUrl is: " + fileUrl);
            String urlStr = FilenameUtils.getFullPath(fileUrl) + Encodes.urlEncode(getFileName(fileUrl));
            log.debug("----Info: url is:" + urlStr);
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod(Constants.HTTPGET);
            conn.connect();
            if (conn.getContentLengthLong() != 0) {
                //存储时，存放应用磁盘Context路径
                String savePath = getFileLocation() + storePath + fileName;
                targetFile = new File(savePath);
                FileUtils.copyURLToFile(url, targetFile);
                conn.disconnect();
                log.debug(String.format("Downloaded file from url: %s, and save at: %s", fileUrl, targetFile.getAbsolutePath()));
            } else {
                log.error(String.format("Want to download file from %s, but get nothing......", fileUrl));
            }
        } catch (Exception e) {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
            log.error(Exceptions.getStackTraceAsString(e));
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return targetFile;
    }
    
    public File downloadFromCloud(String object, String fileName, String storePath) {
        File targetFile = null;
        try {
        	String savePath = getFileLocation() + storePath + fileName;
        	targetFile = new File(savePath);
        	if(object.startsWith("http")){
        		int index = object.indexOf(bucket);
        		object = object.substring(index+bucket.length(), object.length());
        	}
        	GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, object);
        	bosClient.getObject(getObjectRequest, targetFile);
        	
        } catch (Exception e) {
            
        }
        return targetFile;
    }

    /**
     * 将文件由远程URL读取后，再上传至云存储或应用系统
     *
     * @param fileUrl   远程文件URL
     * @param fileName  存储文件名称
     * @param storePath 云存储或应用Context路径下的相对路径，以/开头，并且以/结束
     * @return 返回存储路径
     */
    public String uploadFromUrl(String fileUrl, String fileName, String storePath) {
        String savePath = null;
        HttpURLConnection conn = null;
        try {
            log.debug("----Info fileUrl is: " + fileUrl);
            String urlStr = FilenameUtils.getFullPath(fileUrl) + Encodes.urlEncode(getFileName(fileUrl));
            log.debug("----Info: url is:" + urlStr);
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod(Constants.HTTPGET);
            conn.connect();
            switch (location) {
                case Cloud:
                    storePath += fileName;
                    ObjectMetadata meta = new ObjectMetadata();
                    meta.setContentLength(conn.getContentLengthLong());
                    meta.setContentType(conn.getContentType());
                    bosClient.putObject(bucket, storePath, conn.getInputStream(), meta);
                    savePath = bosClient.generatePresignedUrl(bucket, storePath, -1).toString() + "&responseContentDisposition=attachment";
                    int index = savePath.indexOf("?authorization");
                    savePath = savePath.substring(0, index);
                    break;
                case Disk:
                    //存储时，存放应用磁盘Context路径
                    savePath = getFileLocation() + storePath + fileName;
                    log.debug("----savePath:" + savePath);
                    File tmpFile1 = new File(savePath);
                    FileUtils.touch(tmpFile1); //覆盖文件
                    FileUtils.copyURLToFile(url, tmpFile1);
                    log.debug(String.format("Temp File %s is exist %s, and size is %s, store at %s", tmpFile1.getName(), tmpFile1.exists(), tmpFile1.length(), tmpFile1.getAbsolutePath()));
                    //访问时，访问应用URL路径
                    String baseUrl = getBaseUrl();
                    savePath = baseUrl + storePath + fileName;
                    break;
                case FastDFS:
                    savePath = getFileLocation() + storePath + fileName;
                    log.debug("----savePath:" + savePath);
                    File tmpFile = new File(savePath);
                    FileUtils.touch(tmpFile); //覆盖文件
                    FileUtils.copyURLToFile(url, tmpFile);
                    log.debug(String.format("Temp File %s is exist %s, and size is %s, store at %s", tmpFile.getName(), tmpFile.exists(), tmpFile.length(), tmpFile.getAbsolutePath()));
                    String fileId = fastdfsClient.upload(tmpFile, fileName);
                    savePath = fileId;
                    log.debug(fileId);
                    tmpFile.deleteOnExit();
                    break;
                default:
                    break;
            }
            conn.disconnect();
        } catch (Exception e) {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
            savePath = null;
            log.error(Exceptions.getStackTraceAsString(e));
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        if (StringUtils.isNotEmpty(savePath)) {
            savePath = StringUtils.replace(savePath, Constants.SEPARATOR, "/");
        }
        log.debug("----Info: savePath is:" + savePath);
        return savePath;
    }

    /**
     * 客户端通过应用系统上传附件至云存储或系统应用
     *
     * @param file      浏览器接收文件
     * @param fileName  存储文件名称
     * @param storePath 云存储或应用Context路径下的相对路径，以/开头，并且以/结束
     * @return 返回存储路径
     */
    public String uploadFromClient(MultipartFile file, String fileName, String storePath) {
        //fileName = AppCodeGenerator.cn2En(fileName);
        String savePath = null;
        File targetFile;
        try {
            switch (location) {
                case Cloud:
                    storePath += fileName;
                    ObjectMetadata meta = new ObjectMetadata();
                    meta.setContentLength(file.getSize());
                    meta.setContentType(file.getContentType());
                    bosClient.putObject(bucket, storePath, file.getInputStream(), meta);
                    savePath = bosClient.generatePresignedUrl(bucket, storePath, -1).toString() + "&responseContentDisposition=attachment";
                    int index = savePath.indexOf("?authorization");
                    savePath = savePath.substring(0, index);
                    break;
                case Disk:
                    //存储时，存放应用磁盘Context路径
                    targetFile = new File(getFileLocation() + storePath + fileName);
                    FileUtils.touch(targetFile); //覆盖文件
                    file.transferTo(targetFile);
                    //访问时，访问应用URL路径
                    String baseUrl = getBaseUrl();
                    savePath = baseUrl + storePath + fileName;
                    break;
                case FastDFS:
                    File tmpFile = File.createTempFile("tmp", "."+getFileExtByName(fileName));
                    file.transferTo(tmpFile);
                    String fileId = fastdfsClient.upload(tmpFile,fileName);
                    savePath = fileId;
                    log.debug(fileId);
                    tmpFile.deleteOnExit();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            savePath = null;
            log.error(Exceptions.getStackTraceAsString(e));
        }
        if (StringUtils.isNotEmpty(savePath)) {
            savePath = StringUtils.replace(savePath, Constants.SEPARATOR, "/");
        }
        log.debug("----Info: savePath is:" + savePath);
        return savePath;
    }

    /**
     * 将应用系统Context路径下的文件上传至云存储，或者直接将Context路径下的文件转换为Http访问路径
     *
     * @param localFile  应用系统临时目录的文件
     * @param storePath  云存储或应用Context路径下的相对路径，以/开头，并且以/结束
     * @return 返回存储路径
     */
    public String uploadFromLocal(File localFile, String storePath) {
        String savePath = null;
        FileInputStream fis = null;
        try {
            switch (location) {
                case Cloud:
                    storePath += localFile.getName();
                    ObjectMetadata meta = new ObjectMetadata();
                    meta.setContentLength(localFile.length());
                    meta.setContentType(getContentType(localFile.getAbsolutePath()));
                    fis = new FileInputStream(localFile);
                    bosClient.putObject(bucket, storePath, fis, meta);
                    savePath = bosClient.generatePresignedUrl(bucket, storePath, -1).toString() + "&responseContentDisposition=attachment";
                    int index = savePath.indexOf("?authorization");
                    savePath = savePath.substring(0, index);
                    break;
                case Disk:
                    //存储时，存放应用磁盘Context路径
                    File targetFile = new File(getFileLocation() + storePath + localFile.getName());
                    if (!targetFile.getCanonicalPath().equals(localFile.getCanonicalPath()))
                        FileUtils.copyFile(localFile, targetFile);
                    //访问时，访问应用URL路径
                    //直接将Context路径下的文件转换为Http访问路径
                    String baseUrl = getBaseUrl();
                    savePath = baseUrl + storePath + targetFile.getName();
                    break;
                case FastDFS:
                    String fileId = fastdfsClient.upload(localFile, localFile.getName());
                    savePath = fileId;
                    log.debug(fileId);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            savePath = null;
            log.error(Exceptions.getStackTraceAsString(e));
        } finally {
            if (null != fis)
                try {
                    fis.close();
                } catch (IOException e) {
                }
        }
        if (StringUtils.isNotEmpty(savePath)) {
            savePath = StringUtils.replace(savePath, Constants.SEPARATOR, "/");
        }
        log.debug("----Info: savePath is:" + savePath);
        return savePath;
    }

    /**
     * 将图片生成文件，并存放在应用系统Context路径下，或者上传至云存储
     *
     * @param image     图片
     * @param storePath 云存储或应用Context路径下的相对路径，以/开头，并且以/结束
     * @return 返回存储路径
     */
    public String uploadBufferedImage(BufferedImage image, String storePath) {
        String savePath = null;
        String fileName = AppCodeGenerator.nextSystemUUID() + ".png";
        try {
            File targetFile = new File(getFileLocation() + storePath + fileName);
            FileUtils.touch(targetFile); //创建空文件
            ImageIO.write(image, "png", targetFile);
            String baseUrl = getBaseUrl();
            savePath = baseUrl + storePath + fileName;
        } catch (IOException e) {
            savePath = null;
            Exceptions.printException(e);
        }
        log.debug("----Info: savePath is:" + savePath);
        return savePath;
    }

    /**
     * 将应用系统Context路径下的图片压缩后，再上传至云存储
     * <p>
     * 图片高质量压缩参考：http://www.lac.inpe.br/JIPCookbook/6040-howto-compressimages.jsp
     *
     * @param imageFile 应用系统Context路径下的图片
     * @param quality   压缩质量
     * @param storePath 云存储或应用Context路径下的相对路径，以/开头，并且以/结束
     * @return 返回存储路径
     */
    public String uploadCompressImage(File imageFile, float quality, String storePath) {
        String savePath = null;
        ByteArrayOutputStream baos = null;
        ImageOutputStream ios = null;
        ByteArrayInputStream bais = null;
        ImageWriter writer;
        try {
            BufferedImage image = ImageIO.read(imageFile);
            Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpg");
            if (!writers.hasNext())
                throw new IllegalStateException("No writers found");
            writer = (ImageWriter) writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            baos = new ByteArrayOutputStream(32768);
            log.debug("uploadCompressImage location is :" + location);
            switch (location) {
                case Cloud:
                    storePath += "compressed" + imageFile.getName();
                    bais = new ByteArrayInputStream(baos.toByteArray());
                    bosClient.putObject(bucket, storePath, bais);
                    savePath = bosClient.generatePresignedUrl(bucket, storePath, -1).toString() + "&responseContentDisposition=attachment";
                    int index = savePath.indexOf("?authorization");
                    savePath = savePath.substring(0, index);
                    bais.close();
                    break;
                case Disk:
                    //存储时，存放应用磁盘Context路径
                    ios = ImageIO.createImageOutputStream(baos);
                    String compressedFilePath = getFileLocation() + storePath;
                    FileUtils.forceMkdir(new File(compressedFilePath));
                    File compressedFile = new File(compressedFilePath + "compressed" + imageFile.getName());
                    log.debug("----Info compressedFile path is :" + compressedFile.getPath());
                    FileImageOutputStream output = new FileImageOutputStream(compressedFile);
                    writer.setOutput(output);
                    writer.write(null, new IIOImage(image, null, null), param);
                    output.flush();
                    writer.dispose();
                    ios.flush();
                    ios.close();
                    baos.close();
                    output = null;
                    writer = null;
                    ios = null;
                    baos = null;
                    //访问时，访问应用URL路径
                    String baseUrl = getBaseUrl();
                    savePath = baseUrl + storePath + compressedFile.getName();
                    break;
                case FastDFS:
                    ios = ImageIO.createImageOutputStream(baos);
                    File tmpFile = File.createTempFile("tmp", "."+getFileExtByName(imageFile.getName()));
                    output = new FileImageOutputStream(tmpFile);
                    writer.setOutput(output);
                    writer.write(null, new IIOImage(image, null, null), param);
                    output.flush();
                    writer.dispose();
                    ios.flush();
                    ios.close();
                    baos.close();
                    output = null;
                    writer = null;
                    ios = null;
                    baos = null;
                    String fileId = fastdfsClient.upload(tmpFile,tmpFile.getName());
                    savePath = fileId;
                    log.debug(fileId);
                    tmpFile.deleteOnExit();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            savePath = null;
            Exceptions.printException(e);
        } finally {
            if (baos != null)
                try {
                    baos.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
        }
//        if (StringUtils.isNotEmpty(savePath)) {
//            savePath = StringUtils.replace(savePath, Constants.SEPARATOR, "/");
//        }
        log.debug("savePath:" + savePath);
        return savePath;
    }

    public String uploadCompressImageFromUrl(String fileUrl, float quality, String storePath) {
        try {
            //先将远程URL资源保存为本地图片
            log.debug("----Info fileUrl is: " + fileUrl);
            String urlStr = FilenameUtils.getFullPath(fileUrl) + Encodes.urlEncode(getFileName(fileUrl));
            log.debug("----Info: urlStr is:" + urlStr);
            URL url = new URL(urlStr);
            log.debug("----Info url is:" + url.getPath());
            String tempFileName = getFileBaseName(fileUrl);
            if(StringUtils.length(tempFileName) <= 3)
                tempFileName  = AppCodeGenerator.nextDateTimeCode() + tempFileName;
            File imageFile = File.createTempFile(tempFileName, ".jpg");
            log.debug("----Info imageFile path is:" + imageFile.getPath());
            FileUtils.copyURLToFile(url, imageFile);
            return uploadCompressImage(imageFile, quality, storePath);
        } catch (IOException e) {
            Exceptions.printException(e);
        }
        return null;
    }

    public String uploadImageFromUrl(String fileUrl, String storePath) {
        try {
            //先将远程URL资源保存为本地图片
            log.debug("----Info fileUrl is: " + fileUrl);
            String urlStr = FilenameUtils.getFullPath(fileUrl) + Encodes.urlEncode(getFileName(fileUrl));
            log.debug("----Info: urlStr is:" + urlStr);
            URL url = new URL(urlStr);
            log.debug("----Info url is:" + url.getPath());
            String tempFileName = getFileBaseName(fileUrl);
            log.debug("----tempFileName is:" + tempFileName);
            if(StringUtils.length(tempFileName) <= 3)
                tempFileName  = AppCodeGenerator.nextDateTimeCode() + tempFileName;
            tempFileName = tempFileName + ".jpg";
            log.debug("----tempFileName is:" + tempFileName);
            return uploadFromUrl(fileUrl, tempFileName, storePath);
        } catch (IOException e) {
            Exceptions.printException(e);
        }
        return null;
    }

    /**
     * 根据文件访问URL, 将文件从云存储或应用系统Context路径下的文件删除
     *
     * @param fileUrl
     */
    public void deleteFile(String fileUrl) {
        log.debug("----Info fileUrl is :" + fileUrl);
        if (!StringUtils.isEmpty(fileUrl)) {
            switch (location) {
                case Cloud:
                    final String remoteUrl = Encodes.urlDecode(fileUrl);
                    String objectKey = null;
                    try {
                        objectKey = StringUtils.substringAfterLast(remoteUrl, coreConfig.getValue("bae.bcs.bucket") + "/");
                        BosObject object = bosClient.getObject(bucket, objectKey);
                        //bosClient.deleteObject(bucket, object.getKey());
                    } catch (Exception e) {
                        log.error(String.format("Delete File failed with objectKey: %s remoteUrl: %s ......", objectKey, remoteUrl));
                    }
                    break;
                case Disk:
                    String filePath = null;
                    try {
                        filePath = getFileLocation() + fileUrl.substring(fileUrl.indexOf("/static"));
                        //FileUtils.forceDelete(new File(filePath));
                        log.debug(filePath);
                    } catch (Exception e) {
                        log.error(String.format("Delete File failed with filePath: %s ......", filePath));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 根据文件访问URL列表, 将文件从云存储或应用系统Context路径下的文件删除
     * <p>
     * 调用带有返回值的多线程（实现callable接口），也是同步的。参考：http://blueram.iteye.com/blog/1583117
     *
     * @param fileUrls
     * @return 返回存储路径
     */
    public Integer deleteFiles(Collection<String> fileUrls) {
        int count = 0;
        AsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        for (String url : fileUrls) {
            final String fileUrl = StringUtils.substringAfterLast(url, coreConfig.getValue("bae.bcs.bucket") + "/");
            try {
                Future<Integer> future = executor.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        deleteFile(fileUrl);
                        return 1;
                    }
                });
                count += future.get();
            } catch (InterruptedException | ExecutionException e) {
                Exceptions.printException(e);
            }
        }
        return count;
    }

    /**
     * 根据路径返回文件名，如：http://aaa/bbb.jpg C:/aaa/abc.jpg 返回abc
     *
     * @param pathToName
     * @return
     */
    public static String getFileBaseName(String pathToName) {
        return FilenameUtils.getBaseName(pathToName);
    }

    /**
     * 根据路径返回文件名，如：http://aaa/bbb.jpg C:/aaa/abc.jpg 返回abc.jpg
     *
     * @param pathToName
     * @return
     */
    public static String getFileName(String pathToName) {
        return FilenameUtils.getName(pathToName);
    }

    /**
     * 根据路径返回文件后缀，如：http://aaa/bbb.jpg C:/aaa/abc.jpg 返回jpg
     *
     * @param pathToName
     * @return 返回存储路径
     */
    public static String getFileExtByName(String pathToName) {
        return FilenameUtils.getExtension(pathToName);
    }

    /**
     * 根据文件路径获取文件contentType
     *
     * @param pathToFile 文件路径
     * @return 返回存储路径
     */
    public static String getContentType(String pathToFile) {
        Path path = Paths.get(pathToFile);
        String contentType = null;
        try {
            contentType = Files.probeContentType(path);
            if (StringUtils.isEmpty(contentType))
                contentType = getFileExtByName(pathToFile);
        } catch (IOException e) {
            Exceptions.printException(e);
        }
        log.debug("----Info contentType is :" + contentType);
        return contentType;
    }

    public static boolean isWord(String pathToFile) {
        String contentType = getContentType(pathToFile);
        if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType))
            return true;
        else if ("application/msword".equals(contentType))
            return true;
        else if ("doc".equals(contentType))
            return true;
        else if ("docx".equals(contentType))
            return true;
        else
            return false;
    }

    public static boolean isExcel(String pathToFile) {
        String contentType = getContentType(pathToFile);
        if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType))
            return true;
        else if ("application/vnd.ms-excel".equals(contentType))
            return true;
        else if ("xls".equals(contentType))
            return true;
        else if ("xlsx".equals(contentType))
            return true;
        else
            return false;
    }

    public static boolean isOfficeFile(String pathToFile) {
        if (isExcel(pathToFile) || isWord(pathToFile))
            return true;
        else
            return false;
    }

    public static boolean isImage(String pathToFile) {
        String contentType = getContentType(pathToFile);
        if ("image/jpeg".equals(contentType))
            return true;
        else if ("image/gif".equals(contentType))
            return true;
        else if ("image/png".equals(contentType))
            return true;
        else if ("image/bmp".equals(contentType))
            return true;
        else if ("jpeg".equals(contentType))
            return true;
        else if ("jpg".equals(contentType))
            return true;
        else if ("gif".equals(contentType))
            return true;
        else if ("png".equals(contentType))
            return true;
        else if ("bmp".equals(contentType))
            return true;
        else
            return false;
    }

    public static boolean isTxt(String pathToFile) {
        String contentType = getContentType(pathToFile);
        if ("text/plain".equals(contentType))
            return true;
        else if ("txt".equals(contentType))
            return true;
        else
            return false;
    }

    public static boolean isCompressFile(String pathToFile) {
        String contentType = getContentType(pathToFile);
        if ("application/x-zip-compressed".equals(contentType))
            return true;
        else if ("application/zip".equals(contentType))
            return true;
        else if ("application/x-zip".equals(contentType))
            return true;
        else if ("application/x-rar-compressed".equals(contentType))
            return true;
        else if ("application/rar".equals(contentType))
            return true;
        else if ("application/x-rar".equals(contentType))
            return true;
        else if ("rar".equals(contentType))
            return true;
        else if ("rar".equals(contentType))
            return true;
        else if ("zip".equals(contentType))
            return true;
        else
            return false;
    }

    /**
     * 如果pom.xml中设置了文件存储绝对路径app.disk.location，则以该路径作为文件存储路径；
     * 否则，根据app.root获取应用目录下的相对路径，作为存储路径；
     *
     * @return 文件存储路径
     */
    public String getFileLocation() {
        String diskLocation = coreConfig.getValue("app.disk.location");
        if (StringUtils.isEmpty(diskLocation)) {
            diskLocation = System.getProperty(coreConfig.getValue("app.root"));
            log.debug("----Info: diskLocation is empty, so file store path is: " + diskLocation);
        } else {
            log.debug("----Info: diskLocation is not empty, so file store path is: " + diskLocation);
        }
        return diskLocation;
    }

    /**
     * 如果pom.xml定义了文件访问别名app.path.alias，则以该别名作为文件路径前缀（需要配合Tomcat的Context的别名设置）
     * 否则，以应用工程名作为文件路径前缀
     *
     * @return 文件访问前缀
     */
    public String getBaseUrl() {
        String baseUrl = coreConfig.getValue("app.path.alias");
        if (StringUtils.isEmpty(baseUrl)) {
            baseUrl = "/" + coreConfig.getCtx();
        }
        log.debug("----Info baseUrl is :" + baseUrl);
        return baseUrl;
    }

    public File createTempFile(String filename){
        File targetFile = new File(System.getProperty("java.io.tmpdir")+Constants.SEPARATOR+filename);
        try {
            FileUtils.touch(targetFile); //覆盖文件
        } catch (IOException e) {
            Exceptions.printException(e);
        }
        return targetFile;
    }
}
