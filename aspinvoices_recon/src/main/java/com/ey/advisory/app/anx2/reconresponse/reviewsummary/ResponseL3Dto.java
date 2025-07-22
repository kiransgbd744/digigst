package com.ey.advisory.app.anx2.reconresponse.reviewsummary;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ResponseL3Dto {
	
	@Expose
	private String response;
	
	@Expose
	private BigDecimal exactMatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal matchWithTolerance = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal valueMismatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal posMismatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal docDateMismatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal docDateAndValueMismatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal docTypeMismatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal probable1 = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal peobable2 = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal fuzzyMatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal addtionalAnx2 = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal addtionalPR = BigDecimal.ZERO;
	
	
	@Override
	public String toString() {
		return "ResponseL3Dto [response=" + response + ", exactMatch="
				+ exactMatch + ", matchWithTolerance=" + matchWithTolerance
				+ ", valueMismatch=" + valueMismatch + ", posMismatch="
				+ posMismatch + ", docDateMismatch=" + docDateMismatch
				+ ", docDateAndValueMismatch=" + docDateAndValueMismatch
				+ ", docTypeMismatch=" + docTypeMismatch + ", probable1="
				+ probable1 + ", peobable2=" + peobable2 + ", fuzzyMatch="
				+ fuzzyMatch + ", addtionalAnx2=" + addtionalAnx2
				+ ", addtionalPR=" + addtionalPR + "]";
	}


	/**
	 * @param response
	 * @param exactMatch
	 * @param matchWithTolerance
	 * @param valueMismatch
	 * @param posMismatch
	 * @param docDateMismatch
	 * @param docDateAndValueMismatch
	 * @param docTypeMismatch
	 * @param probable1
	 * @param peobable2
	 * @param fuzzyMatch
	 * @param addtionalAnx2
	 * @param addtionalPR
	 */
	public ResponseL3Dto(String response, BigDecimal exactMatch,
			BigDecimal matchWithTolerance, BigDecimal valueMismatch,
			BigDecimal posMismatch, BigDecimal docDateMismatch,
			BigDecimal docDateAndValueMismatch, BigDecimal docTypeMismatch,
			BigDecimal probable1, BigDecimal peobable2, BigDecimal fuzzyMatch,
			BigDecimal addtionalAnx2, BigDecimal addtionalPR) {
		super();
		this.response = response;
		this.exactMatch = exactMatch;
		this.matchWithTolerance = matchWithTolerance;
		this.valueMismatch = valueMismatch;
		this.posMismatch = posMismatch;
		this.docDateMismatch = docDateMismatch;
		this.docDateAndValueMismatch = docDateAndValueMismatch;
		this.docTypeMismatch = docTypeMismatch;
		this.probable1 = probable1;
		this.peobable2 = peobable2;
		this.fuzzyMatch = fuzzyMatch;
		this.addtionalAnx2 = addtionalAnx2;
		this.addtionalPR = addtionalPR;
	}

	public ResponseL3Dto() {
		super();
	}


}
