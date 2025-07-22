package com.ey.advisory.app.services.aspreports;

import java.util.ArrayList;
import java.util.List;

public class MasterAspReportsChildDto {

	private String reportsName;
	
	private List<MasterAspReportsChildItemDto> items = new ArrayList<>();
	public String getReportsName() {
		return reportsName;
	}
	public void setReportsName(String reportsName) {
		this.reportsName = reportsName;
	}
	public List<MasterAspReportsChildItemDto> getItems() {
		return items;
	}
	public void setItems(List<MasterAspReportsChildItemDto> items) {
		this.items = items;
	}
	
	
	
}
