package com.ey.advisory.app.signinfile;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.javatuples.Pair;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr1ASignAndFileService {

	public void updateGstr1ATables(String taxperiod, String gstin, String signId, String ackNum, LocalDateTime now);

	public Pair<Boolean, String> getGstr1AGstnSummary(Gstr1GetInvoicesReqDto gstr1SummaryDto);

	public List<Gstr3BFilingDetailsDTO> getGstr1AFilingDetails(String gstin, String taxPeriod);
	
	public void downloadGstr1AErrorResp(String id,HttpServletResponse response);

}
