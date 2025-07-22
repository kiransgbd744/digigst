package com.ey.advisory.app.itc04;

import java.time.LocalDateTime;
import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @author Sri Bhavya
 *
 */
public interface Itc04SignAndFileService {

	public void updateItc04Tables(String taxperiod, String gstin, String signId, String ackNum, LocalDateTime now);

	public Pair<Boolean, String> getItc04GstnSummary(Itc04GetInvoicesReqDto itc04SummaryDto);
	
	public void downloadItc04ErrorResp(String fileId, HttpServletResponse response);

	public List<Gstr3BFilingDetailsDTO> getItc04FilingDetails(String gstin, String taxPeriod);

}
