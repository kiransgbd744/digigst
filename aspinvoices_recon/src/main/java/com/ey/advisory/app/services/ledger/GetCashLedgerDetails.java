package com.ey.advisory.app.services.ledger;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface GetCashLedgerDetails {

	public String findCash(String jsonReq, String groupCode);
	public Workbook findCashReportDownload(String jsonReq, String groupCode);
	Workbook findCreditReportDownload(String jsonReq, String groupCode);
	public JasperPrint generatePdfReport(GetCashLedgerDetailsReqDto dto,
			String groupCode);
	public Workbook reversalAndReclaimExcelRpt(String jsonReq, String groupCode);
	
	public JasperPrint generateReversalAndReclaimPdfReport(GetCashLedgerDetailsReqDto dto,
			String groupCode);
	public Workbook rcmDetailscnExcelRpt(String jsonReq);
	public Workbook negativeLedgerDetailscnExcelRpt(String jsonReq);
	
}
