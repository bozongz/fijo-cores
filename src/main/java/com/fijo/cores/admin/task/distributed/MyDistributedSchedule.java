/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.admin.task.distributed;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 用途：测试任务样例
 * 作者: zhangbo
 * 时间: 2016-11-30  18:57
 */
@Component
public class MyDistributedSchedule {
    protected transient final Log log = LogFactory.getLog(getClass());

    @Autowired
    private DistributedJobExecutor distributedJobExecutor;


    @Autowired
    private DistributedMasterUtil masterUtil;

    @Scheduled(cron = "0/5 * * * * ?")
    public void execute() {
        if (distributedJobExecutor.checkMasterIsMe()) {
            log.trace(masterUtil.getServerIP() + ":" + masterUtil.getServerPort() + " is running jog------");
        }
    }
}
