package com.ey.advisory.app.data.services.drc;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSignFilePanDetailsDTO;

import jakarta.servlet.http.HttpServletResponse;

public interface DRC01BSignAndFileService {

	public Pair<Boolean, String> getDrc01BGstnSummary(
			DRC01BGetInvoicesReqDto gstr3BSummaryDto);

	public void updateDrc01BTables(String taxperiod, String gstin,
			String signId, String ackNum);

	public List<Gstr3BFilingDetailsDTO> getDrc01BFilingDetails(String gstin,
			String taxPeriod);

	public List<Gstr3BSignFilePanDetailsDTO> getPanDetails(String gstin);

	public void downloadDRC01BErrorResp(String id,
			HttpServletResponse response);

}
