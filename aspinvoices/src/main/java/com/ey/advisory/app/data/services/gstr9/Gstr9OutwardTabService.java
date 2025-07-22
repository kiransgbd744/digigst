package com.ey.advisory.app.data.services.gstr9;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9GstinInOutwardDashBoardDTO;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9OutwardDashboardDTO;

public interface Gstr9OutwardTabService {

	Gstr9OutwardDashboardDTO getgstr9OutwardDashBoardData(String gstin,
			String taxPeriod, String fy);

	void saveGstr9OutwardUserInputData(String gstin, String fy,
			List<Gstr9GstinInOutwardDashBoardDTO> userInputList);

}
