package com.ey.advisory.app.gstr2.recon.summary;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2ReconSummaryMasterDto {

	private List<Gstr2ReconSummaryDto> reconSummaryDto;

	private List<GstinDto> gstinList;

	private String toTaxPeriod2A;

	private String fromTaxPeriod2A;

	private String toTaxPeriodPR;

	private String fromTaxPeriodPR;

}
