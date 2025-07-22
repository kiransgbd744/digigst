package com.ey.advisory.app.dashboard.recon.details;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author vishal.verma
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardReconDetailsDto implements Serializable {
	
	

	private static final long serialVersionUID = 1L;
	
	@Expose
	private RespSummaryDto reconResponse;
	
	@Expose
	private ReconSummaryDto reconSummary;
	
	
}
