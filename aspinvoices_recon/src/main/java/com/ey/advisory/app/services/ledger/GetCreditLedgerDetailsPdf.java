package com.ey.advisory.app.services.ledger;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.ledger.GetCashITCBalanceReqDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * 
 * @author kiran s
 *
 */
public interface GetCreditLedgerDetailsPdf {

	public JasperPrint generateCreditPdfReport(GetCashLedgerDetailsReqDto dto,
			String groupCode);
	
	public JasperPrint generateLiabilityPdfReport(GetCashITCBalanceReqDto dto,
			String groupCode);
	
	
}
