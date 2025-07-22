package com.ey.advisory.app.gstr3b;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gstr3bDashboardDto {

	private List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard;

	private String recentSavedDate;

	private String recentUpdatedDate;

	private String recentFilingDate;
	
	private boolean validationFlag;
	
	private boolean errorFlag;
	
	private boolean isActive;

}
