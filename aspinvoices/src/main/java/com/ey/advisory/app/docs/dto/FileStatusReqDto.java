package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 
 * @author Mahesh.Golla
 *
 */

@Setter
@Getter
@ToString
public class FileStatusReqDto extends SearchCriteria {

	public FileStatusReqDto(String searchType) {
		super(SearchTypeConstants.FILE_STATUS_SEARCH);
	}

	@Expose
	@SerializedName("type")
	private String type;

	@Expose
	@SerializedName("criteria")
	private String criteria;

	@Expose
	@SerializedName("dataRecvFrom")
	private LocalDate dataRecvFrom;

	@Expose
	@SerializedName("dataRecvTo")
	private LocalDate dataRecvTo;

	@Expose
	@SerializedName("docDateFrom")
	private LocalDate docDateFrom;

	@Expose
	@SerializedName("docDateTo")
	private LocalDate docDateTo;

	@Expose
	@SerializedName("retPeriodFrom")
	private String retPeriodFrom;

	@Expose
	@SerializedName("retPeriodTo")
	private String retPeriodTo;

	@Expose
	@SerializedName("gstin")
	private List<String> sgstins = new ArrayList<>();
	
	@Expose
	@SerializedName("dataSecAttrs")
	private FileStatusDataSecReqDto fileStatusDataSecReqDto;

	@Expose
	@SerializedName("fileType")
	private String fileType;
	
	@Expose
	@SerializedName("dataType")
	private String dataType;
	
	@Expose
	@SerializedName("source")
	private String source;

	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
}