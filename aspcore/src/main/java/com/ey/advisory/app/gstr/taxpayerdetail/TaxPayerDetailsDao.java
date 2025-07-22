package com.ey.advisory.app.gstr.taxpayerdetail;

import com.ey.advisory.core.api.APIResponse;

public interface TaxPayerDetailsDao {
	
	public APIResponse findTaxPayerDetails(String gstin, String groupCode);

}
