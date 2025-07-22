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

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2ReconSummaryRespFinalDto {
	
	private Gstr2ReconSummaryDto absoluteMatch;
	
	private List<Gstr2ReconSummaryDto> mismatch;
	
	private Gstr2ReconSummaryDto multiMismatch;
	
	private Gstr2ReconSummaryDto docNumMismatch;
	
	private Gstr2ReconSummaryDto potentialGold;
	
	private Gstr2ReconSummaryDto potentialSilver;
	
	private Gstr2ReconSummaryDto addition2A;
	
	private Gstr2ReconSummaryDto addtionPR;
	
	private Gstr2ReconSummaryDto forceMatch;
	

}
