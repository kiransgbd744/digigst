package com.ey.advisory.core.dto;

import java.util.List;
import java.util.Map;

import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author BalaKrishna S
 *
 */
public class VendorSummaryReqDto extends SearchCriteria {
	
	/**
	 * @param searchType
	 */
	public VendorSummaryReqDto(String searchType) {
		super(searchType);
		// TODO Auto-generated constructor stub
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	/*@Expose
	@SerializedName("gstin")
	private List<String> gstins;

*/	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("vendorPan")
	private List<String> vendorPan;
	
	@Expose
	@SerializedName("vendorGstin")
	private List<String> vendorGstin;
	
	@Expose
	@SerializedName("data")
	private List<String> data;
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String,List<String>> dataSecAttrs;

	/**
	 * @return the entityId
	 */
	public List<Long> getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
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
	 * @param taxPeriod the taxPeriod to set
	 */
	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	/**
	 * @return the vendorPan
	 */
	public List<String> getVendorPan() {
		return vendorPan;
	}

	/**
	 * @param vendorPan the vendorPan to set
	 */
	public void setVendorPan(List<String> vendorPan) {
		this.vendorPan = vendorPan;
	}

	/**
	 * @return the vendorGstin
	 */
	public List<String> getVendorGstin() {
		return vendorGstin;
	}

	/**
	 * @param vendorGstin the vendorGstin to set
	 */
	public void setVendorGstin(List<String> vendorGstin) {
		this.vendorGstin = vendorGstin;
	}

	/**
	 * @return the data
	 */
	public List<String> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<String> data) {
		this.data = data;
	}

	/**
	 * @return the dataSecAttrs
	 */
	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	/**
	 * @param dataSecAttrs the dataSecAttrs to set
	 */
	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}
	
	

}
