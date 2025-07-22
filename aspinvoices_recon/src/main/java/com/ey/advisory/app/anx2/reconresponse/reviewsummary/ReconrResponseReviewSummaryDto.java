package com.ey.advisory.app.anx2.reconresponse.reviewsummary;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author vishal.verma
 *
 */
@Setter
@Getter
public class ReconrResponseReviewSummaryDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private String level;
	
	@Expose
	private String tableType;
	
	@Expose
	private String docType;
	
	@Expose
	private String response;
	
	@Expose
	private BigDecimal exactMatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal matchUpToTolerance = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal valueMismatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal posMismatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal docDateMismatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal multiMismatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal docTypeMismatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal fuzzyMatch = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal addtionalAnx2 = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal addtionalPR = BigDecimal.ZERO;
	
	public String getKey() {
		if ("L1".equals(level)) return tableType;
		if ("L2".equals(level)) return tableType + "#" + docType;
		return tableType + "#" + docType + "#" + response;
	}

	

	public ReconrResponseReviewSummaryDto() {
		super();
	}
	


	public ReconrResponseReviewSummaryDto(String level) {
		super();
		this.level = level;
	}

	/**
	 * @param tableType
	 * @param docType
	 */
	public ReconrResponseReviewSummaryDto(String tableType, String docType) {
		super();
		this.tableType = tableType;
		this.docType = docType;
	}
	
	/**
	 * @param tableType
	 * @param docType
	 */
	public ReconrResponseReviewSummaryDto(String tableType, String docType,
			String level) {
		super();
		this.tableType = tableType;
		this.docType = docType;
		this.level = level;
	}



	/**
	 * @param level
	 * @param tableType
	 * @param docType
	 * @param response
	 * @param exactMatch
	 * @param matchUpToTolerance
	 * @param valueMismatch
	 * @param posMismatch
	 * @param docDateMismatch
	 * @param multiMismatch
	 * @param docTypeMismatch
	 * @param fuzzyMatch
	 * @param addtionalAnx2
	 * @param addtionalPR
	 */
	public ReconrResponseReviewSummaryDto(String level, String tableType,
			String docType, String response, BigDecimal exactMatch,
			BigDecimal matchUpToTolerance, BigDecimal valueMismatch,
			BigDecimal posMismatch, BigDecimal docDateMismatch,
			BigDecimal multiMismatch, BigDecimal docTypeMismatch,
			BigDecimal fuzzyMatch, BigDecimal addtionalAnx2,
			BigDecimal addtionalPR) {
		super();
		this.level = level;
		this.tableType = tableType;
		this.docType = docType;
		this.response = response;
		this.exactMatch = exactMatch;
		this.matchUpToTolerance = matchUpToTolerance;
		this.valueMismatch = valueMismatch;
		this.posMismatch = posMismatch;
		this.docDateMismatch = docDateMismatch;
		this.multiMismatch = multiMismatch;
		this.docTypeMismatch = docTypeMismatch;
		this.fuzzyMatch = fuzzyMatch;
		this.addtionalAnx2 = addtionalAnx2;
		this.addtionalPR = addtionalPR;
	}



	@Override
	public String toString() {
		return "ReconrResponseReviewSummaryDto [level=" + level + ", tableType="
				+ tableType + ", docType=" + docType + ", response=" + response
				+ ", exactMatch=" + exactMatch + ", matchUpToTolerance="
				+ matchUpToTolerance + ", valueMismatch=" + valueMismatch
				+ ", posMismatch=" + posMismatch + ", docDateMismatch="
				+ docDateMismatch + ", multiMismatch=" + multiMismatch
				+ ", docTypeMismatch=" + docTypeMismatch + ", fuzzyMatch="
				+ fuzzyMatch + ", addtionalAnx2=" + addtionalAnx2
				+ ", addtionalPR=" + addtionalPR + "]";
	}
	

}
