# 团队JEE工程通用的MVN依赖库、插件、仓库等设置

```
#Maven仓库
<repositories>
    <repository>
        <id>simbest-parent-mvn-repo</id>
        <url>https://raw.github.com/simbest/simbest-parent/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

  执行命令发布依赖包至Github
    
    mvn clean deploy

  参考
	
  [Hosting a Maven repository on github (with sources and javadoc)](https://www.javacodegeeks.com/2014/09/hosting-a-maven-repository-on-github-with-sources-and-javadoc.html)
  
  [Hosting a Maven repository on github](http://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github/)
  
  切换Nexus内网仓库注意事项
  
  1. 调整maven的settings.xml
  
  2. plugins中取消maven-deploy-plugin和site-maven-plugin的定义，采用默认配置
  
  3. pluginManagement中取消maven-deploy-plugin和site-maven-plugin的定义，采用默认配置
  
  4. distributionManagement变更为nexus-releases和nexus-snapshots
