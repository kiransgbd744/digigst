
package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

public interface Anx2ReconResponseDao {

	public List<Anx2ReconRespResultSetDataDTO> getA2ReconData(
			List<String> gstins, String taxPeriod, List<String> tableType,
			List<String> docType, String response);

	public List<Anx2ReconRespResultSetDataDTO> getPRReconData(
			List<String> gstins, String taxPeriod, List<String> tableType,
			List<String> docType, String response);

	public List<Anx2ReconRespResultSetDataDTO> getPRAReconData(
			List<String> gstins, String taxPeriod, List<String> tableType,
			List<String> docType, String response);

}
