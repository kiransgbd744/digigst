package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



@Entity
@Table(name = "GSTR2_B2CS_DETAILS")
public class Gstr2B2csDetailsEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;
	
	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RET_PERIOD")
	protected String returnPeriod;

	@Expose
	@SerializedName("transType")
	@Column(name = "TRAN_TYPE")
	protected String transType;

	@Expose
	@SerializedName("month")
	@Column(name = "MONTH")
	protected String month;

	@Expose
	@SerializedName("orgPos")
	@Column(name = "ORG_POS")
	protected String orgPos;

	@Expose
	@SerializedName("orgHsnOrSac")
	@Column(name = "ORG_HSN_SAC")
	protected String orgHsnOrSac;

	@Expose
	@SerializedName("orgUom")
	@Column(name = "ORG_UOM")
	protected String orgUom;

	@Expose
	@SerializedName("orgQnt")
	@Column(name = "ORG_QNT")
	protected BigDecimal orgQnt = BigDecimal.ZERO;

	@Expose
	@SerializedName("orgRate")
	@Column(name = "ORG_RATE")
	protected BigDecimal orgRate = BigDecimal.ZERO;;

	@Expose
	@SerializedName("orgTaxVal")
	@Column(name = "ORG_TAX_VAL")
	protected BigDecimal orgTaxVal = BigDecimal.ZERO;;

	@Expose
	@SerializedName("orgCGstin")
	@Column(name = "ORG_EC_GSTIN")
	protected String orgCGstin;

	@Expose
	@SerializedName("orgSupVal")
	@Column(name = "ORG_SUP_VAL")
	protected BigDecimal orgSupVal = BigDecimal.ZERO;;

	@Expose
	@SerializedName("newPos")
	@Column(name = "NEW_POS")
	protected String newPos;

	@Expose
	@SerializedName("newHsnOrSac")
	@Column(name = "NEW_HSN_SAC")
	protected String newHsnOrSac;

	@Expose
	@SerializedName("newUom")
	@Column(name = "NEW_UOM")
	protected String newUom;

	@Expose
	@SerializedName("newQnt")
	@Column(name = "NEW_QNT")
	protected BigDecimal newQnt = BigDecimal.ZERO;;

	@Expose
	@SerializedName("newRate")
	@Column(name = "NEW_RATE")
	protected BigDecimal newRate = BigDecimal.ZERO;
	
	
	@Expose
    @SerializedName("newTaxVal")
    @Column(name = "NEW_TAX_VAL")
    protected BigDecimal newTaxVal = BigDecimal.ZERO;;

    @Expose
    @SerializedName("newGstin")
    @Column(name = "NEW_EC_GSTIN")
    protected String newGstin;

    @Expose
    @SerializedName("newSupVal")
    @Column(name = "NEW_SUP_VAL")
    protected BigDecimal newSupVal = BigDecimal.ZERO;;

    @Expose
    @SerializedName("igstAmt")
    @Column(name = "IGST_AMT")
    protected BigDecimal igstAmt = BigDecimal.ZERO;;

    @Expose
    @SerializedName("cgstAmt")
    @Column(name = "CGST_AMT")
    protected BigDecimal cgstAmt = BigDecimal.ZERO;;

    @Expose
    @SerializedName("sgstAmt")
    @Column(name = "SGST_AMT")
    protected BigDecimal sgstAmt = BigDecimal.ZERO;;

    @Expose
    @SerializedName("cessAmt")
    @Column(name = "CESS_AMT")
    protected BigDecimal cessAmt = BigDecimal.ZERO;;

    @Expose
    @SerializedName("totalValue")
    @Column(name = "TOT_VAL")
    protected BigDecimal totalValue = BigDecimal.ZERO;
    
    @Expose
    @SerializedName("b2csKey")
    @Column(name = "B2CS_KEY")
    protected String b2csKey;
    
    @Expose
    @SerializedName("derivedRetPeriod")
    @Column(name = "DERIVED_RET_PERIOD")
    protected Integer derivedRetPeriod;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getOrgPos() {
		return orgPos;
	}

	public void setOrgPos(String orgPos) {
		this.orgPos = orgPos;
	}

	public String getOrgHsnOrSac() {
		return orgHsnOrSac;
	}

	public void setOrgHsnOrSac(String orgHsnOrSac) {
		this.orgHsnOrSac = orgHsnOrSac;
	}

	public String getOrgUom() {
		return orgUom;
	}

	public void setOrgUom(String orgUom) {
		this.orgUom = orgUom;
	}

	public BigDecimal getOrgQnt() {
		return orgQnt;
	}

	public void setOrgQnt(BigDecimal orgQnt) {
		this.orgQnt = orgQnt;
	}

	public BigDecimal getOrgRate() {
		return orgRate;
	}

	public void setOrgRate(BigDecimal orgRate) {
		this.orgRate = orgRate;
	}

	public BigDecimal getOrgTaxVal() {
		return orgTaxVal;
	}

	public void setOrgTaxVal(BigDecimal orgTaxVal) {
		this.orgTaxVal = orgTaxVal;
	}

	public String getOrgCGstin() {
		return orgCGstin;
	}

	public void setOrgCGstin(String orgCGstin) {
		this.orgCGstin = orgCGstin;
	}

	public BigDecimal getOrgSupVal() {
		return orgSupVal;
	}

	public void setOrgSupVal(BigDecimal orgSupVal) {
		this.orgSupVal = orgSupVal;
	}

	public String getNewPos() {
		return newPos;
	}

	public void setNewPos(String newPos) {
		this.newPos = newPos;
	}

	public String getNewHsnOrSac() {
		return newHsnOrSac;
	}

	public void setNewHsnOrSac(String newHsnOrSac) {
		this.newHsnOrSac = newHsnOrSac;
	}

	public String getNewUom() {
		return newUom;
	}

	public void setNewUom(String newUom) {
		this.newUom = newUom;
	}

	public BigDecimal getNewQnt() {
		return newQnt;
	}

	public void setNewQnt(BigDecimal newQnt) {
		this.newQnt = newQnt;
	}

	public BigDecimal getNewRate() {
		return newRate;
	}

	public void setNewRate(BigDecimal newRate) {
		this.newRate = newRate;
	}

	public BigDecimal getNewTaxVal() {
		return newTaxVal;
	}

	public void setNewTaxVal(BigDecimal newTaxVal) {
		this.newTaxVal = newTaxVal;
	}

	public String getNewGstin() {
		return newGstin;
	}

	public void setNewGstin(String newGstin) {
		this.newGstin = newGstin;
	}

	public BigDecimal getNewSupVal() {
		return newSupVal;
	}

	public void setNewSupVal(BigDecimal newSupVal) {
		this.newSupVal = newSupVal;
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

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public String getB2csKey() {
		return b2csKey;
	}

	public void setB2csKey(String b2csKey) {
		this.b2csKey = b2csKey;
	}

	public Integer getDerivedRetPeriod() {
		return derivedRetPeriod;
	}

	public void setDerivedRetPeriod(Integer derivedRetPeriod) {
		this.derivedRetPeriod = derivedRetPeriod;
	}

	@Override
	public String toString() {
		return "Gstr2B2csDetailsEntity [id=" + id + ", fileId=" + fileId
				+ ", sgstin=" + sgstin + ", returnPeriod=" + returnPeriod
				+ ", transType=" + transType + ", month=" + month + ", orgPos="
				+ orgPos + ", orgHsnOrSac=" + orgHsnOrSac + ", orgUom=" + orgUom
				+ ", orgQnt=" + orgQnt + ", orgRate=" + orgRate + ", orgTaxVal="
				+ orgTaxVal + ", orgCGstin=" + orgCGstin + ", orgSupVal="
				+ orgSupVal + ", newPos=" + newPos + ", newHsnOrSac="
				+ newHsnOrSac + ", newUom=" + newUom + ", newQnt=" + newQnt
				+ ", newRate=" + newRate + ", newTaxVal=" + newTaxVal
				+ ", newGstin=" + newGstin + ", newSupVal=" + newSupVal
				+ ", igstAmt=" + igstAmt + ", cgstAmt=" + cgstAmt + ", sgstAmt="
				+ sgstAmt + ", cessAmt=" + cessAmt + ", totalValue="
				+ totalValue + ", b2csKey=" + b2csKey + ", derivedRetPeriod="
				+ derivedRetPeriod + "]";
	}
}



