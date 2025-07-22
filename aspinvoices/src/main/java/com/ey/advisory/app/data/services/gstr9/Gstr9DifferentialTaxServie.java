/**
 * 
 */
package com.ey.advisory.app.data.services.gstr9;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9DiffTaxDashboardDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9DiffTaxMapDto;

/**
 * @author vishal.verma
 *
 */
public interface Gstr9DifferentialTaxServie {
	
	public List<Gstr9DiffTaxDashboardDto> getGstr9DiffTaxDetails(String gstin,
			String taxPeriod, String formattedFy);
	
	public String  saveGstr9DiffTaxUserInputData(String gstin, String fy, 
			String status, List<Gstr9DiffTaxMapDto> userInputList);

}
