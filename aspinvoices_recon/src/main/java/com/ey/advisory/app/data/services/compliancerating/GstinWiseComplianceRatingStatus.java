package com.ey.advisory.app.data.services.compliancerating;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GstinWiseComplianceRatingStatus {

	private String vendorGstin;
	private String vendorName;
	private List<MonthWiseComplianceRatingStatus> monthWiseStatusCombination;
	private String filingType;
	private BigDecimal averageRating;
	private BigDecimal averageRatingWithPrevFy;
	private Integer ttlRetFiled;
	private Integer retFiledInTime;
	private Integer lateRetFiled;
	private Integer ttlRetNotFiled;
	private boolean isCancelled;
}
