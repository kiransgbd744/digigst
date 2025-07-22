package com.ey.advisory.app.data.entities.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "ANX_ERROR_B2C")
public class OutwardB2cErrorEntity {
	
	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;
	
	@Expose
	@SerializedName("retType")
	@Column(name = "RETURN_TYPE")
	protected String retType;
	
	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;
	
	@Expose
	@SerializedName("retPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String retPeriod;
	
	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@SerializedName("diffPercent")
	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;
	
	@Expose
	@SerializedName("sec7OfIgstFlag")
	@Column(name = "SEC7_OF_IGST_FLAG")
	protected String sec7OfIgstFlag;
	
	@Expose
	@SerializedName("autoPopulateToRefund")
	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopulateToRefund;
	
	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos;
	
	@Expose
	@SerializedName("hsnSac")
	@Column(name = "HSNSAC")
	protected String hsnSac;
	
	@Expose
	@SerializedName("uom")
	@Column(name = "UOM")
	protected String uom;
	
	@Expose
	@SerializedName("quentity")
	@Column(name = "QUANTITY")
	protected String quentity;

	@Expose
	@SerializedName("rate")
	@Column(name = "RATE")
	protected String rate;
	
	@Expose
	@SerializedName("stateApplyCess")
	@Column(name = "STATE_APPLYING_CESS")
	protected String stateApplyCess;
	
	@Expose
	@SerializedName("stateCessRate")
	@Column(name = "STATE_CESS_RATE")
	protected String stateCessRate;

	@Expose
	@SerializedName("taxableValue")
	@Column(name = "TAXABLE_VAL")
	protected String taxableValue ;
	
	@Expose
	@SerializedName("tcsFlag")
	@Column(name = "TCS_FLAG")
	protected String tcsFlag;
	
	@Expose
	@SerializedName("ecomGstin")
	@Column(name = "ECOM_GSTIN")
	protected String ecomGstin;
	
	@Expose
	@SerializedName("ecomValueSuppMade")
	@Column(name = "ECOM_VAL_SUPP_MADE")
	protected String ecomValueSuppMade ;

	@Expose
	@SerializedName("ecomValSuppRet")
	@Column(name = "ECOM_VAL_SUPP_RET")
	protected String ecomValSuppRet ;
	
	@Expose
	@SerializedName("ecomNetValSupp")
	@Column(name = "ECOM_NETVAL_SUP")
	protected String ecomNetValSupp ;
	
	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST")
	protected String igstAmt ;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST")
	protected String cgstAmt ;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST")
	protected String sgstAmt ;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS")
	protected String cessAmt ;
	
	@Expose
	@SerializedName("tcsAmt")
	@Column(name = "TCS_AMT")
	protected String tcsAmt ;

	@Expose
	@SerializedName("stateCessAmt")
	@Column(name = "STATE_CESS_AMT")
	protected String stateCessAmt ;

	@Expose
	@SerializedName("otherValue")
	@Column(name = "OTHER_VALUE")
	protected String otherValue ;

	@Expose
	@SerializedName("totalValue")
	@Column(name = "TOTAL_VALUE")
	protected String totalValue ;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTER")
	protected String profitCentre;
	
	@Expose
	@SerializedName("plant")
	@Column(name = "PLANT")
	protected String plant;
	
	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;
	
	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("salesOrganisation")
	@Column(name = "SALES_ORG")
	protected String salesOrganisation;
	
	@Expose
	@SerializedName("distributionChannel")
	@Column(name = "DISTRIBUTION_CHANNEL")
	protected String distributionChannel;
	
	@Expose
	@SerializedName("userAccess1")
	@Column(name = "USER_ACCESS1")
	protected String userAccess1;

	@Expose
	@SerializedName("userAccess2")
	@Column(name = "USER_ACCESS2")
	protected String userAccess2;
	
	@Expose
	@SerializedName("userAccess3")
	@Column(name = "USER_ACCESS3")
	protected String userAccess3;
	
	@Expose
	@SerializedName("userAccess4")
	@Column(name = "USER_ACCESS4")
	protected String userAccess4;
	
	@Expose
	@SerializedName("userAccess5")
	@Column(name = "USER_ACCESS5")
	protected String userAccess5;
	
	@Expose
	@SerializedName("userAccess6")
	@Column(name = "USER_ACCESS6")
	protected String userAccess6;
	
	@Expose
	@SerializedName("userDef1")
	@Column(name = "USER_DEFINED1")
	protected String userDef1;
	
	@Expose
	@SerializedName("userDef2")
	@Column(name = "USER_DEFINED2")
	protected String userDef2;
	
	@Expose
	@SerializedName("userDef3")
	@Column(name = "USER_DEFINED3")
	protected String userDef3;
	
	@Expose
	@SerializedName("b2cKey")
	@Column(name = "B2C_KEY")
	protected String b2cKey;
	
	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRetType() {
		return retType;
	}

	public void setRetType(String retType) {
		this.retType = retType;
	}

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(String diffPercent) {
		this.diffPercent = diffPercent;
	}

	public String getSec7OfIgstFlag() {
		return sec7OfIgstFlag;
	}

	public void setSec7OfIgstFlag(String sec7OfIgstFlag) {
		this.sec7OfIgstFlag = sec7OfIgstFlag;
	}

	public String getAutoPopulateToRefund() {
		return autoPopulateToRefund;
	}

	public void setAutoPopulateToRefund(String autoPopulateToRefund) {
		this.autoPopulateToRefund = autoPopulateToRefund;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getHsnSac() {
		return hsnSac;
	}

	public void setHsnSac(String hsnSac) {
		this.hsnSac = hsnSac;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getQuentity() {
		return quentity;
	}

	public void setQuentity(String quentity) {
		this.quentity = quentity;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getStateApplyCess() {
		return stateApplyCess;
	}

	public void setStateApplyCess(String stateApplyCess) {
		this.stateApplyCess = stateApplyCess;
	}

	public String getStateCessRate() {
		return stateCessRate;
	}

	public void setStateCessRate(String stateCessRate) {
		this.stateCessRate = stateCessRate;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(String taxableValue) {
		this.taxableValue = taxableValue;
	}

	public String getTcsFlag() {
		return tcsFlag;
	}

	public void setTcsFlag(String tcsFlag) {
		this.tcsFlag = tcsFlag;
	}

	public String getEcomGstin() {
		return ecomGstin;
	}

	public void setEcomGstin(String ecomGstin) {
		this.ecomGstin = ecomGstin;
	}

	public String getEcomValueSuppMade() {
		return ecomValueSuppMade;
	}

	public void setEcomValueSuppMade(String ecomValueSuppMade) {
		this.ecomValueSuppMade = ecomValueSuppMade;
	}

	public String getEcomValSuppRet() {
		return ecomValSuppRet;
	}

	public void setEcomValSuppRet(String ecomValSuppRet) {
		this.ecomValSuppRet = ecomValSuppRet;
	}

	public String getEcomNetValSupp() {
		return ecomNetValSupp;
	}

	public void setEcomNetValSupp(String ecomNetValSupp) {
		this.ecomNetValSupp = ecomNetValSupp;
	}

	public String getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(String igstAmt) {
		this.igstAmt = igstAmt;
	}

	public String getCgstAmt() {
		return cgstAmt;
	}

	public void setCgstAmt(String cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	public String getSgstAmt() {
		return sgstAmt;
	}

	public void setSgstAmt(String sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	public String getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(String cessAmt) {
		this.cessAmt = cessAmt;
	}

	public String getTcsAmt() {
		return tcsAmt;
	}

	public void setTcsAmt(String tcsAmt) {
		this.tcsAmt = tcsAmt;
	}

	public String getStateCessAmt() {
		return stateCessAmt;
	}

	public void setStateCessAmt(String stateCessAmt) {
		this.stateCessAmt = stateCessAmt;
	}

	public String getOtherValue() {
		return otherValue;
	}

	public void setOtherValue(String otherValue) {
		this.otherValue = otherValue;
	}

	public String getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
	}

	public String getProfitCentre() {
		return profitCentre;
	}

	public void setProfitCentre(String profitCentre) {
		this.profitCentre = profitCentre;
	}

	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSalesOrganisation() {
		return salesOrganisation;
	}

	public void setSalesOrganisation(String salesOrganisation) {
		this.salesOrganisation = salesOrganisation;
	}

	public String getDistributionChannel() {
		return distributionChannel;
	}

	public void setDistributionChannel(String distributionChannel) {
		this.distributionChannel = distributionChannel;
	}

	public String getUserAccess1() {
		return userAccess1;
	}

	public void setUserAccess1(String userAccess1) {
		this.userAccess1 = userAccess1;
	}

	public String getUserAccess2() {
		return userAccess2;
	}

	public void setUserAccess2(String userAccess2) {
		this.userAccess2 = userAccess2;
	}

	public String getUserAccess3() {
		return userAccess3;
	}

	public void setUserAccess3(String userAccess3) {
		this.userAccess3 = userAccess3;
	}

	public String getUserAccess4() {
		return userAccess4;
	}

	public void setUserAccess4(String userAccess4) {
		this.userAccess4 = userAccess4;
	}

	public String getUserAccess5() {
		return userAccess5;
	}

	public void setUserAccess5(String userAccess5) {
		this.userAccess5 = userAccess5;
	}

	public String getUserAccess6() {
		return userAccess6;
	}

	public void setUserAccess6(String userAccess6) {
		this.userAccess6 = userAccess6;
	}

	public String getUserDef1() {
		return userDef1;
	}

	public void setUserDef1(String userDef1) {
		this.userDef1 = userDef1;
	}

	public String getUserDef2() {
		return userDef2;
	}

	public void setUserDef2(String userDef2) {
		this.userDef2 = userDef2;
	}

	public String getUserDef3() {
		return userDef3;
	}

	public void setUserDef3(String userDef3) {
		this.userDef3 = userDef3;
	}

	public String getB2cKey() {
		return b2cKey;
	}

	public void setB2cKey(String b2cKey) {
		this.b2cKey = b2cKey;
	}
	
	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	@Override
	public String toString() {
		return "OutwardB2cErrorEntity [id=" + id + ", retType=" + retType
				+ ", sgstin=" + sgstin + ", retPeriod=" + retPeriod
				+ ", docType=" + docType + ", diffPercent=" + diffPercent
				+ ", sec7OfIgstFlag=" + sec7OfIgstFlag
				+ ", autoPopulateToRefund=" + autoPopulateToRefund + ", pos="
				+ pos + ", hsnSac=" + hsnSac + ", uom=" + uom + ", quentity="
				+ quentity + ", rate=" + rate + ", stateApplyCess="
				+ stateApplyCess + ", stateCessRate=" + stateCessRate
				+ ", taxableValue=" + taxableValue + ", tcsFlag=" + tcsFlag
				+ ", ecomGstin=" + ecomGstin + ", ecomValueSuppMade="
				+ ecomValueSuppMade + ", ecomValSuppRet=" + ecomValSuppRet
				+ ", ecomNetValSupp=" + ecomNetValSupp + ", igstAmt=" + igstAmt
				+ ", cgstAmt=" + cgstAmt + ", sgstAmt=" + sgstAmt + ", cessAmt="
				+ cessAmt + ", tcsAmt=" + tcsAmt + ", stateCessAmt="
				+ stateCessAmt + ", otherValue=" + otherValue + ", totalValue="
				+ totalValue + ", profitCentre=" + profitCentre + ", plant="
				+ plant + ", division=" + division + ", location=" + location
				+ ", salesOrganisation=" + salesOrganisation
				+ ", distributionChannel=" + distributionChannel
				+ ", userAccess1=" + userAccess1 + ", userAccess2="
				+ userAccess2 + ", userAccess3=" + userAccess3
				+ ", userAccess4=" + userAccess4 + ", userAccess5="
				+ userAccess5 + ", userAccess6=" + userAccess6 + ", userDef1="
				+ userDef1 + ", userDef2=" + userDef2 + ", userDef3=" + userDef3
				+ ", b2cKey=" + b2cKey + ", fileId=" + fileId + "]";
	}
	}