/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.utils.xml;

import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.DateUtil;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用途： 
 * 作者: zhangbo
 * 时间: 2016-05-07  16:38 
 */
public class JaxbDateAdapter extends XmlAdapter<String, Date> {

    private String pattern = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Date unmarshal(String dateStr) throws Exception {
        return DateUtil.parseDate(dateStr);
    }

    @Override
    public String marshal(Date date) throws Exception {
        return DateUtil.getDate(date, pattern);
    }

}
