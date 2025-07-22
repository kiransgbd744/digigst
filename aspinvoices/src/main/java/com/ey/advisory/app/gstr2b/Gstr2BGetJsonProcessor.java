package com.ey.advisory.app.gstr2b;

import java.util.List;

public interface Gstr2BGetJsonProcessor{

	Gstr2BGetJsonResponseProcessor getProcessorType(List<Long> reqIds);
}
