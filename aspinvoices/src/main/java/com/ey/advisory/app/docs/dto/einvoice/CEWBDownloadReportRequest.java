/**
 * 
 */
package com.ey.advisory.app.docs.dto.einvoice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class CEWBDownloadReportRequest extends SearchCriteria {

	public CEWBDownloadReportRequest() {
		super(SearchTypeConstants.REPORTS_SEARCH);
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("CEWBFrom")
	private LocalDate cewbFrom;

	@Expose
	@SerializedName("CEWBTo")
	private LocalDate cewbTo;

	@Expose
	@SerializedName("CEWBNo")
	private String cewbNum;

	@Expose
	@SerializedName("PreviousCEWBNo")
	private String previousCewbNo;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("items")
	private List<CEWBDownloadReportChildRequest> items = new ArrayList<>();

}
