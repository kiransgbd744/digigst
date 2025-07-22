package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Component
public class Anx1ReportSearchReqDto extends SearchCriteria {

	public Anx1ReportSearchReqDto() {
		super(SearchTypeConstants.REPORTS_SEARCH);
	}

	@Expose
	@SerializedName("dataType")
	private String dataType;

	@Expose
	@SerializedName("serviceOption")
	private String serviceOption;

	@Expose
	@SerializedName("cgstin")
	private List<String> gstins;

	@Expose
	@SerializedName("criteria")
	private String criteria;

	@Expose
	@SerializedName("dataRecvFrom")
	private LocalDate receivFromDate;

	@Expose
	@SerializedName("dataRecvTo")
	private LocalDate receivToDate;

	@Expose
	@SerializedName("docDateFrom")
	private LocalDate docDateFrom;

	@Expose
	@SerializedName("docDateTo")
	private LocalDate docDateTo;

	@Expose
	@SerializedName("taxPeriodFrom")
	private String returnFrom;

	@Expose
	@SerializedName("taxPeriodTo")
	private String returnTo;

	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("date")
	private LocalDate date;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("status")
	private String status;

	@Expose
	@SerializedName("type")
	private String type;

	@Expose
	@SerializedName("answer")
	private Integer answer;

	@Expose
	@SerializedName("tableType")
	private List<String> tableType;

	public List<String> getTableType() {
		return tableType;
	}

	public void setTableType(List<String> tableType) {
		this.tableType = tableType;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the gstins
	 */
	public List<String> getGstins() {
		return gstins;
	}

	/**
	 * @param gstins
	 *            the gstins to set
	 */
	public void setGstins(List<String> gstins) {
		this.gstins = gstins;
	}

	/**
	 * @return the criteria
	 */
	public String getCriteria() {
		return criteria;
	}

	/**
	 * @param criteria
	 *            the criteria to set
	 */
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	/**
	 * @return the receivFromDate
	 */
	public LocalDate getReceivFromDate() {
		return receivFromDate;
	}

	/**
	 * @param receivFromDate
	 *            the receivFromDate to set
	 */
	public void setReceivFromDate(LocalDate receivFromDate) {
		this.receivFromDate = receivFromDate;
	}

	/**
	 * @return the receivToDate
	 */
	public LocalDate getReceivToDate() {
		return receivToDate;
	}

	/**
	 * @param receivToDate
	 *            the receivToDate to set
	 */
	public void setReceivToDate(LocalDate receivToDate) {
		this.receivToDate = receivToDate;
	}

	/**
	 * @return the docDateFrom
	 */
	public LocalDate getDocDateFrom() {
		return docDateFrom;
	}

	/**
	 * @param docDateFrom
	 *            the docDateFrom to set
	 */
	public void setDocDateFrom(LocalDate docDateFrom) {
		this.docDateFrom = docDateFrom;
	}

	/**
	 * @return the docDateTo
	 */
	public LocalDate getDocDateTo() {
		return docDateTo;
	}

	/**
	 * @param docDateTo
	 *            the docDateTo to set
	 */
	public void setDocDateTo(LocalDate docDateTo) {
		this.docDateTo = docDateTo;
	}

	/**
	 * @return the returnFrom
	 */
	public String getReturnFrom() {
		return returnFrom;
	}

	/**
	 * @param returnFrom
	 *            the returnFrom to set
	 */
	public void setReturnFrom(String returnFrom) {
		this.returnFrom = returnFrom;
	}

	/**
	 * @return the returnTo
	 */
	public String getReturnTo() {
		return returnTo;
	}

	/**
	 * @param returnTo
	 *            the returnTo to set
	 */
	public void setReturnTo(String returnTo) {
		this.returnTo = returnTo;
	}

	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the answer
	 */
	public Integer getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(Integer answer) {
		this.answer = answer;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getServiceOption() {
		return serviceOption;
	}

	public void setServiceOption(String serviceOption) {
		this.serviceOption = serviceOption;
	}

	@Override
	public String toString() {
		return "Anx1ReportSearchReqDto [dataType=" + dataType
				+ ", serviceOption=" + serviceOption + ", gstins=" + gstins
				+ ", criteria=" + criteria + ", receivFromDate="
				+ receivFromDate + ", receivToDate=" + receivToDate
				+ ", docDateFrom=" + docDateFrom + ", docDateTo=" + docDateTo
				+ ", returnFrom=" + returnFrom + ", returnTo=" + returnTo
				+ ", returnPeriod=" + returnPeriod + ", taxPeriod=" + taxPeriod
				+ ", date=" + date + ", dataSecAttrs=" + dataSecAttrs
				+ ", entityId=" + entityId + ", status=" + status + ", type="
				+ type + ", answer=" + answer + "]";
	}
}
