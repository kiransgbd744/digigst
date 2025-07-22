/**
 * 
 */
package com.ey.advisory.app.anx1.recipientsummary;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Nikhil.Duseja
 *This Dto is to be used to bind the SR Summary Response Data
 *and this can be used to sum the Sr Summary at various level 
 *Recipient PAN, Recipient GSTIN, and Table and Doc Type Level
 *
 */

@Getter
@Setter
@NoArgsConstructor
public class RecipientSRSummaryDto implements Serializable {
	
	

	private static final long serialVersionUID = 1L;
	
	
	public RecipientSRSummaryDto(String level) {
		this.level = level;
	}

	@Expose
	private String cPan;
	
	@Expose
	private String cName;
	
	@Expose
	private String cgstin;
	
	@Expose
	private String tableType;
	
	@Expose
	private String docType;
	
	@Expose
	private Integer count = 0;
	
	@Expose
	private BigDecimal taxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal docAmt = BigDecimal.ZERO;
	
	@Expose
	private String level;
	
	public String getKey() {
		if ("L1".equals(level)) return cPan;
		if ("L2".equals(level)) return cPan + "#" + cgstin;
		return cPan + "#" + cgstin + "#" + tableType + "#" + docType;
	}

	public RecipientSRSummaryDto(String cPan, String cName, String cgstin,
			String tableType, String docType, Integer count,
			BigDecimal taxableVal, BigDecimal cess, BigDecimal igst,
			BigDecimal cgst, BigDecimal sgst, BigDecimal docAmt, String level) {
		super();
		this.cPan = cPan;
		this.cName = cName;
		this.cgstin = cgstin;
		this.tableType = tableType;
		this.docType = docType;
		this.count = count;
		this.taxableVal = taxableVal;
		this.cess = cess;
		this.igst = igst;
		this.cgst = cgst;
		this.sgst = sgst;
		this.docAmt = docAmt;
		this.level = level;
	}

	@Override
	public String toString() {
		return "RecipientSRSummaryDto [cPan=" + cPan + ", cName=" + cName
				+ ", cgstin=" + cgstin + ", taxDocType=" + tableType
				+ ", docType=" + docType + ", count=" + count + ", taxableVal="
				+ taxableVal + ", cess=" + cess + ", igst=" + igst + ", cgst="
				+ cgst + ", sgst=" + sgst + ", docAmt=" + docAmt + ", level="
				+ level + "]";
	}
	
	
}
