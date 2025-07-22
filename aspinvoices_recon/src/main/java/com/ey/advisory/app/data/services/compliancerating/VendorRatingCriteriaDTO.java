package com.ey.advisory.app.data.services.compliancerating;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Data
public class VendorRatingCriteriaDTO {

	private String returnType;
	private String dueType;
	private Integer fromDay;
	private Integer toDay;
	private BigDecimal rating;
	private LocalDateTime createdOn;
}
