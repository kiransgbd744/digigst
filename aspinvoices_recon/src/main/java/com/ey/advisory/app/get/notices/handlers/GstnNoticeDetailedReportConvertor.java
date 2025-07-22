package com.ey.advisory.app.get.notices.handlers;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.ReportConvertor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GstnNoticeDetailedReportConvertor")
public class GstnNoticeDetailedReportConvertor 
				implements ReportConvertor {
	
	@Override
	public Object convert(Object[] arr, String reportType) {

		GstnNoticeDetailReportRespDto obj = new GstnNoticeDetailReportRespDto();
		
		obj.setEntityName((arr[0] != null) ? arr[0].toString() : null);
		obj.setGstin((arr[1] != null) ? arr[1].toString() : null);
		obj.setReferenceId((arr[2] != null) ? arr[2].toString() : null);
		obj.setAckNo((arr[3] != null) ? arr[3].toString() : null);
		obj.setDateOfIssue((arr[4] != null) ? arr[4].toString() : null);
		obj.setDueDate((arr[5] != null) ? arr[5].toString() : null);
		obj.setDateOfResponse((arr[6] != null) ? arr[6].toString() : null);
		obj.setNoticeStatus((arr[7] != null) ? arr[7].toString() : null);
		obj.setTimeLeftForResponse((arr[8] != null) ? arr[8].toString() : null);
		obj.setFromTaxPeriod((arr[9] != null) ? arr[9].toString() : null);
		obj.setToTaxPeriod((arr[10] != null) ? arr[10].toString() : null);
		obj.setNoticeType((arr[11] != null) ? arr[11].toString() : null);
		obj.setNoticeTypeDescription((arr[12] != null) ? arr[12].toString() : null);
		obj.setTaxableValue((arr[13] != null) ? arr[13].toString() : null);
		obj.setTotalTax((arr[14] != null) ? arr[14].toString() : null);
		obj.setUniqueModuleCode((arr[15] != null) ? arr[15].toString() : null);
		obj.setUniqueModuleDesc((arr[16] != null) ? arr[16].toString() : null);
		obj.setUniqueAlertCode((arr[17] != null) ? arr[17].toString() : null);
		obj.setUniqueAlertDesc((arr[18] != null) ? arr[18].toString() : null);
		obj.setMainDocType((arr[19] != null) ? arr[19].toString() : null);
		obj.setMainDocId((arr[20] != null) ? arr[20].toString() : null);
		obj.setMainDocHashCode((arr[21] != null) ? arr[21].toString() : null);
		/*obj.setSuppDocType(suppDocType);
		obj.setSuppDocId(suppDocId);
		obj.setSuppDocHashCode(suppDocHashCode);*/
		return obj;
	}


}
