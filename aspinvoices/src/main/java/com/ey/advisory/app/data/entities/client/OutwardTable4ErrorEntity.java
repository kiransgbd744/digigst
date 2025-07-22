package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "ANX_ERROR_TABLE4")
public class OutwardTable4ErrorEntity {
	
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
	@SerializedName("ecomGstin")
	@Column(name = "ECOM_GSTIN")
	protected String ecomGstin;
	
	@Expose
	@SerializedName("valueOfSupMade")
	@Column(name = "VAL_OF_SUP_MADE")
	protected String valueOfSupMade;

	@Expose
	@SerializedName("valueOfSupRet")
	@Column(name = "VAL_OF_SUP_RET")
	protected String valueOfSupRet;
	
	@Expose
	@SerializedName("netValueOfSup")
	@Column(name = "NET_VAL_OF_SUP")
	protected String netValueOfSup;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST")
	protected String igstAmt;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST")
	protected String cgstAmt;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST")
	protected String sgstAmt;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	protected String cessAmt;
	
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
	@SerializedName("table4Key")
	@Column(name = "TABLE4_KEY")
	protected String table4Key;
	
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

	public String getEcomGstin() {
		return ecomGstin;
	}

	public void setEcomGstin(String ecomGstin) {
		this.ecomGstin = ecomGstin;
	}

	public String getValueOfSupMade() {
		return valueOfSupMade;
	}

	public void setValueOfSupMade(String valueOfSupMade) {
		this.valueOfSupMade = valueOfSupMade;
	}

	public String getValueOfSupRet() {
		return valueOfSupRet;
	}

	public void setValueOfSupRet(String valueOfSupRet) {
		this.valueOfSupRet = valueOfSupRet;
	}

	public String getNetValueOfSup() {
		return netValueOfSup;
	}

	public void setNetValueOfSup(String netValueOfSup) {
		this.netValueOfSup = netValueOfSup;
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
	
	public String getTable4Key() {
		return table4Key;
	}

	public void setTable4Key(String table4Key) {
		this.table4Key = table4Key;
	}
	
	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	@Override
	public String toString() {
		return "OutwardTable4ErrorEntity [id=" + id + ", retType=" + retType
				+ ", sgstin=" + sgstin + ", retPeriod=" + retPeriod
				+ ", ecomGstin=" + ecomGstin + ", valueOfSupMade="
				+ valueOfSupMade + ", valueOfSupRet=" + valueOfSupRet
				+ ", netValueOfSup=" + netValueOfSup + ", igstAmt=" + igstAmt
				+ ", cgstAmt=" + cgstAmt + ", sgstAmt=" + sgstAmt + ", cessAmt="
				+ cessAmt + ", profitCentre=" + profitCentre + ", plant="
				+ plant + ", division=" + division + ", location=" + location
				+ ", salesOrganisation=" + salesOrganisation
				+ ", distributionChannel=" + distributionChannel
				+ ", userAccess1=" + userAccess1 + ", userAccess2="
				+ userAccess2 + ", userAccess3=" + userAccess3
				+ ", userAccess4=" + userAccess4 + ", userAccess5="
				+ userAccess5 + ", userAccess6=" + userAccess6 + ", userDef1="
				+ userDef1 + ", userDef2=" + userDef2 + ", userDef3=" + userDef3
				+ ", table4Key=" + table4Key + ", fileId="
				+ fileId + "]";
	}
	
		}