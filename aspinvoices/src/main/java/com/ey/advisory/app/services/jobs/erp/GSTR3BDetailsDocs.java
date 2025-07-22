package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.erp.Gstr3BDashboardSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3BDetailResponseDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3BPosDetailResponseDto;
import com.ey.advisory.app.docs.dto.erp.Gstrn3BFinalResultDto;
import com.ey.advisory.app.gstr3b.Gstr3BEntityDashboardDto;



public interface GSTR3BDetailsDocs {
	
	public  List<Gstr3BDetailResponseDto> convertDetailsDocsAsDtos(
			List<Object[]> objs, String entityName, String companyCode);

	public List<Gstr3BDashboardSummaryDto> convertSummarisedDocsAsDtos(
			List<Gstr3BEntityDashboardDto> gstinDashBoardDto, String entityName ,
			String companyCode);
	
	public List<Gstr3BEntityDashboardDto> get3BSummarisedData(String gstinNum, String taxPeriod);

	public Integer pushToErp(Gstrn3BFinalResultDto finalDto,
			String destination, AnxErpBatchEntity batch);
	
	public List<Object[]> get3BDetailsData(String gstin);
	
	public List<Gstr3BPosDetailResponseDto> convertPosDetails(
			List<Object[]> objs,String entityName, String companyCode);
	
}
