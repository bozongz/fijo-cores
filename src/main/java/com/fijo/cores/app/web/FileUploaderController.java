/**
 * 
 */
package com.fijo.cores.app.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.AppCodeGenerator;
import com.fijo.cores.utils.AppFileUtils;
import com.fijo.cores.utils.Digests;
import com.fijo.cores.utils.Encodes;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.model.GetObjectRequest;
import com.baidubce.services.bos.model.ObjectMetadata;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.fijo.cores.admin.authority.model.ShiroUser;
import com.fijo.cores.app.model.FileUploader;
import com.fijo.cores.app.service.IFileUploaderService;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.InvalidateSNSUserException;
import com.fijo.cores.shiro.AppUserSession;
import com.fijo.cores.utils.AppCodeGenerator;
import com.fijo.cores.utils.AppFileUtils;
import com.fijo.cores.utils.AppFileUtils.StoreLocation;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.Digests;
import com.fijo.cores.utils.Encodes;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.utils.enums.SNSLoginType;
import com.fijo.cores.web.LogicController;
import com.fijo.cores.web.filter.SNSAuthenticationToken;

/**
 * @author zhangbo
 *
 */
@Controller
@RequestMapping(value = {"/action/app/file", "/action/api/app/file", "/action/sso/app/file", "/action/anonymous/app/file"}) 
public class FileUploaderController extends LogicController<FileUploader, Long>{

	@Autowired
	private AppUserSession appUserSession;
	
	@Autowired
	private CoreConfig coreConfig;
	
	@Autowired
	private IFileUploaderService fileUploaderService;

	private AppFileUtils.StoreLocation location = null;
	
	@Autowired
	@Qualifier("appFileUtils")
	private AppFileUtils appFileUtils;
	
	public FileUploaderController() {
		super(FileUploader.class, null, null);
	}

	@PostConstruct
	private void initService() {
		setService(fileUploaderService);
		location = Enum.valueOf(AppFileUtils.StoreLocation.class, coreConfig.getValue("app.upload.file.store"));
	}
	
	/**
	 * APP上传文件，并记录文件记录（attr1为MD5保存值）
	 * @param file
	 * @param accesstoken
	 * @param md5
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/createMd5File", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "通过校验前端提交的MD5值完成文件上传", httpMethod = "POST", response = Map.class, notes = "通过校验前端提交的MD5值完成文件上传，避免相同文件，不同用户文件无法上传",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> createMd5File(HttpServletRequest request, HttpServletResponse response, @RequestParam("accesstoken") String accesstoken,
			@RequestParam("md5") String md5, @RequestParam("timestamp") String timestamp, @RequestParam("fileClass") String fileClass) throws Exception{  
		Map<String, Object> map = Maps.newHashMap();
		Set<String> filePaths = Sets.newHashSet();
		int total = 0;
		int saved = 0;
		String actualMd5 = Digests.encryptMD5(config.getValue("app.api.key")+accesstoken+timestamp+fileClass);
		log.debug("Expect MD5: " +md5);
		log.debug("Actual MD5: " +actualMd5);
		if(StringUtils.isEmpty(accesstoken) || !md5.equals(actualMd5)){
			throw new InvalidateSNSUserException("MP100001", String.format("Invalidate user not allowed to access resource!"));
		}else{			
			if(appUserSession.isUserSessionTimeout()){
				SNSAuthenticationToken snsToken = new SNSAuthenticationToken(accesstoken, SNSLoginType.accesstoken);
				try{
					SecurityUtils.getSubject().login(snsToken);
				}catch(Exception e){
					throw new InvalidateSNSUserException("MP100001", String.format("Invalidate user not allowed to access resource!"));
				}
			}
			MultipartFile file = null;
			MultipartHttpServletRequest mureq = (MultipartHttpServletRequest) request;
			Map<String, MultipartFile> files = mureq.getFileMap(); 
			if (files != null && files.size() != 0) { 
				total = files.size();
				for(String key : files.keySet()) {
			    	file=files.get(key);
			    	String filePath = fileUploaderService.createMd5File(file, fileClass);
			    	if(StringUtils.isNotEmpty(filePath)){
			    		saved++;
			    		filePaths.add(filePath);
			    	}
		    	}
			}
			map.put("message", String.format("共计接收%d个文件，成功保存%d个", total, saved));		
			map.put("data", filePaths);
			map.put("responseid", 1);
			return map;
		}	
	}
	
	/**
	 * 上传文件人口（单个文件）[只存储文件，不保存文件记录]
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "uploadFile", method = RequestMethod.POST)
    @ApiOperation(value = "文件上传", httpMethod = "POST", response = Map.class, notes = "上传文件人口（单个文件）[只存储文件，不保存文件记录]",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public void uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception{  
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8"); 
		PrintWriter out = response.getWriter();
		ShiroUser user = appUserSession.getCurrentUser();
		MultipartFile file = null;
		String storePath = null;
		String filename = null;
		if(StringUtils.isNotEmpty(request.getParameter("imageurl"))){ // 美图秀秀第二次返回剪裁
			log.debug("imageurl is not null ---------------------"+request.getParameter("imageurl"));
			filename = StringUtils.substringAfterLast(request.getParameter("imageurl"), "/");
			switch(location){
				case Cloud:				
					storePath = request.getParameter("imageurl");
			    	storePath = StringUtils.substringAfter(storePath, coreConfig.getValue("bae.bcs.bucket")); //截获bucket以后字符串
			    	storePath = StringUtils.substringBeforeLast(storePath, "/"); //去掉文件名
			    	storePath += "/"; //补全路径
					break;
				case Disk:			
					storePath = request.getParameter("imageurl");
			    	storePath = StringUtils.substringAfter(storePath, "/"+coreConfig.getCtx()); //截获项目名以后字符串
			    	storePath = StringUtils.substringBeforeLast(storePath, "/"); //去掉文件名
			    	storePath += "/"; //补全路径
					break;
				default:
					break;
			}
		}else{
			log.debug("imageurl is null ......................"); // 第一次上传文件
			storePath = "/static/uploadFiles/"+user.getLoginName()+"/"+DateUtil.getToday()+"/"+AppCodeGenerator.nextSystemUUID()+"/";
		}
		MultipartHttpServletRequest mureq = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> files = mureq.getFileMap(); 
		if (files != null && files.size() != 0) { 
			for(String key : files.keySet()) {
		    	file=files.get(key);
		    	if(StringUtils.isEmpty(request.getParameter("imageurl"))){
		    		filename = file.getOriginalFilename();
		    	}
		    	break;
	    	}
			log.debug("storePath is: "+storePath);
			String savePath = appFileUtils.uploadFromClient(file, filename, storePath);
			if(!StringUtils.isEmpty(savePath)){			
				out.println("<script type=\"text/javascript\">parent.imageupload=\"" + savePath +
		                "\";parent.imageOtherInfo={\"filename\":\"" + file.getOriginalFilename() + "\"}</script>"); 

			}else{
				out.println("<script type=\"text/javascript\">parent.imageMessage=\"系统异常\";</script>");
				out.close();
			}
		}else{
			out.println("<script type=\"text/javascript\">parent.imageMessage=\"未有文件提交上传\";</script>");
			out.close();
		}
	} 
	
	/**
	 * 上传情报正文及附件入口(历史遗留问题，情报系统专用NTKO控件)
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "uploadFileContent", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "上传NTKO控件文件", httpMethod = "POST", response = Map.class, notes = "上传NTKO控件文件【情报系统】",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String,String> uploadFileContent(HttpServletRequest request) throws Exception{  
		Map<String,String> map = Maps.newHashMap();
		String fileName = null;
		String savePath = null;
		ShiroUser user = appUserSession.getCurrentUser();
		MultipartFile file = null;
		MultipartHttpServletRequest mureq = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> files = mureq.getFileMap(); 
		if (files != null && files.size() != 0) { 
			for(String key : files.keySet()) {
		    	file=files.get(key);
		    	break;
	    	}
	        String fileFullPath = Encodes.urlDecode(file.getOriginalFilename()); //NTKO控件新增和上传文件回传的文件名其实是一个http的网络绝对路径
	        log.debug(fileFullPath);
	        if(fileFullPath.indexOf("http") == -1 || fileFullPath.indexOf("/static/uploadFiles/") == -1){
	        	fileName = fileFullPath; //新建文件
	        }else{ //修改已上传文件
	        	fileName = fileFullPath.substring(fileFullPath.lastIndexOf("/"));
	        }        
	        savePath = appFileUtils.uploadFromClient(file, fileName, "/static/uploadFiles/"+user.getLoginName()+"/"+DateUtil.getToday()+"/");
			log.debug(fileName);
			log.debug(savePath);
		} 
		map.put("fileName", StringUtils.isEmpty(savePath) ? "": Encodes.urlEncode(fileName));
		map.put("filePath", StringUtils.isEmpty(savePath) ? "": Encodes.urlEncode(savePath));
		return map;
	} 
	
	/**
	 * 既删除文件，又删除数据库记录
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteFiles", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "删除文件", httpMethod = "POST", response = Map.class, notes = "既删除文件，又删除数据库记录",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public Map<String, Object> deleteFiles(String filePath) throws Exception {
		Map<String, Object> map = Maps.newHashMap();
		FileUploader o = new FileUploader();
		o.setFilePath(filePath);
		int ret = fileUploaderService.delete(o);
		map.put("message", ret>0 ? "删除成功!":"删除失败!");
		map.put("responseid", ret>0 ? 1:0);
		return map;
	}
	
	
	/**
	 * 上传文件入口（单个文件）[存储文件，并保存文件记录]
	 * @param request
	 * @return
	 * @throws Exception
	 */	
	@RequestMapping(value = "uploadFileAndSaveRecord", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件并保存记录", httpMethod = "POST", response = Map.class, notes = "上传文件入口（单个文件）[存储文件，并保存文件记录]",
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public void uploadFileAndSaveRecord(HttpServletRequest request, HttpServletResponse response) throws Exception{  
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8"); 
		PrintWriter out = response.getWriter();
		ShiroUser user = appUserSession.getCurrentUser();
		MultipartFile file = null;
		String storePath = null;
		String filename = null;
		if(StringUtils.isNotEmpty(request.getParameter("imageurl"))){ // 美图秀秀第二次返回剪裁
			log.debug("imageurl is not null ---------------------"+request.getParameter("imageurl"));
			filename = StringUtils.substringAfterLast(request.getParameter("imageurl"), "/");
			switch(location){
				case Cloud:				
					storePath = request.getParameter("imageurl");
			    	storePath = StringUtils.substringAfter(storePath, coreConfig.getValue("bae.bcs.bucket")); //截获bucket以后字符串
			    	storePath = StringUtils.substringBeforeLast(storePath, "/"); //去掉文件名
			    	storePath += "/"; //补全路径
					break;
				case Disk:			
					storePath = request.getParameter("imageurl");
			    	storePath = StringUtils.substringAfter(storePath, "/"+coreConfig.getCtx()); //截获项目名以后字符串
			    	storePath = StringUtils.substringBeforeLast(storePath, "/"); //去掉文件名
			    	storePath += "/"; //补全路径
					break;
				default:
					break;
			}
		}else{
			log.debug("imageurl is null ......................"); // 第一次上传文件
			storePath = "/static/uploadFiles/"+user.getLoginName()+"/"+DateUtil.getToday()+"/"+AppCodeGenerator.nextSystemUUID()+"/";			
		}
		MultipartHttpServletRequest mureq = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> files = mureq.getFileMap(); 
		if (files != null && files.size() != 0) { 
			for(String key : files.keySet()) {
		    	file=files.get(key);
		    	if(StringUtils.isEmpty(request.getParameter("imageurl"))){
		    		String  suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."), file.getOriginalFilename().length());
		    		filename = AppCodeGenerator.nextSystemUUID()+suffix;
		    	}
		    	break;
	    	}
			log.debug("storePath is: "+storePath);
			String savePath = appFileUtils.uploadFromClient(file, filename, storePath);
			if(!StringUtils.isEmpty(savePath)){	
				try {
					
					FileUploader fUploader = new FileUploader();
					fUploader.setFilePath(savePath);
					fUploader.setOrgId(user.getOrgId());
					fUploader.setCreateUserId(user.getUserId());
					fUploader.setCreateUserCode(user.getUserCode());
					fUploader.setCreateUserName(user.getUserName());
					fUploader.setCreateDate(DateUtil.getCurrent());
					fUploader.setFileClass(UUID.randomUUID().toString()+"-"+DateUtil.getDate(fUploader.getCreateDate(), DateUtil.timestampPattern1));
					fUploader.setFinalName(file.getOriginalFilename());
					int ret = fileUploaderService.create(fUploader);
					log.debug(ret);
					
					
					out.println("<script type=\"text/javascript\">parent.imageupload=\"" + savePath +
							"\";parent.imageOtherInfo={\"filename\":\"" + file.getOriginalFilename() + "\",\"id\":\"" + fUploader.getId() + "\"}</script>"); 
				} catch (Exception e) {
					out.println("<script type=\"text/javascript\">parent.imageMessage=\"系统异常\";</script>");
					out.close();
				}

			}else{
				out.println("<script type=\"text/javascript\">parent.imageMessage=\"系统异常\";</script>");
				out.close();
			}
		}else{
			out.println("<script type=\"text/javascript\">parent.imageMessage=\"未有文件提交上传\";</script>");
			out.close();
		}
	} 
	
	@RequestMapping(value = "/dowanloadSourceFile", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件", httpMethod = "GET", notes = "以流的方式下载文件")
	public void dowanloadSourceFile(Long id,HttpServletRequest request, HttpServletResponse response) throws IOException {
		ShiroUser user = appUserSession.getCurrentUser();
		FileUploader fUploader = fileUploaderService.getById(id);
		String filePath = fUploader.getFilePath();
		String savePath = "/tempDownOrExport/"+user.getLoginName()+"/"+DateUtil.getToday()+"/";
		File file = null;
        switch (location) {
            case Cloud:
            	
            	file = appFileUtils.downloadFromCloud(fUploader.getFilePath(),fUploader.getFinalName(),savePath);
                break;
            case Disk:
            	file = new File(appFileUtils.getFileLocation()+filePath.substring(filePath.indexOf(appFileUtils.getBaseUrl())+appFileUtils.getBaseUrl().length()));
                break;
            case FastDFS:
            	String fileUrl = coreConfig.getValue("fastdfsClient")+filePath;
            	file = appFileUtils.downloadFromUrl(fileUrl, fUploader.getFinalName(), savePath);
                break;
            default:
                break;
        }
		
		OutputStream outputStream = null;
		try {
			String filename = fUploader.getFinalName();
			response.reset();  
			if (request.getHeader( "USER-AGENT" ).toLowerCase().indexOf( "msie" ) >  0 )//ie浏览器空格处理
            {
				filename = URLEncoder.encode(filename, "utf-8");
				filename = filename.replace("+", "%20");// 处理空格变“+”的问题
				response.setHeader("Content-disposition", "attachment; filename=" + filename);
            }
            else
            {
                response.setHeader("content-disposition", "attachment;filename=\"" +  new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"");//其他浏览器空格处理
            } 
		    response.setContentType("application/octet-stream;charset=UTF-8");  	
		    response.setHeader("Content-Length", String.valueOf(file.length()));
		    outputStream = new BufferedOutputStream(response.getOutputStream());  	    
		    outputStream.write(FileUtils.readFileToByteArray(file));  
		    outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {  
	        if (outputStream != null) {  
	        	outputStream.close();  
	        }  
	    } 	
	}
}
