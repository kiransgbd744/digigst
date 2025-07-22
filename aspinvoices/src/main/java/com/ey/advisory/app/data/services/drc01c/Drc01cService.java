package com.ey.advisory.app.data.services.drc01c;

import java.util.List;

import com.ey.advisory.app.data.entities.drc01c.TblDrc01cChallanDetails;
import com.ey.advisory.app.data.services.drc.DrcGetRespDto;
import com.ey.advisory.app.data.services.drc.Reason;


public interface Drc01cService {

	public DrcGetRespDto getDRCGetDetails(List<String> gstin, String retPeriod,
			Long entityId, String refId);

	public String saveReasonDetails(List<Reason> reasonDetails);

	public String saveDifferentialDetails(String gstin, String taxPeriod,
			String refId, List<TblDrc01cChallanDetails> diffDetails);

}