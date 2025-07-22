package com.ey.advisory.app.data.services.gstr9;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9InwardDashboardDTO;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9InwardUserInputDTO;

/**
 * 
 * @author Jithendra.B
 *
 */
public interface Gstr9InwardService {

	Gstr9InwardDashboardDTO getgstr9InwardDashBoardData(String gstin,
			String fy);

	void saveGstr9InwardUserInputData(String gstin,String fy,String status,
			List<Gstr9InwardUserInputDTO> userInputList);
	
	List<Gstr9InwardUserInputDTO> getGstr9Inward7HPopUpData(String gstin,
			String fy);

}
