package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;

public class Gstr1DataStatusBasicsectionDto {
	
	
	List<Gstr1DataStatusSectionDto> dataStatus =new ArrayList<>();

	public List<Gstr1DataStatusSectionDto> getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(List<Gstr1DataStatusSectionDto> dataStatus) {
		this.dataStatus = dataStatus;
	}
	
	

}
