package com.ey.advisory.app.signinfile;

import java.time.LocalDateTime;
import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr1SignAndFileService {

	public void updateGstr1Tables(String taxperiod, String gstin, String signId, String ackNum, LocalDateTime now);

	public Pair<Boolean, String> getGstr1GstnSummary(Gstr1GetInvoicesReqDto gstr1SummaryDto);

	public List<Gstr3BFilingDetailsDTO> getGstr1FilingDetails(String gstin, String taxPeriod);
	
	public void downloadGstr1ErrorResp(String id,HttpServletResponse response);

}
