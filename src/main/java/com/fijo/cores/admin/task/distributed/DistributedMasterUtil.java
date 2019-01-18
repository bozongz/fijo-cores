/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.admin.task.distributed;

import com.distributed.lock.redis.RedisReentrantLock;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.exceptions.Exceptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用途：
 * 作者: zhangbo
 * 时间: 2016-11-30  16:06
 */
@Component
public class DistributedMasterUtil {
    protected transient final Log log = LogFactory.getLog(getClass());

    private String master; //集群主控节点ip

    private JedisPool jedisPool;

    private Jedis jedis;

    @Autowired
    private JedisPoolConfig config;

    @Autowired
    private JedisConnectionFactory factory;

    @PostConstruct
    public void init() {
        jedisPool = new JedisPool(config, factory.getHostName(), factory.getPort(), factory.getTimeout(), factory.getPassword());
        jedis = jedisPool.getResource();
    }

    public boolean checkMasterIsMe() {
        log.trace("Host ip: "+getServerIP());
        log.trace("Master ip: "+jedis.get("clusert_master_ip"));
        log.trace("Host port: "+getServerPort());
        log.trace("Master port: "+jedis.get("clusert_master_port"));
        log.trace("Check result: "+ (jedis.get("clusert_master_ip").equals(getServerIP()) && jedis.get("clusert_master_port").equals(getServerPort().toString())));
        return jedis.get("clusert_master_ip").equals(getServerIP()) && jedis.get("clusert_master_port").equals(getServerPort().toString());
    }

    /**
     * 每隔五分钟调用的表达式为： "0 0/5 * * * ?" 参考 TaskTriggerDefinition
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void becameMasertIfNotExist() {
        RedisReentrantLock lock = new RedisReentrantLock(jedisPool, "lock_clusert_master");
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                //TODO 获得锁后要做的事
                String masterIp = jedis.get("clusert_master_ip");
                String masterPort = jedis.get("clusert_master_port");
                String myIp = getServerIP();
                Integer myPort = getServerPort();
                if (StringUtils.isEmpty(masterIp) || StringUtils.isEmpty(masterPort)) {       //1.没有Master
                    jedis.set("clusert_master_ip", myIp);   //设置我为Master
                    jedis.set("clusert_master_port", String.valueOf(myPort));
                    log.trace(String.format("IP: %s on port %s become cluster master...", myIp, myPort));
                } else {
                    boolean masterIsAvailable = heartTest(masterIp, Integer.valueOf(masterPort));
                    if (!masterIsAvailable) {              //2.Master不可用
                        jedis.set("clusert_master_ip", myIp);   //设置我为Master
                        jedis.set("clusert_master_port", String.valueOf(myPort));
                        log.trace(String.format("IP: %s on port %s become cluster master...", myIp, myPort));
                    } else {
                        log.trace(String.format("Master is already at IP: %s on port %s ...", masterIp, masterPort));
                    }
                }
            } else {
                //TODO 获得锁超时后要做的事
                log.trace("I couldn't get the redis lock...");
            }

        } catch (Exception e) {
            Exceptions.printException(e);
        } finally {
            lock.unlock();
        }


    }

    private static List<Inet4Address> getInet4Addresses() throws SocketException {
        List<Inet4Address> ret = new ArrayList();
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                    ret.add((Inet4Address)inetAddress);
                }
            }
        }

        return ret;
    }


//    public static String getServerIP() {
//        List<Inet4Address> inet4;
//        try {
//            inet4 = getInet4Addresses();
//            return !inet4.isEmpty()
//                    ? inet4.get(0).getHostAddress()
//                    : "";
//        } catch (SocketException e) {
//        }
//        return "";
//    }

    /**
     * http://www.cnblogs.com/interdrp/p/3730878.html
     */
//    public static Integer getServerIP(){
//        String port = System.getProperty("reyo.localPort");
//        return StringUtils.isEmpty(port) ? 0 : Integer.parseInt(port);
//    }
    public static String getServerIP(){
        String ip = System.getProperty("reyo.localIp");
        return ip;
    }

    /**
     * 获取服务器ip和端口信息
     * 参考：http://ruitao.name/blog/20160111/tomcat-port/
     *
     * @return
     */
    public Integer getServerPort() {
        try {
            MBeanServer server = null;
            if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
                server = MBeanServerFactory.findMBeanServer(null).get(0);
            }
            Set names = server.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
            Iterator iterator = names.iterator();
            ObjectName name = null;
            while (iterator.hasNext()) {
                name = (ObjectName) iterator.next();
                String protocol = server.getAttribute(name, "protocol").toString();
                String port = server.getAttribute(name, "port").toString();
                if (protocol.equals("HTTP/1.1")) {
                    return Integer.valueOf(port);
                }
            }
        } catch (Exception e) {
            Exceptions.printException(e);
        }
        return 0;
    }

    /**
     * 心跳检查
     * 参考：
     * http://stackoverflow.com/questions/11547082/fastest-way-to-scan-ports-with-java
     * http://jupiterbee.blog.51cto.com/3364619/1301284
     *
     * @param host
     * @param port
     * @return
     */
    public boolean heartTest(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 5000);
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                Exceptions.printException(e);
            }
        }
    }


    public static void main(String[] args) throws UnknownHostException {
        DistributedMasterUtil a = new DistributedMasterUtil();

    }
}
