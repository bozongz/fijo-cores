package com.fijo.cores.web.filter;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import com.octo.captcha.image.ImageCaptcha;
/**
 * 自定义验证码实现类，参考Gimpy.class源码，修改后支持“忽略大小写”功能
 */
public class MyGimpy extends ImageCaptcha implements Serializable {
	private static final long serialVersionUID = -4116932473756890938L;
	private String response;
    MyGimpy(String question, BufferedImage challenge, String response) {
        super(question, challenge);
        this.response = response;
    }
    public final Boolean validateResponse(final Object response) {
        return (null != response && response instanceof String)
                ? validateResponse((String) response) : Boolean.FALSE;
    }
    private final Boolean validateResponse(final String response) {
        // 主要改的这里
        return new Boolean(response.toLowerCase().equals(this.response.toLowerCase()));
    }
}
