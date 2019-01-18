/**
 * 
 */
package com.fijo.cores.utils.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
//
///**
// * @author Li
// *
// * http://blog.csdn.net/fengspg/article/details/43705537
// */
@Configuration
@EnableWebMvc
@EnableSwagger
public class SwaggerConfig {
	private SpringSwaggerConfig springSwaggerConfig;

	/**
	 * Required to autowire SpringSwaggerConfig
	 */
	@Autowired
	public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
		this.springSwaggerConfig = springSwaggerConfig;
	}

	/**
     * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc
     * framework - allowing for multiple swagger groups i.e. same code base
     * multiple swagger resource listings.
     */
    @Bean
    public SwaggerSpringMvcPlugin customImplementation()
    {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(apiInfo()).includePatterns(
                ".*?");
    }

    private ApiInfo apiInfo()
    {
    	ApiInfo apiInfo = new ApiInfo(
                "App REST API",
                "App REST BackEnd API",
                "App Terms of service",
                "service@support.app.cn",
                "App API Licence Type",
                "App API License URL"
        );
        return apiInfo;
    }
}
