package com.ey.advisory.app.gstr3b;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSignFilePanDetailsDTO;
import com.ey.advisory.core.dto.Gstr3bGetInvoicesReqDto;

import jakarta.servlet.http.HttpServletResponse;

public interface Gstr3BSignAndFileService {

	public Pair<Boolean, String> getGstr3BGstnSummary(
			Gstr3bGetInvoicesReqDto gstr3BSummaryDto);

	public void updateGstr3BTables(String taxperiod, String gstin,
			String signId, String ackNum);

	public List<Gstr3BFilingDetailsDTO> getGstr3bFilingDetails(String gstin,
			String taxPeriod);

	public List<Gstr3BSignFilePanDetailsDTO> getPanDetails(String gstin);

	public void downloadGstr3bErrorResp(String id,
			HttpServletResponse response);

}
