
package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

public interface Anx2ReconResponseService {

	public Anx2ReconResponseDTO getReconResponse(List<String> gstins,
			String userName, List<String> tableType, List<String> docType);

}
