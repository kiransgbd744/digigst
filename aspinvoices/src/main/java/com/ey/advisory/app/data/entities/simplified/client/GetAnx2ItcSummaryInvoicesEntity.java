/**
 * 
 */
package com.ey.advisory.app.data.entities.simplified.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "ANX2_GET_ITC_SUMMARY")
public class GetAnx2ItcSummaryInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CGSTIN")
	private String cgstin;

	@Column(name = "TAX_PERIOD")
	private String returnPeriod;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "DOC_VAL")
	private BigDecimal docVal;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	/*@ManyToOne
	@JoinColumn(name = "BATCH_ID", referencedColumnName = "ID", nullable = false)
	private GetGstr2aBatchEntity itcSumBatchIdAnx2;*/
	
	@Column(name = "BATCH_ID")
	private Long itcSumBatchIdAnx2;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public BigDecimal getDocVal() {
		return docVal;
	}

	public void setDocVal(BigDecimal docVal) {
		this.docVal = docVal;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	public Long getItcSumBatchIdAnx2() {
		return itcSumBatchIdAnx2;
	}

	public void setItcSumBatchIdAnx2(Long itcSumBatchIdAnx2) {
		this.itcSumBatchIdAnx2 = itcSumBatchIdAnx2;
	}

	/*public GetGstr2aBatchEntity getItcSumBatchIdAnx2() {
		return itcSumBatchIdAnx2;
	}

	public void setItcSumBatchIdAnx2(GetGstr2aBatchEntity itcSumBatchIdAnx2) {
		this.itcSumBatchIdAnx2 = itcSumBatchIdAnx2;
	}*/
	
	

}
