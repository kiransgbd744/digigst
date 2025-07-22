/**
 * 
 */
package com.ey.advisory.app.report.convertor;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.GSTR1EntityLevelSummaryDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportConvertor;

/**
 * @author Balakrishna.S
 *
 */
@Component("Gstr1EntityLevelReportConvertor")
public class Gstr1EntityLevelReportConvertor implements ReportConvertor {

	static final String OLDFARMATTER = "yyyy-MM-dd";
	static final String NEWFARMATTER = "dd-MM-yyyy";

	static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy hh:mm:ss a");
	
	
	@Override
	public Object convert(Object[] obj, String reportType) {
		
		GSTR1EntityLevelSummaryDto dto = new GSTR1EntityLevelSummaryDto();
		
		dto.setGSTIN((String) obj[0]);
		dto.setTableNo((String) obj[1]);
		dto.setTableDescription((String) obj[2]);
		dto.setTaxPeriod(DownloadReportsConstant.CSVCHARACTER.concat(obj[3].toString()));
		dto.setAspCount((GenUtil.getBigInteger(obj[4])).intValue());
		dto.setAspInvoiceValue((BigDecimal) obj[5]);
		dto.setAspTaxableValue((BigDecimal) obj[6]);
		dto.setAspIGST((BigDecimal) obj[7]);
		dto.setAspCGST((BigDecimal) obj[8]);
		dto.setAspSGST((BigDecimal) obj[9]);
		dto.setAspCess((BigDecimal) obj[10]);
		dto.setAvailableCount((GenUtil.getBigInteger(obj[11])).intValue());
		dto.setAvailableInvoiceValue((BigDecimal) obj[12]);
		dto.setAvailableTaxableValue((BigDecimal) obj[13]);
		dto.setAvailableIGST((BigDecimal) obj[14]);
		dto.setAvailableCGST((BigDecimal) obj[15]);
		dto.setAvailableSGST((BigDecimal) obj[16]);
		dto.setAvailableCess((BigDecimal) obj[17]);
		dto.setDiffCount((GenUtil.getBigInteger(obj[18])).intValue());
		dto.setDiffInvoiceValue((BigDecimal) obj[19]);
		dto.setDiffTaxableValue((BigDecimal) obj[20]);
		dto.setDiffIGST((BigDecimal) obj[21]);
		dto.setDiffCGST((BigDecimal) obj[22]);
		dto.setDiffSGST((BigDecimal) obj[23]);
		dto.setDiffCess((BigDecimal) obj[24]);
		
		
		return dto;
	}

}
