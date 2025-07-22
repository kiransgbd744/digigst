package com.ey.advisory.app.docs.dto.anx1a;

import java.util.ArrayList;
import java.util.List;

public class Anx1aSummaryRespDto {
	private List<Anx1aSummaryInwardOutwardRespDto> outward = new ArrayList<>();
	private List<Anx1aSummaryInwardOutwardRespDto> inward = new ArrayList<>();
	private List<Anx1aSummarySuppliesRespDto> supplies = new ArrayList<>();

	public List<Anx1aSummaryInwardOutwardRespDto> getOutward() {
		return outward;
	}

	public void setOutward(List<Anx1aSummaryInwardOutwardRespDto> outward) {
		this.outward = outward;
	}

	public List<Anx1aSummaryInwardOutwardRespDto> getInward() {
		return inward;
	}

	public void setInward(List<Anx1aSummaryInwardOutwardRespDto> inward) {
		this.inward = inward;
	}

	public List<Anx1aSummarySuppliesRespDto> getSupplies() {
		return supplies;
	}

	public void setSupplies(List<Anx1aSummarySuppliesRespDto> supplies) {
		this.supplies = supplies;
	}

}
