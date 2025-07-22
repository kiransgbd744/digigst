/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto.ReturnPeriodDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Sujith.Nanga
 *
 */

@Service("ComplainceReportServiceImpl")
public class ComplainceReportServiceImpl implements ComplainceReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ComplainceReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("CompienceHistoryServiceImpl")
	private CompienceHistoryServiceImpl complienceSummery;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	public Workbook findComplaince(Gstr2aProcessedDataRecordsReqDto criteria,
			PageRequest pageReq) {

		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<ReturnComplianceDto> responseFromView = dataresultset(criteria);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Complaince Report response"
					+ responseFromView);
		}
		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("comp.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"ComplianceReport.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}
		return workbook;
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

	public List<ReturnComplianceDto> dataresultset(
			Gstr2aProcessedDataRecordsReqDto criteria) {
		List<ReturnComplianceDto> responseFromView = new ArrayList<>();

		List<ReturnStusFilingDto> statusdto = complienceSummery
				.findcomplienceSummeryRecords(criteria);

		statusdto.stream().forEach(dto -> {
			String gstin = dto.getGstin();
			String stateCode = gstin.substring(0, 2);
			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			String registrationType = gstinInfoRepository
					.findByGstinAndIsDeleteFalse(gstin).getRegistrationType();

			List<ReturnPeriodDto> returnperiods = dto.getReturnperiods();

			returnperiods.stream().forEach(dto1 -> {

				ReturnComplianceDto obj = new ReturnComplianceDto();

				obj.setgStin(gstin);
				String datetime = dto1.getTime();
				if (datetime != null) {

					String[] datetime1 = datetime.split(" ");
					String date = datetime1[0];

					obj.setFillingSubDate(date);
				}
				obj.setRegistrationType(registrationType);
				obj.setReturnType(criteria.getReturnType());
				obj.setStateName(stateName);
				if (criteria.getReturnType().equalsIgnoreCase("ITC04")) {
					obj.setTaxPeriod(itc04TaxPeriod(dto1.getMonth()));
				} else{
					obj.setTaxPeriod(dto1.getMonth());
				}
				obj.setArnNo(dto1.getArnNo());
				obj.setStatus(dto1.getStatus());

				responseFromView.add(obj);

			});

		});
		return responseFromView;
	}
	
	public String itc04TaxPeriod(String month){
		String taxPeriod = "";
		if(month.startsWith("13")){
			taxPeriod = month.replace("13", "Q1");
		} else if(month.startsWith("14")){
			taxPeriod = month.replace("14", "Q2");
		} else if(month.startsWith("15")){
			taxPeriod = month.replace("15", "Q3");
		} else if(month.startsWith("16")){
			taxPeriod = month.replace("16", "Q4");
		} else if(month.startsWith("17")){
			taxPeriod = month.replace("17", "H1");
		} else if(month.startsWith("18")){
			taxPeriod = month.replace("18", "H2");
		} else{
			taxPeriod = month;
		}
			
		return taxPeriod;	
	}
}