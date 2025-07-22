package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DashboardAllSecDto {

	@Expose
	@SerializedName("entityId")
	private String entityId;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("authTokenTot")
	private int authTokenTot;

	@Expose
	@SerializedName("authTokenIna")
	private int authTokenIna;

	@Expose
	@SerializedName("gstrReturnStatus")
	private DashboardGstrReturnStatusDto gstrReturnStatusDto;

	@Expose
	@SerializedName("supplyStatus")
	private List<DashboardGstrSupplyDetailsDto> supplyDetailsDto;

	/*@Expose
	@SerializedName("ledgerDetails")
	private List<DashboardGstrLedgerStatusDto> ledgerDetails;*/

}
