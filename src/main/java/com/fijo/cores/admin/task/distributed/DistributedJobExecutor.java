/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.admin.task.distributed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用途：
 * 作者: zhangbo
 * 时间: 2016-11-30  18:47
 */
@Component
public class DistributedJobExecutor {

    @Autowired
    private DistributedMasterUtil masterUtil;

    public boolean checkMasterIsMe() {
        return masterUtil.checkMasterIsMe();
    }
}
