package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;

@XmlRootElement(name="Gstr1DocSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class Gstr1DocSumJsonToXml {

	@SerializedName("gstin")
	@XmlElement(name="gstin")
	private String gstin ;
	
	@SerializedName("ret_period")
	@XmlElement(name="ret_period")
	private String retPeriod ;
	
	@SerializedName("chksum")
	@XmlElement(name="chksum")
	private String chkSum ;
	
	@SerializedName("sec_sum")
	@XmlElementWrapper(name="sec_sum")
	@XmlElement(name="data")
	private List<Gstr1DocSummaryJsonToXmldto> secSum;

	public String getGstin() {
		return gstin;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public String getChkSum() {
		return chkSum;
	}

		public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public void setChkSum(String chkSum) {
		this.chkSum = chkSum;
	}

	public List<Gstr1DocSummaryJsonToXmldto> getSecSum() {
		return secSum;
	}

	public void setSecSum(List<Gstr1DocSummaryJsonToXmldto> secSum) {
		this.secSum = secSum;
	}
	
}
