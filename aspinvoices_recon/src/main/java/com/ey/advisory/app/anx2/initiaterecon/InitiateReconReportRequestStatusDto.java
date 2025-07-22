package com.ey.advisory.app.anx2.initiaterecon;

import java.io.Serializable;
import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
public class InitiateReconReportRequestStatusDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Expose
	private Long requestId = 0L;
	
	@Expose
	private Integer gstinCount = 0;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private List<GstinDto> gstins;
	
	/*@Expose
	private LocalDateTime initiatedOn;*/
	
	@Expose
	private String initiatedOn;
	
	@Expose
	private String initiatedBy;
	
	/*@Expose
	private LocalDateTime completionOn;*/
	
	@Expose
	private String completionOn;
	
	@Expose
	private String status;
	
	@Expose
	private String path;
	
	
	public InitiateReconReportRequestStatusDto() { }


	


	/**
	 * @param requestId
	 * @param gstinCount
	 * @param taxPeriod
	 * @param gstins
	 * @param initiatedOn
	 * @param initiatedBy
	 * @param completionOn
	 * @param status
	 * @param path
	 */
	public InitiateReconReportRequestStatusDto(Long requestId,
			Integer gstinCount, String taxPeriod, List<GstinDto> gstins,
			String initiatedOn, String initiatedBy,
			String completionOn, String status, String path) {
		super();
		this.requestId = requestId;
		this.gstinCount = gstinCount;
		this.taxPeriod = taxPeriod;
		this.gstins = gstins;
		this.initiatedOn = initiatedOn;
		this.initiatedBy = initiatedBy;
		this.completionOn = completionOn;
		this.status = status;
		this.path = path;
	}





	@Override
	public String toString() {
		return "InitiateReconReportRequestStatusDto [requestId=" + requestId
				+ ", gstinCount=" + gstinCount + ", taxPeriod=" + taxPeriod
				+ ", gstins=" + gstins + ", initiatedOn=" + initiatedOn
				+ ", initiatedBy=" + initiatedBy + ", completionOn="
				+ completionOn + ", status=" + status + ", path=" + path + "]";
	}


	
}
