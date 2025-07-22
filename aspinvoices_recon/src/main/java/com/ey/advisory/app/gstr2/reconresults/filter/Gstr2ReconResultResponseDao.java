package com.ey.advisory.app.gstr2.reconresults.filter;

public interface Gstr2ReconResultResponseDao {

	public String validateResponseAndErrorFileCrea(Long batchId, String reconType,
			Long fileId);
}
