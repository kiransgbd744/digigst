package com.ey.advisory.app.docs.dto.anx2;

import java.util.ArrayList;

import java.util.List;

/**
 * 
 * @author Anand3.M
 *
 */

public class Anx2GetFinalSummaryDetailsResDto {
	private Anx2GetGstinSummaryDetailsResData GSTIN;
	private List<Anx2GetAnx2SummaryDetailsResDto> table = new ArrayList<>();

	public Anx2GetGstinSummaryDetailsResData getGSTIN() {
		return GSTIN;
	}

	public void setGSTIN(Anx2GetGstinSummaryDetailsResData gSTIN) {
		GSTIN = gSTIN;
	}

	public List<Anx2GetAnx2SummaryDetailsResDto> getTable() {
		return table;
	}

	public void setTable(List<Anx2GetAnx2SummaryDetailsResDto> table) {
		this.table = table;
	}

}
