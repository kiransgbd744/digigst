package com.ey.advisory.app.gstr2.reconresults.filter;

public interface Gstr2ReconResultMultiUnlockDao {

	public void unlockResponseAndErrorFileCrea(Long batchId, String reconType,
			Long fileId);
}
