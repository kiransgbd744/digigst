/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */
@Setter
@Getter
@ToString
public class Gstr1VerticalDownloadReportsReqDto extends SearchCriteria {

	public Gstr1VerticalDownloadReportsReqDto() {
		super(SearchTypeConstants.REPORTS_SEARCH);
	}

	@Expose
	@SerializedName("dataType")
	private String dataType;
	@Expose
	@SerializedName("fileId")
	private Long fileId;
	@Expose
	@SerializedName("type")
	private String type;
	@Expose
	@SerializedName("status")
	private String status;
	@Expose
	@SerializedName("fileType")
	private String fileType;
	@Expose
	@SerializedName("errorType")
	private String errorType;
}
