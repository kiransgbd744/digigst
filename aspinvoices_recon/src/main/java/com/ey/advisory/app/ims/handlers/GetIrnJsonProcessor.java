package com.ey.advisory.app.ims.handlers;

import java.util.List;

public interface GetIrnJsonProcessor{

	GetIrnJsonResponseProcessor getProcessorType(List<Long> reqIds);
}

