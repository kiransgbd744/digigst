package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
public class OverallFilingStatusDto {

	private String vendorPan;
	private String vendorName;
	private List<OverAllPanStatusDto> overAllPanStatus;
	private List<GstinWiseFilingStatus> gstinWiseFilingStatusMonthwise;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((vendorName == null) ? 0 : vendorName.hashCode());
		result = prime * result
				+ ((vendorPan == null) ? 0 : vendorPan.hashCode());
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
		OverallFilingStatusDto other = (OverallFilingStatusDto) obj;
		if (vendorName == null) {
			if (other.vendorName != null)
				return false;
		} else if (!vendorName.equals(other.vendorName))
			return false;
		if (vendorPan == null) {
			if (other.vendorPan != null)
				return false;
		} else if (!vendorPan.equals(other.vendorPan))
			return false;
		return true;
	}

}
