/**
 * 
 */
package com.fijo.cores.utils.json;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.web.util.HtmlUtils;

/**
 * @author zhangbo
 *
 */
public class JacksonObjectMapperConfig extends ObjectMapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5665435614319670909L;

	public JacksonObjectMapperConfig() {
		super();
		
		this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);		
		//this.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false); 不增加，避免key值为null，而避免节点消失
		this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true); 
		
		//this.setSerializationInclusion(Include.NON_EMPTY); //对象转字符串时，只转化非空字段 zjs 需要占位
		
		//SimpleModule module = new SimpleModule();
        SimpleModule module = new SimpleModule("HTML XSS Serializer",
                new Version(1, 0, 0, "FINAL", "com.fijo", "ep-jsonmodule"));
        module.addSerializer(new JsonHtmlXssSerializer(String.class));
        module.addDeserializer(Date.class, new CustomJsonDateDeseralizer());
        // Add more here ...
        registerModule(module);
	}

    class JsonHtmlXssSerializer extends JsonSerializer<String> {
        public JsonHtmlXssSerializer(Class<String> string) {
            super();
        }

        public Class<String> handledType() {
            return String.class;
        }

        public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                throws IOException, JsonProcessingException {
            if (value != null) {
                String encodedValue = HtmlUtils.htmlEscape(value.toString());
                encodedValue=encodedValue.replaceAll("&quot;", "\"");
                encodedValue=encodedValue.replaceAll("&amp;", "&");
                encodedValue=encodedValue.replaceAll("&ldquo;", "“");
                encodedValue=encodedValue.replaceAll("&rdquo;", "”");
                encodedValue=encodedValue.replaceAll("&mdash;", "—");
                encodedValue=encodedValue.replaceAll("&times;", "×");
                encodedValue=encodedValue.replaceAll("&lt;", "<");
                encodedValue=encodedValue.replaceAll("&gt;", ">");
                encodedValue=encodedValue.replaceAll("&le;", "<=");
                encodedValue=encodedValue.replaceAll("&ge;", ">=");
                jsonGenerator.writeString(encodedValue);
            }
        }
    }
}
