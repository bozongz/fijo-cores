/**
 * 
 */
package com.fijo.cores.admin.authority.cache;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.service.ISysOrgAdvanceService;
import com.fijo.cores.cache.cqengine.index.SysUserIndex;
import com.fijo.cores.cache.cqengine.search.SysUserSearch;
import com.fijo.cores.utils.configs.CoreConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserService;
import com.fijo.cores.cache.impl.GenericCache;

import java.util.Collection;
import java.util.List;

/**
 * @author Li
 *
 */
@Component(value="sysUserCache")
public class SysUserCache extends GenericCache<SysUser,Integer>{
    public transient final Log log = LogFactory.getLog(getClass());

    @Autowired
    private ISysOrgAdvanceService sysOrgAdvanceService;

    @Autowired
    private SysUserSearch sysUserSearch;

    @Autowired
    private CoreConfig config;

    private Integer count = 0;

	@Autowired
	public SysUserCache(@Qualifier(value="sysUserService")ISysUserService sysUserService) {
		super(sysUserService);
		super.setClazz(SysUser.class);
		this.registerCustomkey("openid");
		this.registerCustomkey("accesstoken");
        this.registerCustomkey("uniqueCode");
	}

    @Override
    public void doSometingForEachObject(SysUser o){
        if(Boolean.valueOf(config.getValue("app.enable.cqengine"))) {
            boolean result = sysUserSearch.createToIndex(o, sysOrgAdvanceService.getHierarchyOrgs(o.getSysOrg().getId()));
            if (result)
                count++;
        }
    }

    @Override
    public void doSometingAfterLoad(Collection<SysUser> itemCollection){
        log.debug("Find "+itemCollection.size()+" records in redis, and indexed "+count+" records to the Google CQEngine !");
    }

}
