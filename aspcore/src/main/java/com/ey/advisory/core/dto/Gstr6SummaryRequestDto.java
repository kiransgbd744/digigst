/**
 * 
 */
package com.ey.advisory.core.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Laxmi.Salukuti
 *
 */
public class Gstr6SummaryRequestDto {

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("qreturnPeriod")
	private String qreturnPeriod;
	
	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;
	
	@Expose
	@SerializedName("tableno")
	private List<String> tableno = new ArrayList<>();
	
	@Expose
	@SerializedName("listOfretPer")
	private List<String> listOfretPer = new ArrayList<>();
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;
	
	@SerializedName("fileId")
	private Long fileId;
	
	@Expose
	@SerializedName("gstnRefId")
	private String gstnRefId;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	@Expose
	@SerializedName("type")
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the entityId
	 */
	public List<Long> getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId
	 *            the entityId to set
	 */
	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the taxPeriod
	 */
	public String getTaxPeriod() {
		return taxPeriod;
	}

	/**
	 * @param taxPeriod
	 *            the taxPeriod to set
	 */
	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	/**
	 * @return the dataSecAttrs
	 */
	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	/**
	 * @param dataSecAttrs
	 *            the dataSecAttrs to set
	 */
	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public String getGstnRefId() {
		return gstnRefId;
	}

	public void setGstnRefId(String gstnRefId) {
		this.gstnRefId = gstnRefId;
	}

	/**
	 * @return the qreturnPeriod
	 */
	public String getQreturnPeriod() {
		return qreturnPeriod;
	}

	/**
	 * @param qreturnPeriod the qreturnPeriod to set
	 */
	public void setQreturnPeriod(String qreturnPeriod) {
		this.qreturnPeriod = qreturnPeriod;
	}

	/**
	 * @return the returnPeriod
	 */
	public String getReturnPeriod() {
		return returnPeriod;
	}

	/**
	 * @param returnPeriod the returnPeriod to set
	 */
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	/**
	 * @return the tableno
	 */
	public List<String> getTableno() {
		return tableno;
	}

	/**
	 * @param tableno the tableno to set
	 */
	public void setTableno(List<String> tableno) {
		this.tableno = tableno;
	}

	public List<String> getListOfretPer() {
		return listOfretPer;
	}

	public void setListOfretPer(List<String> listOfretPer) {
		this.listOfretPer = listOfretPer;
	}



	/**
	 * @return the tableno
	 */
	

}
