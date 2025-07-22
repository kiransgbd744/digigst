package com.ey.advisory.app.data.services.compliancerating;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MonthWiseComplianceRatingStatus {

	private String month;
	private String gstr1Status;
	private String gstr3bStatus;
	private BigDecimal rating;
	private BigDecimal gstr1Rating;
	private BigDecimal gstr3BRating;
	private boolean isFiledOnTime;
	private boolean isGstr3BFiledOnTime;
	private String gstr1ReturnFilingDate;
	private String gstr3BReturnFilingDate;
}
