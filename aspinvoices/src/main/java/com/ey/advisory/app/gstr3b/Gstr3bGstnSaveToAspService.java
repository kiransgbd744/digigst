package com.ey.advisory.app.gstr3b;

import java.util.List;

public interface Gstr3bGstnSaveToAspService {
	/**
	 * This method is responsible to save the GSTN response into DB.
	 */
	public void saveGstnResponse(String gstin,String taxPeriod, 
			List<Gstr3BGstinsDto> gstnRes, List<Gstr3bTaxPaymentDto> taxRes, 
			String apiResp);

}
