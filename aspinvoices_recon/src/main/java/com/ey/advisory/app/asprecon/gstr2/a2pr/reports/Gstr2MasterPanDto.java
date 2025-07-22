package com.ey.advisory.app.asprecon.gstr2.a2pr.reports;

import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconReportDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2MasterPanDto extends Gstr2InitiateReconReportDto{

	private String supPan2A;
	
	private String SupPanPR;
	
}
