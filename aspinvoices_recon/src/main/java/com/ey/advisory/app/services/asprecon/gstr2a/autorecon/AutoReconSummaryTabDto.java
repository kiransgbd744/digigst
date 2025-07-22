package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AutoReconSummaryTabDto {

	private List<AutoReconSummaryDto> autoReconSummary;
	private String lastUpdatedOn;
}
