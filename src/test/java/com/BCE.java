package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.utils.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.BosObject;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.ListObjectsResponse;
import com.baidubce.services.bos.model.ObjectMetadata;
import com.fijo.cores.utils.AppFileUtils;
import com.fijo.cores.utils.Encodes;

public class BCE {
	private static final String ACCESS_KEY_ID = "aee1ca18d56a47c1845227bd335ed0d4";
	private static final String SECRET_ACCESS_KEY = "10856598e09b4f9a9536c83a10ed6fa8";
	private static final String BUCKET = "onegym1011";

	public static void main(String[] args) throws IOException {
	    String ENDPOINT = "http://bj.bcebos.com";
	    BosClientConfiguration config = new BosClientConfiguration();
	    config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
	    config.setEndpoint(ENDPOINT);
	    BosClient client = new BosClient(config);	    
//	    File targetFile = new File("C:\\Users\\lenovo\\Desktop\\清华大学MBA.txt");
//	    String storePath = "/static/test/";
//	    putObject(client, targetFile, storePath);
//	    deleteObject(client, "http://bj.bcebos.com/v1/onegymbucket/static/test/aaa.jpg");
        //上传url文件
        String fileurl = "http://bj.bcebos.com/v1/onegymbucket/static/uploadFiles/manager1/2015-10-28/e082e0bfc93d40d29b62afd087ee9f88/logo.jpg";
        String uploadedUrl =uploadFromUrl(client, BUCKET, fileurl, AppFileUtils.getFileBaseName(fileurl), "/static/uploadFiles/");
        System.out.println("uploadedUrl is: "+uploadedUrl);
	}
	
	public static void putObject(BosClient client,File targetFile, String storePath) throws FileNotFoundException{		
		storePath += targetFile.getName();
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(targetFile.length());
		meta.setContentType(AppFileUtils.getContentType(targetFile.getAbsolutePath()));
		client.putObject(BUCKET, storePath, new FileInputStream(targetFile), meta);												
		String savePath = client.generatePresignedUrl(BUCKET, storePath, -1).toString()+"&responseContentDisposition=attachment";
    	int index = savePath.indexOf("?authorization");
    	savePath = savePath.substring(0, index);
    	System.out.println(savePath);
	}

	public static void listObject(BosClient client){
	    ListObjectsResponse listing = client.listObjects(BUCKET);
	    // 遍历所有Object
	    for (BosObjectSummary objectSummary : listing.getContents()) {
	        System.out.println("ObjectKey: " + objectSummary.getKey());
	    }
	}
	
	public static void deleteObject(BosClient client, String fileUrl){
		String remoteUrl = Encodes.urlDecode(fileUrl);	
		try{
			String url = StringUtils.substringAfterLast(remoteUrl, BUCKET+"/");
			BosObject object = client.getObject(BUCKET, url);
			client.deleteObject(BUCKET, object.getKey());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

    public static String uploadFromUrl(BosClient bosClient, String bucket, String fileUrl, String fileName, String storePath){
        String savePath = null;
        HttpURLConnection conn = null;
        try{
            String urlStr = FilenameUtils.getFullPath(fileUrl)+Encodes.urlEncode(AppFileUtils.getFileName(fileUrl));
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod(Constants.HTTPGET);
            conn.connect();
            storePath += fileName;
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(conn.getContentLengthLong());
            meta.setContentType(conn.getContentType());
            bosClient.putObject(bucket, storePath, conn.getInputStream(), meta);
            savePath = bosClient.generatePresignedUrl(bucket, storePath, -1).toString()+"&responseContentDisposition=attachment";
            int index = savePath.indexOf("?authorization");
            savePath = savePath.substring(0, index);
            conn.disconnect();
        } catch(Exception e){
            if(conn != null){
                conn.disconnect();
                conn = null;
            }
            savePath = null;
        }finally{
            if(conn != null){
                conn.disconnect();
                conn = null;
            }
        }
        if(StringUtils.isNotEmpty(savePath)) {
            savePath = StringUtils.replace(savePath, Constants.SEPARATOR, "/");
        }
        return savePath;
    }
}
