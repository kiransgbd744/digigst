package com.ey.advisory.app.gstr2.reconresults.filter;

import java.io.IOException;

public interface Gstr2ReconResponseErrorDao {

	public String getErrorData(Long batchId) throws IOException;
 
}
