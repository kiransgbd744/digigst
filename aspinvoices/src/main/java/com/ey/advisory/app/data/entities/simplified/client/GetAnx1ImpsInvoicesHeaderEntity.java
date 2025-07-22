/**
 * 
 */
package com.ey.advisory.app.data.entities.simplified.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GETANX1_IMPS_HEADER")
public class GetAnx1ImpsInvoicesHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "POS")
	private String pos;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxable;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<GetAnx1ImpsInvoicesItemEntity> lineItems = new ArrayList<>();

	/*
	 * @ManyToOne
	 * 
	 * @JoinColumn(name = "BATCH_ID", referencedColumnName = "ID", nullable =
	 * false) private GetAnx1BatchEntity impsBatchId;
	 */
	@Column(name = "BATCH_ID")
	private Long impsBatchId;

	@Column(name = "GSTIN")
	private String sGstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@Column(name = "RFNDELG")
	private String rfndelg;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "UPLOAD_DOC_DATE")
	private LocalDate uploadDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getChkSum() {
		return chkSum;
	}

	public void setChkSum(String chkSum) {
		this.chkSum = chkSum;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public BigDecimal getTaxable() {
		return taxable;
	}

	public void setTaxable(BigDecimal taxable) {
		this.taxable = taxable;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	public List<GetAnx1ImpsInvoicesItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetAnx1ImpsInvoicesItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

	/*
	 * public GetAnx1BatchEntity getImpsBatchId() { return impsBatchId; }
	 * 
	 * public void setImpsBatchId(GetAnx1BatchEntity impsBatchId) {
	 * this.impsBatchId = impsBatchId; }
	 */

	public Long getImpsBatchId() {
		return impsBatchId;
	}

	public void setImpsBatchId(Long impsBatchId) {
		this.impsBatchId = impsBatchId;
	}

	public String getsGstin() {
		return sGstin;
	}

	public void setsGstin(String sGstin) {
		this.sGstin = sGstin;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public int getDerTaxPeriod() {
		return derTaxPeriod;
	}

	public void setDerTaxPeriod(int derTaxPeriod) {
		this.derTaxPeriod = derTaxPeriod;
	}

	public LocalDate getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(LocalDate uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getRfndelg() {
		return rfndelg;
	}

	public void setRfndelg(String rfndelg) {
		this.rfndelg = rfndelg;
	}

}
