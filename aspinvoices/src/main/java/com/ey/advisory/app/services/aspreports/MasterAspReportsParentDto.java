package com.ey.advisory.app.services.aspreports;

import java.util.ArrayList;
import java.util.List;

public class MasterAspReportsParentDto {

	private String reportsName;
	private List<MasterAspReportsChildDto> items = new ArrayList<MasterAspReportsChildDto>();

	public String getReportsName() {
		return reportsName;
	}

	public void setReportsName(String reportsName) {
		this.reportsName = reportsName;
	}

	public List<MasterAspReportsChildDto> getItems() {
		return items;
	}

	public void setItems(List<MasterAspReportsChildDto> items) {
		this.items = items;
	}

}
