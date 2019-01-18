package com.fijo.cores.common.service.impl;


import com.fijo.cores.common.mapper.ProcessCodeHolderMapper;
import com.fijo.cores.common.model.ProcessCodeHolder;
import com.fijo.cores.common.service.IProcessCodeHolderService;
import com.fijo.cores.utils.AppCodeGenerator;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.configs.CoreConfig;
import com.fijo.cores.common.mapper.ProcessCodeHolderMapper;
import com.fijo.cores.common.model.ProcessCodeHolder;
import com.fijo.cores.common.service.IProcessCodeHolderService;
import com.fijo.cores.exceptions.CodeLengthOutOfLimitException;
import com.fijo.cores.exceptions.CodePersistFailedException;
import com.fijo.cores.service.impl.GenericMapperService;
import com.fijo.cores.utils.AppCodeGenerator;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.DateUtil;
import com.fijo.cores.utils.configs.CoreConfig;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.List;

@Service(value = "processCodeHolderService")
public class ProcessCodeHolderService extends GenericMapperService<ProcessCodeHolder, Long> implements IProcessCodeHolderService {

    @Autowired
    private CoreConfig config;

    private Integer countLength; //计数位数(目前所有流程位数相同，没有针对流程分别计算位数)

    private ProcessCodeHolderMapper mapper;

    @PostConstruct
    public void init() {
        countLength = Integer.valueOf(config.getValue("processCodeHolder.countLength"));
    }

    @Autowired
    public ProcessCodeHolderService(@Qualifier(value = "sqlSessionTemplateSimple") SqlSession sqlSession) {
        super(sqlSession);
        this.mapper = sqlSession.getMapper(ProcessCodeHolderMapper.class);
        super.setMapper(mapper);
    }

    private ProcessCodeHolder getLast(String prefix, String processDate) {
        ProcessCodeHolder o = new ProcessCodeHolder();
        o.setPrefix(prefix);
        o.setProcessDate(processDate);
        List<ProcessCodeHolder> all = (List<ProcessCodeHolder>) mapper.getAll(o);
        ProcessCodeHolder ret = all.size() > 0 ? all.get(0) : null;
        log.debug("@ProcessCodeHolderService get last object is: " + ret);
        return ret;
    }

    private ProcessCodeHolder getNext(String prefix, String processDate) {
        ProcessCodeHolder last = getLast(prefix, processDate);
        if (last == null) {
            ProcessCodeHolder next = new ProcessCodeHolder(prefix, processDate, 1, countLength);
            int persit = mapper.create(next);
            if (persit > 0 && next.getId() != null)
                return next;
            else
                throw new CodePersistFailedException();
        } else {
            boolean isLegalCountCode = lessThanMaxLength(countLength, last.getCountCode() + 1);
            if (!isLegalCountCode) {
                throw new CodeLengthOutOfLimitException();
            } else {
                last.setCountCode(last.getCountCode() + 1);
                int persit = mapper.update(last);
                if (persit > 0)
                    return last;
                else
                    throw new CodePersistFailedException();
            }
        }
    }

    public String getNextCode(String prefix) {
        ProcessCodeHolder next = getNext(prefix, DateUtil.getToday(DateUtil.datePattern2));
        return prefix + Constants.LINE + DateUtil.getToday(DateUtil.datePattern2) + Constants.LINE + AppCodeGenerator.addLeftZeroForNum(countLength, next.getCountCode());
    }
    public Integer getSequenceCode(String prefix) {
        ProcessCodeHolder next = getNext(prefix, null);
        return next.getCountCode();
    }

    private boolean lessThanMaxLength(Integer countLength, Integer countCode) {
        Integer max = 1;
        for (int i = 0; i < countLength; i++) {
            max = max * 10;
        }
        return max.compareTo(countCode) > 0 ? true:false;
    }
}
