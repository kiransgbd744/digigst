package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;

public interface Gstr2InitiateReconReportDownloadDao {
	
	List<Gstr2InitiateReconReportDownloadDto> gstr2InitiateRecon(Gstr2InitiateReconDto request);

}
