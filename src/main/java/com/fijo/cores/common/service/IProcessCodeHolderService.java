package com.fijo.cores.common.service;

import com.fijo.cores.common.model.ProcessCodeHolder;
import com.fijo.cores.common.model.ProcessCodeHolder;
import com.fijo.cores.service.IGenericService;

public interface IProcessCodeHolderService extends IGenericService<ProcessCodeHolder, Long> {

    String getNextCode(String prefix);
    
    Integer getSequenceCode(String prefix);
}
