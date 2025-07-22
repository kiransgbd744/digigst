package com.ey.advisory.app.data.services.drc;

import java.util.List;

import com.ey.advisory.app.data.entities.drc.TblDrcChallanDetails;

/**
 *
 * @author Siva.Reddy
 *
 */
public interface DrcService {

	public DrcGetRespDto getDRCGetDetails(List<String> gstin, String retPeriod,
			Long entityId, String refId);

	public String saveReasonDetails(List<Reason> reasonDetails);

	public String saveDifferentialDetails(String gstin, String taxPeriod,
			String refId, List<TblDrcChallanDetails> diffDetails);

}
