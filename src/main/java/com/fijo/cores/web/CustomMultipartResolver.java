/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.web;

import com.fijo.cores.exceptions.NotAllowUploadFileTypeException;
import com.fijo.cores.utils.AppFileUtils;
import com.fijo.cores.exceptions.NotAllowUploadFileTypeException;
import com.fijo.cores.utils.AppFileUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 用途： 
 * 作者: zhangbo
 * 时间: 2016-04-13  16:59 
 */
public class CustomMultipartResolver extends CommonsMultipartResolver {
    public transient final Log log = LogFactory.getLog(getClass());

    private Set<String> fileTypes;

    protected MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
        String encoding = determineEncoding(request);
        List<FileItem> fileItems = checkContentType(request, encoding);
        return parseFileItems(fileItems, encoding);
    }

    public List<FileItem> checkContentType(HttpServletRequest request, String encoding){
        if(null != fileTypes && fileTypes.size() > 0){
            FileUpload fileUpload = prepareFileUpload(encoding);
            try {
                List<FileItem> fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
                for (FileItem item : fileItems) {
                    if(!item.isFormField()) { //必须是文件
                        log.debug("client want to upload file with name: " + item.getName());
                        log.debug("client want to upload file with type: " + item.getContentType());
                        if (!fileTypes.contains(AppFileUtils.getFileExtByName(item.getName()))) {
                            for(String fileType : fileTypes){
                                log.error("Allowd fileType is: "+fileType);
                            }
                            throw new NotAllowUploadFileTypeException("Not allow upload file type exception occur, client upload type is: "+AppFileUtils.getFileExtByName(item.getName()));
                        }
                    }
                }
                return fileItems;
            } catch (FileUploadException e) {
                throw new NotAllowUploadFileTypeException("Not allow upload file type exception occur... \r\n" + e.getMessage());
            }
        }
        return null;
    }

    public Set<String> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(Set<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public static void main(String[] args) {  
        String specialStr = " #测试转义：#<table id=\"testid\"><tr>test1;test2</tr></table>";
     // ①转换为HTML转义字符表示
        String str1 = HtmlUtils.htmlEscape(specialStr);
        System.out.println(str1);

     // ②转换为数据转义表示
        String str2 = HtmlUtils.htmlEscapeDecimal(specialStr);
        System.out.println(str2);

      //③转换为十六进制数据转义表示
        String str3 = HtmlUtils.htmlEscapeHex(specialStr);
        System.out.println(str3);

       // ④下面对转义后字符串进行反向操作
        System.out.println(HtmlUtils.htmlUnescape(str1));
        System.out.println(HtmlUtils.htmlUnescape(str2));
        System.out.println(HtmlUtils.htmlUnescape(str3));
    }
}
