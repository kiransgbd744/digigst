package com.ey.advisory.app.services.ledger;

import java.util.List;

import com.ey.advisory.app.docs.dto.ledger.NegativeDetailsRespDto;

public interface GetDetailedNegativeDetails {
	
	public List<NegativeDetailsRespDto>  findNegativeDetails(String jsonReq);


}
