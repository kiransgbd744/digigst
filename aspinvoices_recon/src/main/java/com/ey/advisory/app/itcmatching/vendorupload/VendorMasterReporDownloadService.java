package com.ey.advisory.app.itcmatching.vendorupload;

import java.io.IOException;
import java.util.List;

public interface VendorMasterReporDownloadService {
	
	public void getVendorMasterGstinReport(List<GstinDetailsdto> reqDto,Long id,Long entityId) throws IOException;
}
