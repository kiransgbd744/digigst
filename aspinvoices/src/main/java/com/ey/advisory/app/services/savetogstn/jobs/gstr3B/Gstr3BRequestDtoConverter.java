package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSavetoGstnDTO;


public interface Gstr3BRequestDtoConverter {

	/**
	 * This method is responsible to convert the list of docs as batches 
	 * (Json Objects) with Maximum size is less than 5MB. 
	 *   
	 * @param objects
	 * @param section
	 * @param groupCode
	 * @param supplyType
	 * @return
	 */
	public Gstr3BSavetoGstnDTO convertToGstr3BObject(List<Object[]> objects,
			String section, String groupCode);

}
