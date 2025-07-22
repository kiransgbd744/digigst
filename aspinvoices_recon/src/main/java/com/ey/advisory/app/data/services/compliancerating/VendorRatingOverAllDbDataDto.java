package com.ey.advisory.app.data.services.compliancerating;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterApiEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorRatingCriteriaEntity;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;

import lombok.Data;

/**
 * @author Saif.S
 *
 */
@Data
public class VendorRatingOverAllDbDataDto {

	private List<String> stampedVendors;
	private List<String> taxPeriods;
	private List<VendorRatingCriteriaEntity> ratingCriteria;
	private Map<String, LocalDate> dueDateMap;
	private List<GstrReturnStatusEntity> returnStatusEntities;
	private Map<String, String> filingTypeMap;
	private List<VendorMasterUploadEntity> uploadEntities;
	private Map<String, VendorRegDateDto> dateMap;
	private List<VendorMasterApiEntity> uploadApiEntities;
	
	public VendorRatingOverAllDbDataDto(List<String> stampedVendors,
			List<String> taxPeriods,
			List<VendorRatingCriteriaEntity> ratingCriteria,
			Map<String, LocalDate> dueDateMap,
			List<GstrReturnStatusEntity> returnStatusEntities,
			Map<String, String> filingTypeMap,
			List<VendorMasterUploadEntity> uploadEntities,
			Map<String, VendorRegDateDto> dateMap,
			List<VendorMasterApiEntity> uploadApiEntities) {
		super();
		this.stampedVendors = stampedVendors;
		this.taxPeriods = taxPeriods;
		this.ratingCriteria = ratingCriteria;
		this.dueDateMap = dueDateMap;
		this.returnStatusEntities = returnStatusEntities;
		this.filingTypeMap = filingTypeMap;
		this.uploadEntities = uploadEntities;
		this.dateMap = dateMap;
		this.uploadApiEntities = uploadApiEntities;
	}

	public VendorRatingOverAllDbDataDto() {
		super();
	}
}
