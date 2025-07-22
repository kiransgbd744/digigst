package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Arun K.A
 *
 */
@Getter
@Setter
public class FileStatusReportDto {

	@Expose
	@SerializedName("dataType")
	private String dataType;

	@Expose
	@SerializedName("serviceOption")
	private String serviceOption;

	@Expose
	@SerializedName("gstins")
	private List<String> gstins;

	@Expose
	@SerializedName("fileId")
	private Long fileId;

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
	@SerializedName("errorType")
	private String errorType;

	@Expose
	@SerializedName("answer")
	private Integer answer;

	@Expose
	@SerializedName("completedOn")
	protected LocalDateTime completedOn;

	@Expose
	@SerializedName("derivedRetPeriod")
	protected Long derivedRetPeriod;

	@Expose
	@SerializedName("createdDate")
	protected LocalDateTime createdDate;

	@Expose
	@SerializedName("createdBy")
	protected String createdBy;

	@Expose
	@SerializedName("reportCateg")
	protected String reportCateg;

	@Expose
	@SerializedName("tableType")
	protected List<String> tableType;

	@Expose
	@SerializedName("docType")
	// private String docType;
	protected List<String> docType;

	@Expose
	@SerializedName("docCategory")
	protected List<String> docCategory;

	@Expose
	@SerializedName("eInvGenerated")
	protected List<String> eInvGenerated;
	
	@Expose
	@SerializedName("eWbGenerated")
	protected List<String> eWbGenerated;

	@Expose
	@SerializedName("autoDraftedGSTN")
	protected List<String> autoDraftedGSTN;
 
	@Expose
	@SerializedName("documentDateFrom")
	private LocalDate documentDateFrom;
	
	@Expose
	@SerializedName("documentDateTo")
	private LocalDate documentDateTo;
	
	@Expose
	@SerializedName("accVoucherDateFrom")
	private LocalDate accVoucherDateFrom;
	
	@Expose
	@SerializedName("accVoucherDateTo")
	private LocalDate accVoucherDateTo;
	
	@Expose
	@SerializedName("reconReportType")
	protected List<String> reconReportType;
	
	@Expose
	@SerializedName("reconType")
	protected String reconType;
	

	
}
