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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespSummaryDto {

	@Expose
	private BigDecimal acceptAnx2Percent = BigDecimal.ZERO;

	@Expose
	private BigDecimal pendingAnx2Percent = BigDecimal.ZERO;

	@Expose
	private BigDecimal rejectAnx2Percent = BigDecimal.ZERO;

	@Expose
	private BigDecimal noActionAnx2Percent = BigDecimal.ZERO;

	@Expose
	private BigDecimal pendingPRPercent = BigDecimal.ZERO;

	@Expose
	private BigDecimal provisinalAddlPRCreditPercent = BigDecimal.ZERO;

	public RespSummaryDto merge(RespSummaryDto that) {
		BigDecimal accept = this.acceptAnx2Percent.add(that.acceptAnx2Percent);
		BigDecimal pendingAnx2 = this.pendingAnx2Percent
				.add(that.pendingAnx2Percent);
		BigDecimal rejectAnx2 = this.rejectAnx2Percent
				.add(that.rejectAnx2Percent);
		BigDecimal pendingPR = this.pendingPRPercent.add(that.pendingPRPercent);
		BigDecimal noActionAnx2 = this.noActionAnx2Percent
				.add(that.noActionAnx2Percent);
		BigDecimal provisinalAddlPRCredit = this.provisinalAddlPRCreditPercent
				.add(that.provisinalAddlPRCreditPercent);
		return new RespSummaryDto(accept, pendingAnx2, rejectAnx2, pendingPR,
				noActionAnx2, provisinalAddlPRCredit);
	}

}
