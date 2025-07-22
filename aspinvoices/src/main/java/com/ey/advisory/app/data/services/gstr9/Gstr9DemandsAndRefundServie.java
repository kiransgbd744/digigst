/**
 * 
 */
package com.ey.advisory.app.data.services.gstr9;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9DemandAndRefundMapDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9DemandAndrefundDashboardDto;

/**
 * @author vishal.verma
 *
 */
public interface Gstr9DemandsAndRefundServie {

	public List<Gstr9DemandAndrefundDashboardDto> getGstr9DemandsAndRefundDetails(
			String gstin, String taxPeriod, String formattedFy);

	public String saveGstr9DemandsAndRefundsUserInputData(String gstin, 
			String fy, String status, 
			List<Gstr9DemandAndRefundMapDto> userInputList);
}
