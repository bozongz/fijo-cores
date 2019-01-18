部署本地 mvn clean install -o -Dmaven.test.skip=true

安装私服 mvn deploy -U -Dmaven.test.skip=true （确保hosted类型的仓库的Deployment Policy选择为Allow Redeploy）

http://10.87.14.77:8081/nexus/content/repositories/public/com/simbest/simbest-cores/0.1/



