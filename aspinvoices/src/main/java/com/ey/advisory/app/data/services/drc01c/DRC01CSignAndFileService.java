package com.ey.advisory.app.data.services.drc01c;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.javatuples.Pair;

import com.ey.advisory.app.data.services.drc.DRC01BGetInvoicesReqDto;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSignFilePanDetailsDTO;

public interface DRC01CSignAndFileService {

	public Pair<Boolean, String> getDrc01CGstnSummary(
			DRC01BGetInvoicesReqDto gstr3BSummaryDto);

	public void updateDrc01CTables(String taxperiod, String gstin,
			String signId, String ackNum);

	public List<Gstr3BFilingDetailsDTO> getDrc01CFilingDetails(String gstin,
			String taxPeriod);

	public List<Gstr3BSignFilePanDetailsDTO> getPanDetails(String gstin);

	public void downloadDRC01CErrorResp(String id,
			HttpServletResponse response);

}
