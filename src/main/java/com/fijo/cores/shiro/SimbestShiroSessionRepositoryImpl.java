/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.shiro;

import com.fijo.cores.utils.configs.CoreConfig;
import io.longyuan.shiro.redissession.service.impl.ShiroSessionRepositoryImpl;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component(value="shiroSessionRepository")
public class SimbestShiroSessionRepositoryImpl extends ShiroSessionRepositoryImpl {

    @Autowired
    private RedisTemplate<String, Session> redisTemplate;

    @Autowired
    private CoreConfig config;

    @PostConstruct
    private void init(){
        this.setRedisTemplate(redisTemplate);
        this.setRedisShiroSessionTimeout(Integer.valueOf(config.getValue("redis.session.timeout")));
    }

    @Override
    public Collection<Session> getAllSessions() {
        Set<Session> sessions = new HashSet<Session>();
        Set<String> keys = getRedisTemplate().keys("shiro-session:*");
        if(keys != null && keys.size()>0){
            for(String key:keys){
                Session session = (Session)this.getRedisTemplate().boundValueOps(key).get();
                sessions.add(session);
            }
        }
        return sessions;
    }
}
