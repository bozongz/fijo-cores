##团队JEE工程核心支撑通用封装库，使用的技术主要有：
  1)、前端控制器Spring MVC
  
  2)、IOC容器使用Spring

  3)、ORM使用MyBatis, [分页控件Mybatis-PageHelper](https://github.com/pagehelper/Mybatis-PageHelper "Mybatis-PageHelper")
    
  4)、认证授权使用Shrio, 学习博客[跟我学Shiro目录贴](http://jinnianshilongnian.iteye.com/blog/2018398)

  5)、缓存使用Redis，并集成Shiro实现分布式Session共享。[博客移步](http://blog.csdn.net/lishehe/article/details/45223477) [源码移步](https://github.com/alexxiyang/shiro-redis)

  6)、Redis客户端[spring-budgetAttachmentData-redis](https://github.com/spring-projects/spring-budgetAttachmentData-redis)

  7)、日志组件使用SLF4J和Log4J

  8)、调度组件使用Quartz

  9)、验证码使用Jcaptcha

  11)、单元测试使用JUnit
    
  12)、数据库连接池使用Commons DBCP, druid

  13)、Office文档操作使用POI, itextpdf		
	
  14)、JSON处理使用[Jackson](https://github.com/FasterXML/jackson-databind)	
	
  15)、Http控件[Apache/httpclient](https://github.com/apache/httpclient)
	
  16)、云存储基于百度BOS[bce-java-sdk](https://github.com/baidubce/bce-sdk-java)
	
  17)、二维码[Google-Zxing](https://github.com/zxing/zxing)
	
  18)、中文拼音[pinyin4j](https://github.com/belerweb/pinyin4j)

  19)、时间控件[joda-time](https://github.com/JodaOrg/joda-time)
	
  20)、代码生成使用ZJS 

#Maven 仓库添加
```
<dependency>
	<groupId>com.fijo</groupId>
	<artifactId>simbest-fijo</artifactId>
	<version>1.0</version>
</dependency>
		
		
 <repositories>
    <repository>
      <id>fijo-pom-mvn-repo</id>
      <url>https://raw.github.com/bozongz/fijo-cores/pom-mvn/</url>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
      </snapshots>
    </repository>
    <repository>
      <id>fijo-cores</id>
      <name>fijo-core</name>
      <url>https://raw.github.com/bozongz/fijo-cores/mvn/</url>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
      </snapshots>
    </repository>
  </repositories>
```
