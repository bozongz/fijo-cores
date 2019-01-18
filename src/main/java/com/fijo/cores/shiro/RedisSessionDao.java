/**
 * 
 */
package com.fijo.cores.shiro;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.crazycake.shiro.RedisSessionDAO;
import org.crazycake.shiro.SerializeUtils;

/**
 * @author Li
 *
 */
public class RedisSessionDao extends RedisSessionDAO{
	@Override
	public Collection<Session> getActiveSessions() {
		Set<Session> sessions = new HashSet<Session>();
		
		Set<byte[]> keys = getRedisManager().keys(getKeyPrefix() + "*");
		if(keys != null && keys.size()>0){
			for(byte[] key:keys){
				Object ss = SerializeUtils.deserialize(getRedisManager().get(key));
				if(ss instanceof Session){
					sessions.add((Session)ss);
				}
			}
		}
		
		return sessions;
	}
}
