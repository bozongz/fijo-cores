package com.fijo.cores.app.service.impl;

import java.util.Collection;

import com.fijo.cores.app.mapper.ProcessTrackMapper;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.model.ProcessTrack;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.app.service.IProcessTrackService;
import com.fijo.cores.service.impl.GenericMapperService;
import com.fijo.cores.utils.enums.ProcessEnum;
import com.fijo.cores.app.model.ProcessStep;
import com.fijo.cores.app.service.IProcessStepAdvanceService;
import com.fijo.cores.utils.enums.ProcessEnum;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fijo.cores.app.mapper.ProcessTrackMapper;
import com.fijo.cores.app.model.ProcessTrack;
import com.fijo.cores.app.service.IProcessTrackService;
import com.fijo.cores.service.impl.GenericMapperService;

@Service(value = "processTrackService")
public class ProcessTrackService extends GenericMapperService<ProcessTrack,Long> implements IProcessTrackService {
	private ProcessTrackMapper mapper;

    @Autowired
    private IProcessStepAdvanceService processStepAdvanceService;

	public ProcessTrackService(SqlSession sqlSession,
			Class<ProcessTrack> persistentMapper) {
		super(sqlSession, persistentMapper);
	}
		
	@Autowired
	public ProcessTrackService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.mapper = sqlSession.getMapper(ProcessTrackMapper.class);
		super.setMapper(mapper);
	}

    /**
     * 默认返回true，因为当ProcessTrack查无记录时，说明ProcessTrack已被删除，所有并行parallel节点到达join节点
     * @param track
     * @param joinId
     * @return
     */
	@Override
	public boolean checkAllParallelToJoin(ProcessTrack track, String joinId) {
        ProcessStep joinStep = processStepAdvanceService.loadByUnique(joinId);
        if(joinStep.getStepClass().equals(ProcessEnum.join)){
            boolean forkTojoin = true;
            Collection<ProcessTrack> tracks = getAll(track);
            for(ProcessTrack t : tracks){
                if(!t.getCurrentStepCode().equalsIgnoreCase(joinId)){
                    forkTojoin = false;
                    break;
                }
            }
            return forkTojoin;
        }else {
            return false;
        }

	}



}
