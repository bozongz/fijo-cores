/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.shiro;

import io.longyuan.shiro.redissession.service.ShiroSessionRepository;
import io.longyuan.shiro.redissession.session.CachingShiroSessionDao;
import org.apache.shiro.session.Session;

import java.util.Collection;

/**
 * 用途：
 * 作者: zhangbo
 * 时间: 2016-12-29  22:00
 */
public class SimbestCachingShiroSessionDao extends CachingShiroSessionDao {

    private ShiroSessionRepository sessionRepository;

    @Override
    public void setSessionRepository(ShiroSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
        super.setSessionRepository(sessionRepository);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return sessionRepository.getAllSessions();
    }

}
