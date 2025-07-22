/**
 * 
 */
package com.ey.advisory.app.docs.dto.einvoice;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Shashikant.Shukla
 */
@Component
public class GstinStatusDocSearchReqDto extends SearchCriteria {

	public GstinStatusDocSearchReqDto() {
		super(SearchTypeConstants.DOC_SEARCH);
	}

	@Expose
	@SerializedName("ReturnPeriod")
	private String returnPeriod;

	@Expose
	@SerializedName("tableType")
	private String tableType;

	@Expose
	@SerializedName("supplyType")
	private String supplyType;

	@Expose
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("docNums")
	private List<String> docNums;

	@Expose
	@SerializedName("docDate")
	private String docDate;

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("RecipientGstin")
	private String recipientGstin;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public List<String> getDocNums() {
		return docNums;
	}

	public void setDocNums(List<String> docNums) {
		this.docNums = docNums;
	}

	public String getDocDate() {
		return docDate;
	}

	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public String getRecipientGstin() {
		return recipientGstin;
	}

	public void setRecipientGstin(String recipientGstin) {
		this.recipientGstin = recipientGstin;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

}
