package com.ey.advisory.app.signinfile;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.javatuples.Pair;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr6SignAndFileService {
	
	public void updateGstr6Tables(String taxperiod, String gstin, String signId, String ackNum, LocalDateTime now);

	public Pair<Boolean, String> getGstr6GstnSummary(Gstr6GetInvoicesReqDto gstr1SummaryDto);

	public List<Gstr3BFilingDetailsDTO> getGstr6FilingDetails(String gstin, String taxPeriod);

	public void downloadGstr6ErrorResp(String id, HttpServletResponse response);
}
