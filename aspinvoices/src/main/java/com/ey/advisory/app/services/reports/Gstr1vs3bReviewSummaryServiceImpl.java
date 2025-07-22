package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.services.anx1.Gstr1VsGstr3bDownloadReviewSummaryFetchService;
import com.ey.advisory.app.docs.dto.Gstr1Vs3bReviewSummaryScreenRespDto;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bReviewSummaryRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

/**
 * @author Sasidhar Reddy
 *
 * 
 */

@Service("Gstr1vs3bReviewSummaryServiceImpl")
public class Gstr1vs3bReviewSummaryServiceImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ItcReversalInwardServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ComputationofRevAmtDaoImpl")
	private ItcReversalInwardDao itcComptDao;

	@Autowired
	@Qualifier("ComptTurnoverReportDaoImpl")
	private ItcReversalInwardDao itcReversalComptDao;

	@Autowired
	@Qualifier("ItcInwardReportDaoImpl")
	private ItcReversalInwardDao itcReversalInwardDao;

	@Autowired
	@Qualifier("ItcOutwardReportDaoImpl")
	private ItcReversalInwardDao itcReversalOutwardDao;

	@Autowired
	@Qualifier("ItcB2csReportDaoImpl")
	private ItcReversalInwardDao itcReversalb2CSDao;

	@Autowired
	@Qualifier("ItcNilNonDaoImpl")
	private ItcNilNonDaoImpl itcReversalnilDao;

	@Autowired
	@Qualifier("Gstr1VsGstr3bDownloadReviewSummaryFetchService")
	Gstr1VsGstr3bDownloadReviewSummaryFetchService gstr1VsGstr3bDownloadReviewSummaryFetchService;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepository;
	
	public Workbook findReviewSummaryData(
			Gstr1VsGstr3bProcessSummaryReqDto criteria) {
		try{
		String entityName = entityInfoRepository.findEntityNameByEntityId(
				criteria.getEntityId().stream().findFirst().get());
		Workbook workbook = new Workbook();

		List<Gstr1VsGstr3bReviewSummaryRespDto> response = gstr1VsGstr3bDownloadReviewSummaryFetchService
				.loadDataByCriteria(criteria);
		List<Gstr1Vs3bReviewSummaryScreenRespDto> responseFromViewProcess = convertProcessRecordsToScreenDtos(
				response);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR1vsGSTR3B_GSTIN_FromTaxPeriod_ToTaxPeriod_TimeofGeneration_v1.xlsx");

		LOGGER.debug("gstr1vs3b Review Summary data response" + response);
		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			try {
				Gstr1vs3bReviewSummaryServiceUtil.downloadGstrVs3bReport(
						workbook, responseFromViewProcess, criteria,
						entityName);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		
		return workbook;
		}catch(Exception e)
		{
			LOGGER.error(e.getMessage());
			throw new AppException(e);
		}
		

	}

	private List<Gstr1Vs3bReviewSummaryScreenRespDto> convertProcessRecordsToScreenDtos(
			List<Gstr1VsGstr3bReviewSummaryRespDto> response) {
		List<Gstr1Vs3bReviewSummaryScreenRespDto> dtos = new ArrayList<Gstr1Vs3bReviewSummaryScreenRespDto>();
		response.stream().forEach(dto -> {
			Gstr1Vs3bReviewSummaryScreenRespDto screenDto = new Gstr1Vs3bReviewSummaryScreenRespDto();
			screenDto.setGstin(dto.getGstin());
			screenDto.setSupplies(String.valueOf(dto.getSupplies()));
			screenDto.setFormula(String.valueOf(dto.getFormula()));
			
			screenDto.setTaxableValue(
					String.valueOf((dto.getTaxableValue().compareTo(BigDecimal.ZERO)!=0)?dto.getTaxableValue():BigDecimal.ZERO));
			screenDto.setIgst(
					String.valueOf((dto.getIgst().compareTo(BigDecimal.ZERO)!=0)?dto.getIgst():BigDecimal.ZERO));
			screenDto.setCgst(
					String.valueOf((dto.getCgst().compareTo(BigDecimal.ZERO)!=0)?dto.getCgst():BigDecimal.ZERO));
			screenDto.setSgst(
					String.valueOf((dto.getSgst().compareTo(BigDecimal.ZERO)!=0)?dto.getSgst():BigDecimal.ZERO));
			screenDto.setCess(
					String.valueOf((dto.getCess().compareTo(BigDecimal.ZERO)!=0)?dto.getCess():BigDecimal.ZERO));
			if (dto.getTaxPeriod() != null) {
				screenDto.setTaxPeriod(String
						.valueOf(GenUtil.convertDerivedTaxPeriodToTaxPeriod(
								Integer.parseInt(dto.getTaxPeriod()))));
			} 
			
			dtos.add(screenDto);
		});
		return dtos;
	}

	private Workbook createWorkbookWithExcelTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}
}
