package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSavetoGstnDTO;

public interface Gstr3BApiCallHandler {

	/**
	 * This Method is responsible to do SaveToGstn Operation by accepting the 
	 * SaveBatchProcessDto objects and to update the Gstr1_Doc_Header and 
	 * Gstr1_save_batch tables.
	 * 
	 * @param batchDto
	 * @param groupCode
	 * @param section
	 * @return
	 */
	public void execute(Gstr3BSavetoGstnDTO batchDto,
			String groupCode);

}
