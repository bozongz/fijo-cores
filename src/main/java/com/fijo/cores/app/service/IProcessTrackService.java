package com.fijo.cores.app.service;

import com.fijo.cores.app.model.ProcessTrack;
import com.fijo.cores.service.IGenericService;
import com.fijo.cores.app.model.ProcessTrack;
import com.fijo.cores.service.IGenericService;

public interface IProcessTrackService extends IGenericService<ProcessTrack, Long> {

	boolean checkAllParallelToJoin(ProcessTrack track, String joinId);
}
