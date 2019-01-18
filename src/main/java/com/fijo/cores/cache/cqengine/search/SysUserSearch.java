/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.cache.cqengine.search;

import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysOrgAdvanceService;
import com.fijo.cores.cache.cqengine.index.SysUserIndex;
import com.fijo.cores.utils.configs.CoreConfig;
import com.google.common.collect.Lists;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import com.googlecode.cqengine.resultset.common.ResultSets;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysOrgAdvanceService;
import com.fijo.cores.cache.cqengine.index.SysUserIndex;
import com.fijo.cores.utils.configs.CoreConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

import static com.googlecode.cqengine.codegen.AttributeBytecodeGenerator.createAttributes;
import static com.googlecode.cqengine.query.QueryFactory.equal;
import static com.googlecode.cqengine.query.QueryFactory.in;

/**
 * 用途： 
 * 作者: zhangbo
 * 时间: 2016-06-04  10:30
 *
 * 参考：
 * http://vishnu667.github.io/using-cqengine/
 * https://github.com/npgall/cqengine
 */
@Component
@CacheConfig(cacheNames = {"runtime:"}, cacheResolver="genericCacheResolver", keyGenerator="genericKeyGenerator")
public class SysUserSearch {
    public transient final Log log = LogFactory.getLog(getClass());

    private IndexedCollection<SysUserIndex> sysUserData = new ConcurrentIndexedCollection<SysUserIndex>();

    private SQLParser<SysUserIndex> parser = SQLParser.forPojoWithAttributes(SysUserIndex.class, createAttributes(SysUserIndex.class));

    @Autowired
    private ISysOrgAdvanceService sysOrgAdvanceService;

    @Autowired
    protected CoreConfig config;

    @PostConstruct
    public void init(){
        if(Boolean.valueOf(config.getValue("app.enable.cqengine"))) {
            //建立索引
            sysUserData.addIndex(HashIndex.onAttribute(SysUserIndex.ID));
            sysUserData.addIndex(HashIndex.onAttribute(SysUserIndex.LOGIN_NAME));
            sysUserData.addIndex(HashIndex.onAttribute(SysUserIndex.UNIQUE_CODE));
            sysUserData.addIndex(HashIndex.onAttribute(SysUserIndex.ORG_ID));
            sysUserData.addIndex(HashIndex.onAttribute(SysUserIndex.PARENT_ID));
            sysUserData.addIndex(HashIndex.onAttribute(SysUserIndex.OWNER_ORG));
            sysUserData.addIndex(HashIndex.onAttribute(SysUserIndex.POSITION));

            //注册查询解析器
            parser.registerAttribute(SysUserIndex.ID);
            parser.registerAttribute(SysUserIndex.LOGIN_NAME);
            parser.registerAttribute(SysUserIndex.UNIQUE_CODE);
            parser.registerAttribute(SysUserIndex.ORG_ID);
            parser.registerAttribute(SysUserIndex.PARENT_ID);
            parser.registerAttribute(SysUserIndex.OWNER_ORG);
            parser.registerAttribute(SysUserIndex.POSITION);
        }
    }

    /**
     * 添加用户至CQEngine内存中
     * @param u
     * @param parents
     * @return
     */
    public boolean createToIndex(SysUser u, List<SysOrg> hierarchyOrgs){
        //建立索引的字段不可为空，否则忽略数据
        if(null !=u.getOwnerOrgId() && StringUtils.isNotEmpty(u.getPosition())){
            SysOrg sysOrg = sysOrgAdvanceService.loadByKey(u.getSysOrg().getId());
            Integer parentId = null==sysOrg.getParent() ? null:sysOrg.getParent().getId();
            SysUserIndex index = new SysUserIndex(u.getId(), u.getLoginName(), u.getUnionid(), sysOrg.getId(), parentId, u.getOwnerOrgId(), u.getPosition(), u, hierarchyOrgs, u.getOrderBy());
            log.debug(index);
            sysUserData.add(index);
            return true;
        }
        return false;
    }

    public boolean removeFromIndex(Integer id){
        ResultSet<SysUserIndex> resultSet = sysUserData.retrieve(equal(SysUserIndex.ID, id));
        if(null != resultSet && resultSet.isNotEmpty()){
            SysUserIndex index = resultSet.uniqueResult();
            return sysUserData.remove(index);
        }
        return false;
    }

    public boolean removeAllFromIndex(Collection<Integer> ids){
        ResultSet<SysUserIndex> resultSet = sysUserData.retrieve(in(SysUserIndex.ID, ids));
        if(null != resultSet && resultSet.isNotEmpty()){
            Collection<SysUserIndex> indexs = Lists.newArrayList();
            for (SysUserIndex user : resultSet) {
                indexs.add(user);
            }
            return sysUserData.removeAll(indexs);
        }
        return false;
    }

    public boolean removeFromIndex(String loginName){
        ResultSet<SysUserIndex> resultSet = sysUserData.retrieve(equal(SysUserIndex.LOGIN_NAME, loginName));
        if(null != resultSet && resultSet.isNotEmpty()){
            SysUserIndex index = resultSet.uniqueResult();
            return sysUserData.remove(index);
        }
        return false;
    }

    /**
     * 通过SQLParser检索数据
     * @param u
     * @return
     */
    @Cacheable
    public Collection<SysUserIndex> searchQuery(Integer id, String loginName, String uniqueCode,Integer orgId, Integer parentId, Integer ownerOrgId, String position){
        String sql = "SELECT * FROM sysUserData WHERE (";
        if(null != id)
            sql += " AND "+SysUserIndex.ID.getAttributeName()+"="+id;
        if(StringUtils.isNotEmpty(loginName))
            sql += " AND "+SysUserIndex.LOGIN_NAME.getAttributeName()+"='"+loginName+"\'";
        if(StringUtils.isNotEmpty(uniqueCode))
            sql += " AND "+SysUserIndex.UNIQUE_CODE.getAttributeName()+"='"+uniqueCode+"\'";
        if(null != orgId)
            sql += " AND "+SysUserIndex.ORG_ID.getAttributeName()+"="+orgId;
        if(null != parentId)
            sql += " AND "+SysUserIndex.PARENT_ID.getAttributeName()+"="+parentId;
        if(null != ownerOrgId)
            sql += " AND "+SysUserIndex.OWNER_ORG.getAttributeName()+"="+ownerOrgId;
        if(StringUtils.isNotEmpty(position))
            sql += " AND "+SysUserIndex.POSITION.getAttributeName()+"='"+position+"\'";
        sql = StringUtils.replace(sql, "WHERE ( AND", "WHERE (" )+") ORDER BY orderBy ASC";
        log.debug(sql);
        return ResultSets.asCollection(parser.retrieve(sysUserData, sql));
    }


    @Cacheable
    public Collection<SysUserIndex> searchQuery(String loginName, String uniqueCode, Integer orgId, Integer parentId, Integer ownerOrgId, String position){
        return searchQuery(null,loginName,uniqueCode,orgId,parentId,ownerOrgId,position);
    }

    @Cacheable
    public Collection<SysUserIndex> searchQuery(Integer id){
        return searchQuery(id,null,null,null,null,null,null);
    }

    @Cacheable
    public Collection<SysUserIndex> searchQuery(String loginName){
        return searchQuery(null,loginName,null,null,null,null);
    }

}
