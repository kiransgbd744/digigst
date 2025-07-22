package com.ey.advisory.app.gstr3b;

import java.util.List;

/**
 * 
 * @author Rajesh N K
 *
 */
public interface Gstr3BAutoCalcReportDownloadService {

	List<Gstr3BAutoCalcReportDownloadDto> getGstrList(String gstnResponse, String gstin);

}
