package com.ey.advisory.app.data.services.compliancerating;

import java.util.List;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Data
public class VendorRatingCriteriaListDTO {

	private List<VendorRatingCriteriaDTO> rtngCriteriaList;
	private Long entityId;
	private String createdOn;
	private String source;
}
