/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package service;

import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserAdvanceService;
import com.fijo.cores.test.AbstractControllerTester;
import com.fijo.cores.web.filter.SSOAuthenticationToken;
import junit.framework.Assert;
import org.apache.shiro.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SysUserServiceTest extends AbstractControllerTester {

    @Autowired
    private ISysUserAdvanceService sysUserAdvanceService;

    @Before
    public void login() {
        SSOAuthenticationToken token = new SSOAuthenticationToken("admin");
        SecurityUtils.getSubject().login(token);
    }

    @Test
    public void testCreate() {
        SysOrg o = new SysOrg(1);
        SysUser u = new SysUser();
        u.setSysOrg(o);
        u.setLoginName("n1");
        u.setUserCode("n1");
        u.setUniqueCode("n1");
        u.setPhone("n1");
        u.setEmail("n1");
        u.setWeChatCode("n1");
        u.setWeiboCode("n1");
        u.setQqCode("n1");
        u.setAlipayCode("n1");
        u.setUserType(1);
        u.setRemoved(false);
        u.setPassword("123456");
        int ret = sysUserAdvanceService.create(u);
        Assert.assertEquals(1, ret);
        System.out.println(u.getId());
        System.out.println("Finish test!");
    }

    /**
     * MySQL ON DUPLICATE 无论新增或是删除，都可以获取对象主键，而Oracle MERGE新增和删除主键都是序列nextval，因此如果是命中更新，那么对象的主键依旧是nextval，并非原来真实的主键。因此慎用数据库的update-if-exists-else-insert
     */
    @Test
    public void testCreateOrUpdateViaAdmin() {
        SysOrg o = new SysOrg(1);
        SysUser u = new SysUser();
        u.setSysOrg(o);
        u.setLoginName("n6");
        u.setUserCode("n6");
        u.setUniqueCode("n6");
        u.setPhone("n6");
        u.setEmail("n6");
        u.setWeChatCode("n6");
        u.setWeiboCode("n6");
        u.setQqCode("n6");
        u.setAlipayCode("n66");
        u.setUserType(1);
        u.setRemoved(false);
        u.setSceneCode("n66");
        int ret = sysUserAdvanceService.createOrUpdateViaAdmin(u);
        Assert.assertEquals(1, ret);
        System.out.println(u.getId());
        System.out.println("Finish test!");
    }

}
