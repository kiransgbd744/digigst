package com.ey.advisory.core.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReturnStusFilingDto {
	
	private String gstin;
	private Long entityId;
	private List<ReturnPeriodDto> returnperiods;
	
	@Data
	public static class  ReturnPeriodDto{
		private String month;
		private String status;
		private String time;
		private String arnNo;
	}

}
