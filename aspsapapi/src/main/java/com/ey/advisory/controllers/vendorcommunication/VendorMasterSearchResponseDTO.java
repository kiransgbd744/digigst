package com.ey.advisory.controllers.vendorcommunication;

import java.util.List;

import com.ey.advisory.app.itcmatching.vendorupload.VendorMappingRespDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class VendorMasterSearchResponseDTO {

	private int pageNum;
	private int pageSize;
	private int totalRecords;
	private int activeGstinCount;
	private int suspendedGstinCount;
	private int cancelledGstinCount;
	private int inactiveGstinCount;
	private int einvApplicableCount;
	private int einvNotApplicableCount;
	// to show the complete results
	private List<VendorMappingRespDto> vendorMappingRespDtoList;
	// for active records
	private List<VendorMappingRespDto> activeGstin;
	// for suspended records
	private List<VendorMappingRespDto> suspendedGstin;
	// cancelled
	private List<VendorMappingRespDto> cancelledGstin;
	// inactive
	private List<VendorMappingRespDto> inactiveGstin;
	// einv aplicable
	private List<VendorMappingRespDto> einvApplicable;
	// einv not aplicable
	private List<VendorMappingRespDto> einvNotApplicable;


}
