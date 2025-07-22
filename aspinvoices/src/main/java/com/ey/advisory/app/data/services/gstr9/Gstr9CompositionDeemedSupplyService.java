/**
 * 
 */
package com.ey.advisory.app.data.services.gstr9;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9CompositionDeemedSupplyDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9CompositionDeemedSupplyMapDto;

/**
 * @author vishal.verma
 *
 */
public interface Gstr9CompositionDeemedSupplyService {

	public List<Gstr9CompositionDeemedSupplyDto> getGstr9CompositionDeemedSupplyDetails(
			String gstin, String taxPeriod, String formattedFy);

	public String saveGstr9CompositionDeemedSupplyUserInputData(String gstin,
			String fy, String status,
			List<Gstr9CompositionDeemedSupplyMapDto> userInputList);

}
