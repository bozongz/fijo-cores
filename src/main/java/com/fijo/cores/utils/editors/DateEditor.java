package com.fijo.cores.utils.editors;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;  
import java.text.DateFormat;  
import java.text.ParseException;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
/*
 * spring mvc绑定对象String转Date 对象中有Data的字段，前台传值用这个类转换。
 */
public class DateEditor extends PropertyEditorSupport {

    private static final DateTimeFormatter dateFormater = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dateTimeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter chineseDateFormater = DateTimeFormat.forPattern("yyyy年MM月dd日");
  
    private DateTimeFormatter dateFormat;
    private boolean allowEmpty = true;  
    
    public DateEditor() {  
    }  
  
    public DateEditor(DateTimeFormatter dateFormat) {
        this.dateFormat = dateFormat;  
    }  
  
    public DateEditor(DateTimeFormatter dateFormat, boolean allowEmpty) {
        this.dateFormat = dateFormat;  
        this.allowEmpty = allowEmpty;  
    }  
  
    /** 
     * Parse the Date from the given text, using the specified DateFormat. 
     */  
    @Override  
    public void setAsText(String text) throws IllegalArgumentException {  
        if (this.allowEmpty && !StringUtils.hasText(text)) {  
            // Treat empty String as null value.  
            setValue(null);  
        }else if(text.equalsIgnoreCase("null") || text.equalsIgnoreCase("\"null\"") || text.equalsIgnoreCase("\'null\'")){
        	setValue(null);  
        }
        else {
            if(this.dateFormat != null)
                setValue(this.dateFormat.parseDateTime(text).toDate());
            else {
                if(text.contains("年"))
                    setValue(chineseDateFormater.parseDateTime(text).toDate());
                else if(text.contains(":"))
                    setValue(dateTimeFormater.parseDateTime(text).toDate());
                else
                    setValue(dateFormater.parseDateTime(text).toDate());
            }
        }  
    }  
  
    /** 
     * Format the Date as String, using the specified DateFormat. 
     */  
    @Override  
    public String getAsText() {  
        Date value = (Date) getValue();
        DateTime dt = new DateTime(value);
        DateTimeFormatter dateFormat = this.dateFormat;
        if(dateFormat == null)  
            dateFormat = dateTimeFormater;
        return (value != null ? dt.toString(dateFormat) : "");
    }

    public static void main(String[] args) {
        DateEditor dd = new DateEditor();
        dd.setAsText("2008-10-12");
        System.out.println(dd.getAsText());
    }
}  
