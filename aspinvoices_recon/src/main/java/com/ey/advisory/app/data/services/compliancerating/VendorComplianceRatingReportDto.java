package com.ey.advisory.app.data.services.compliancerating;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Component
@ToString
public class VendorComplianceRatingReportDto {

	private String vendorGSTIN;
	private String vendorName;
	private BigDecimal rating;
	private String sourceofGSTIN;
	private String taxPeriod;
	private String returnType;
	private String gstr1FilingDate;
	private String gstr3BFilingDate;
	private String gstr1ArnNo;
	private String gstr3BArnNo;
	private String gstr1StatusofReturnFiling;
	private String gstr3BStatusofReturnFiling;
	private String quarterlyorMonthlyfiler;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((returnType == null) ? 0 : returnType.hashCode());
		result = prime * result
				+ ((taxPeriod == null) ? 0 : taxPeriod.hashCode());
		result = prime * result
				+ ((vendorGSTIN == null) ? 0 : vendorGSTIN.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VendorComplianceRatingReportDto other = (VendorComplianceRatingReportDto) obj;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		if (taxPeriod == null) {
			if (other.taxPeriod != null)
				return false;
		} else if (!taxPeriod.equals(other.taxPeriod))
			return false;
		if (vendorGSTIN == null) {
			if (other.vendorGSTIN != null)
				return false;
		} else if (!vendorGSTIN.equals(other.vendorGSTIN))
			return false;
		return true;
	}
}
