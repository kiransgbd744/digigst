package com.ey.advisory.app.dashboard.recon.details;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author vishal.verma
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReconSummaryDto {
	
	@Expose
	private  BigDecimal matchedPercent = BigDecimal.ZERO;
	
	@Expose
	private  BigDecimal mismatchedPercent = BigDecimal.ZERO;
	
	@Expose
	private  BigDecimal probableMatchedPercent = BigDecimal.ZERO;
	
	@Expose
	private  BigDecimal forcedMatchPercent = BigDecimal.ZERO;
	
	@Expose
	private  BigDecimal addlPR = BigDecimal.ZERO;
	
	@Expose
	private  BigDecimal addlA2 = BigDecimal.ZERO;
	
	public ReconSummaryDto merge(ReconSummaryDto that) {
		BigDecimal matched = this.matchedPercent.add(that.matchedPercent);
		BigDecimal mismatched = 
				this.mismatchedPercent.add(that.mismatchedPercent);		
		BigDecimal probableMatched = 
				this.probableMatchedPercent.add(that.probableMatchedPercent);
		BigDecimal forceMatched = 
				this.forcedMatchPercent.add(that.forcedMatchPercent);
		BigDecimal addlPR = this.addlPR.add(that.addlPR);
		BigDecimal addlA2 = this.addlA2.add(that.addlA2);
		return new ReconSummaryDto(matched, mismatched, probableMatched, 
				forceMatched, addlPR, addlA2);
	}
	
	

}
