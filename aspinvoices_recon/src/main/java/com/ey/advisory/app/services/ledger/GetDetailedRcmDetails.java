package com.ey.advisory.app.services.ledger;

import java.util.List;

import com.ey.advisory.app.docs.dto.ledger.RcmDetailsRespDto;

public interface GetDetailedRcmDetails {
	
	public List<RcmDetailsRespDto>  findRcmDetails(String jsonReq);


}
