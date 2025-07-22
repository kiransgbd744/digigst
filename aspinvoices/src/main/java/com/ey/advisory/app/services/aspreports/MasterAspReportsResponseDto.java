package com.ey.advisory.app.services.aspreports;

import java.util.ArrayList;
import java.util.List;

public class MasterAspReportsResponseDto {
	
	private List<MasterAspReportsParentDto> list = new ArrayList<MasterAspReportsParentDto>();

	public List<MasterAspReportsParentDto> getList() {
		return list;
	}

	public void setList(List<MasterAspReportsParentDto> list) {
		this.list = list;
	}
	
	
}
