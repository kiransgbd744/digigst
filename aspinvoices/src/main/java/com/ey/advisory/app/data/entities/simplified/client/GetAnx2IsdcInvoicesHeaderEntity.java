package com.ey.advisory.app.data.entities.simplified.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GETANX2_ISDC_HEADER")
public class GetAnx2IsdcInvoicesHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SGSTIN")
	private String sgstin;

	@Column(name = "CGSTIN")
	private String cgstin;

	@Column(name = "CGSTIN_PAN")
	private String cgstinPan;

	@Column(name = "SGSTIN_PAN")
	private String sgstinPan;

	@Column(name = "D_TAXPERIOD")
	private String distbtrRetPeriod;

	@Column(name = "SUPPLIER_INV_NUM")
	private String invNum;

	@Column(name = "SUPPLIER_INV_DATE")
	private LocalDate invDate;

	@Column(name = "INV_TYPE")
	private String invType;

	@Column(name = "IS_AMENDED")
	private Boolean isAmended;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "TAX_PERIOD")
	private String returnPeriod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;
	
	@Column(name = "TABLE_SECTION")
	private String tableSection;

	/*@ManyToOne
	@JoinColumn(name = "BATCH_ID", referencedColumnName = "ID", nullable = false)
	private GetGstr2aBatchEntity isdcBatchIdAnx2;*/
	
	public Integer getDerivedRetPeriod() {
		return derivedRetPeriod;
	}

	public void setDerivedRetPeriod(Integer derivedRetPeriod) {
		this.derivedRetPeriod = derivedRetPeriod;
	}

	@Column(name = "BATCH_ID")
	private Long isdcBatchIdAnx2;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	public String getCgstinPan() {
		return cgstinPan;
	}

	public void setCgstinPan(String cgstinPan) {
		this.cgstinPan = cgstinPan;
	}

	public String getSgstinPan() {
		return sgstinPan;
	}

	public void setSgstinPan(String sgstinPan) {
		this.sgstinPan = sgstinPan;
	}

	public String getDistbtrRetPeriod() {
		return distbtrRetPeriod;
	}

	public void setDistbtrRetPeriod(String distbtrRetPeriod) {
		this.distbtrRetPeriod = distbtrRetPeriod;
	}

	public String getInvNum() {
		return invNum;
	}

	public void setInvNum(String invNum) {
		this.invNum = invNum;
	}

	public LocalDate getInvDate() {
		return invDate;
	}

	public void setInvDate(LocalDate invDate) {
		this.invDate = invDate;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public Boolean getIsAmended() {
		return isAmended;
	}

	public void setIsAmended(Boolean isAmended) {
		this.isAmended = isAmended;
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

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	/*public GetGstr2aBatchEntity getIsdcBatchIdAnx2() {
		return isdcBatchIdAnx2;
	}

	public void setIsdcBatchIdAnx2(GetGstr2aBatchEntity isdcBatchIdAnx2) {
		this.isdcBatchIdAnx2 = isdcBatchIdAnx2;
	}*/

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Long getIsdcBatchIdAnx2() {
		return isdcBatchIdAnx2;
	}

	public void setIsdcBatchIdAnx2(Long isdcBatchIdAnx2) {
		this.isdcBatchIdAnx2 = isdcBatchIdAnx2;
	}

	public String getTableSection() {
		return tableSection;
	}

	public void setTableSection(String tableSection) {
		this.tableSection = tableSection;
	}
	
	

	
}
