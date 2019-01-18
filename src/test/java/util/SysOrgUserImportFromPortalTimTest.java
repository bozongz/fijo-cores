/**
 * 
 */
package util;

import com.google.common.collect.Lists;
import com.fijo.cores.admin.authority.model.SysOrg;
import com.fijo.cores.admin.authority.model.SysOrgCopy;
import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysOrgAdvanceService;
import com.fijo.cores.admin.authority.service.ISysUserService;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.test.AbstractComponentTester;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.office.ExcelUtil2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author Li
 *
 */
@WebAppConfiguration
public class SysOrgUserImportFromPortalTimTest extends AbstractComponentTester{

    @Autowired
    @Qualifier("sysOrgCopyService")
    private IGenericService<SysOrgCopy, Integer> sysOrgCopyService;

	@Autowired
	private ISysOrgAdvanceService sysOrgAdvanceService;
	
	@Autowired
	private ISysUserService sysUserService;

    public static void main(String[] args) throws IOException {
        File orgTemplateFile = new File("C:\\Users\\lenovo\\Desktop\\TIM人员及组织信息/组织模板.xls");
        FileUtils.touch(orgTemplateFile); //覆盖文件
        ExcelUtil2<SysOrgCopy> util = new ExcelUtil2<>(SysOrgCopy.class);
        util.exportExcel(Lists.<SysOrgCopy>newArrayList(), "组织信息", new FileOutputStream(orgTemplateFile), null);

        File userTemplateFile = new File("C:\\Users\\lenovo\\Desktop\\TIM人员及组织信息/人员模板.xls");
        FileUtils.touch(userTemplateFile); //覆盖文件
        ExcelUtil2<SysUser> util1 = new ExcelUtil2<>(SysUser.class);
        util1.exportExcel(Lists.<SysUser>newArrayList(), "人员信息", new FileOutputStream(userTemplateFile), null);
        System.out.println("Finish create template files.");
    }

    @Test
	public void importSysOrgCopy() throws FileNotFoundException{
		File importFile = new File("C:\\Users\\lenovo\\Desktop\\TIM人员及组织信息/组织0501new.xls");
		FileInputStream fis = new FileInputStream(importFile);
		ExcelUtil2<SysOrgCopy> util = new ExcelUtil2<SysOrgCopy>(SysOrgCopy.class);
		List<SysOrgCopy> list = util.importExcel("导入数据", fis);
		for(SysOrgCopy c:list){
			c.setCreateDate(DateUtil.getCurrent());
		}
        System.out.println("Want to create " + list.size() +" records.");
        int total = 0;
        List<SysOrgCopy> insertList = Lists.newArrayList();
        int count = 0;
		for(int i=0; i<list.size(); i++){
            insertList.add(list.get(i));
		    if(i + 1 == list.size()){
                total += batchCreateSysOrgCopy(insertList); //最后一批
            }
		    else { //每1000条一批
                if (count < 999) {
                    count++;
                } else {
                    total += batchCreateSysOrgCopy(insertList);
                    count = 0;
                    insertList = Lists.newArrayList();
                }
            }
        }
        System.out.println("Actually create " + total +" records.");
	}

	private int batchCreateSysOrgCopy(List<SysOrgCopy> list) {
	    try {
            int ret = sysOrgCopyService.batchCreate(list);
            System.out.println(ret);
            return ret;
        } catch (Exception e){
            Exceptions.printException(e);
        }
        return  0;
    }

	//@Test
	public void exportSysOrgCopy() throws IOException{
		File targetFile = new File("C:\\Users\\lenovo\\Desktop\\TIM人员及组织信息/组织0501-export.xls");
		FileUtils.touch(targetFile); //覆盖文件
		ExcelUtil2<SysOrgCopy> util = new ExcelUtil2<SysOrgCopy>(SysOrgCopy.class);
		SysOrgCopy param = new SysOrgCopy();
		param.setOrderByClause("timOrgParentId asc, timOrgId asc, orderBy asc");
		Collection<SysOrgCopy> list = sysOrgCopyService.getAll(param);
		util.exportExcel((List<SysOrgCopy>)list, "组织信息", new FileOutputStream(targetFile), null);
	}

    //@Test
    public void importRootSysOrg() {
        SysOrg root = new SysOrg(1);
        root.setOrgCode("00000000000000000000");
        root.setOrgName("河南移动");
        root.setDescription("中国移动通信集团河南有限公司");
        root.setRemark("集团省公司");
        root.setOrderBy(1);
        sysOrgAdvanceService.create(root);
    }

    /**
	 * 需要反复执行，直到实在没有数据可进行导入
	 * @throws FileNotFoundException
	 */
	//@Test
	public void importSysOrg() throws FileNotFoundException{
		List<SysOrgCopy> unSuccessImport = Lists.newArrayList();
		File importFile = new File("C:\\Users\\lenovo\\Desktop\\TIM人员及组织信息/组织0501-export.xls");
		FileInputStream fis = new FileInputStream(importFile);
		ExcelUtil2<SysOrgCopy> util = new ExcelUtil2<SysOrgCopy>(SysOrgCopy.class);
		List<SysOrgCopy> list = util.importExcel("导入数据", fis);
		SysOrg root = new SysOrg(1);
		for(SysOrgCopy copy:list){
			SysOrg o = new SysOrg();
			BeanUtils.copyProperties(copy, o);
			o.setShortName(copy.getOrgCode());
			o.setOrgCode(copy.getTimOrgId());
			if(copy.getTimOrgParentId().trim().equals("00000000000000000000")){
				o.setParent(root);
				try{
					int ret = sysOrgAdvanceService.create(o);
					if(ret < 1)
						unSuccessImport.add(copy);
				}catch(Exception e){
					unSuccessImport.add(copy);
				}
			}else{
				SysOrg parent = sysOrgAdvanceService.loadByUnique(copy.getTimOrgParentId());
				if(parent != null){
					o.setParent(parent);
					try{
						int ret = sysOrgAdvanceService.create(o);
						if(ret < 1)
							unSuccessImport.add(copy);
					}catch(Exception e){
						unSuccessImport.add(copy);
					}
				}
				else
					unSuccessImport.add(copy);
			}
		}
		util.exportExcel(unSuccessImport, "失败组织", new FileOutputStream(importFile), null);
	}

    public void importOtherUser() {
        //数据库直接写死supervisor、admin、manager、guest四个用户
    }

    @Test
	public void importSysUser() throws FileNotFoundException {
		List<SysUser> unSuccessImport = Lists.newArrayList();
		List<SysUser> successImport = Lists.newArrayList();
		File importFile = new File("C:\\Users\\lenovo\\Desktop\\TIM人员及组织信息\\人员0501new.xls");
		FileInputStream fis = new FileInputStream(importFile);
		ExcelUtil2<SysUser> util = new ExcelUtil2<SysUser>(SysUser.class);
		List<SysUser> list = util.importExcel("导入数据", fis);
		for(SysUser c:list){
			c.setCreateDate(DateUtil.getCurrent());
			c.setUpdateDate(c.getCreateDate());
			if(StringUtils.isNotEmpty(c.getPosition())) {
                String[] ps1 = StringUtils.split(c.getPosition(), ";");
                String[] ps2 = StringUtils.split(ps1[0], "-");
                c.setPosition(ps2[ps2.length - 1]);
            }
			SysOrg sysOrg = sysOrgAdvanceService.loadByUnique(c.getOrgCode());
			if(sysOrg == null){
				unSuccessImport.add(c);
			}else{
				c.setSysOrg(sysOrg);
				c.setHierarchyOrgIds(sysOrgAdvanceService.getHierarchyOrgIds(sysOrg.getId()));
				successImport.add(c);
			}
		}
		//开始写入
		for(SysUser u:successImport){
			try{
			    u.setPassword("8e5c428a4c0ae58b9c1bfd9d2203e63b5ffd208c");
			    u.setSalt("5581bf3f8d7e0569");
				int ret = sysUserService.createOrUpdateViaAdmin(u);
				if(ret < 1)
					unSuccessImport.add(u);
			}catch(Exception e){
			    e.printStackTrace();
				unSuccessImport.add(u);
			}
		}
		util.exportExcel(unSuccessImport, "失败人员", new FileOutputStream(importFile), null);
	}
	
}
