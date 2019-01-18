package com.fijo.cores.app.authority;

import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-28
 * <p>Version: 1.0
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    public transient final Log log = LogFactory.getLog(getClass());

    private RedisTemplate<String, String> redisTemplate;

    private HashOperations<String, String, Integer> hashOps = null;

    private String keyPrefix = null;

    private Integer maxAttemptLoginTimes = null;

    private Integer maxAttemptLoginFreezeMinutes = null;

    public RetryLimitHashedCredentialsMatcher(RedisTemplate<String, String> redisTemplate, String keyPrefix, Integer maxAttemptLoginTimes, Integer maxAttemptLoginFreezeMinutes) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
        this.keyPrefix = keyPrefix;
        this.maxAttemptLoginTimes = maxAttemptLoginTimes;
        this.maxAttemptLoginFreezeMinutes = maxAttemptLoginFreezeMinutes;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        boolean matches = false;
        String username = (String)token.getPrincipal();
        Integer retryCount = hashOps.get(keyPrefix+Constants.UNDERLINE+username, username);
        if(retryCount == null) {
            retryCount = new Integer(0);
        }
        retryCount += 1;
        hashOps.put(keyPrefix+Constants.UNDERLINE+username, username, retryCount);
        if(retryCount > maxAttemptLoginTimes) {
            //if retry count > maxAttemptLoginTimes throw
            log.warn("username: " + username + " tried to login more than "+maxAttemptLoginTimes+" times in period");
            //after maxAttemptLoginFreezeMinutes, user can retry login
            redisTemplate.expire(keyPrefix+Constants.UNDERLINE+username, maxAttemptLoginFreezeMinutes, TimeUnit.MINUTES);
            throw new ExcessiveAttemptsException();
        }else{
            matches = super.doCredentialsMatch(token, info);
            if(matches) {
                //clear retry count
                if(hashOps.hasKey(keyPrefix+Constants.UNDERLINE+username, username)){
                    hashOps.delete(keyPrefix+Constants.UNDERLINE+username,username);
                }
            }
        }
        return matches;
    }
}
