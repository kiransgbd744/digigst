package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.customisedreport.CustomisedFieldSelEntity;
import com.ey.advisory.app.data.repositories.client.customisedreport.CustomisedFieldSelRepo;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.report.convertor.AspSyncCustomisedReportConvertor;
import com.ey.advisory.app.services.common.Gstr1CommonUtility;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

@Service("Gstr1CDNURASectionReportServiceImpl")
public class Gstr1CDNURASectionReportServiceImpl
		implements Gstr1ReviewSummReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1CDNURASectionReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1CDNURASectionReportDaoImpl")
	private Gstr1ReviewSummaryCustomizedReportsDao gstr1ReviewSummaryReportsDao;

	@Autowired
	CustomisedFieldSelRepo custFieldSeleRepo;

	@Autowired
	@Qualifier("AspSyncCustomisedReportConvertor")
	AspSyncCustomisedReportConvertor aspUploadReptConverter;
	
	@Autowired
	Gstr1CommonUtility gstr1CommonUtility;

	@Override
	public Workbook findGstr1ReviewSummRecords(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = null;
		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		Long entityId = request.getEntityId().get(0);
		Pair<List<Object[]>, List<Object>> gstr1rsReports = gstr1ReviewSummaryReportsDao
				.getGstr1RSReports(request);

		List<Object> responseFromView = gstr1rsReports.getValue1();
		List<Object[]> responseFromViewArr = gstr1rsReports.getValue0();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 CDNURA processed data item level response"
					+ responseFromView.size());
		}

		List<CustomisedFieldSelEntity> isActiveFieldSel = custFieldSeleRepo
				.findByEntityIdAndReportTypeAndIsActiveTrue(entityId,
						APIConstants.GSTR1_CUST_INV_REPORT);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 CDNURA isActiveFieldSel response" + isActiveFieldSel.size());
		}
		
		

		if (responseFromView != null && responseFromView.size() > 0) {

			if (isActiveFieldSel == null || isActiveFieldSel.isEmpty()) {

				String msg = String
						.format("CDNUR-A Gstr1_Asp_Processed_TransactionalReport :"
								+ " No Active column List found for Entity Id %s  "
								+ "and ReportType 'GSTR1_TRANS_LEVEL' Generating "
								+ "Default Report ", entityId);
				LOGGER.debug(msg);
				String[] invoiceHeaders = commonUtility
						.getProp("anx1.api.new.report.headers.new").split(",");
				workbook = gstr1CommonUtility.createWorkbookWithExcelTemplate("ReportTemplates",
						"Gstr1_Asp_Processed_TransactionalReport.xlsx");
				Cells errorDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				errorDumpCells.importCustomObjects(responseFromView,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromView.size(), true, "yyyy-mm-dd", false);
			} else {
				
				workbook = new Workbook();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 CDNURA custom file name -"
							+ workbook.getFileName());
				}

				List<String> selectedList = new ArrayList<String>(Arrays.asList(
						isActiveFieldSel.get(0).getHeaderMapping().split(",")));

				Map<String, Object> ap = new HashMap<>();
				ap.put("selectedList", selectedList);

				responseFromView =  responseFromView.stream()
						.map(o -> (DataStatusEinvoiceDto) aspUploadReptConverter
								.convert(ap, (DataStatusEinvoiceDto)o))
						.collect(Collectors.toCollection(ArrayList::new));
				String[] invoiceHeaders = selectedList
						.toArray(new String[selectedList.size()]);
				isHeaderRequired = true;
				startRow = 1;
				CommonUtility.setAsposeLicense();
				workbook = new Workbook();
//				CommonUtility.setAsposeLicense();
				Cells errorDumpCells = workbook.getWorksheets().get(0)
						.getCells();
//				errorDumpCells.importCustomObjects(responseFromView,
//						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
//						responseFromView.size(), true, "yyyy-mm-dd", false);
				



			errorDumpCells.importArray(invoiceHeaders, 0, 0, false);
			
			List<String> selectedColumnList  = Arrays
					.asList(isActiveFieldSel.get(0).getJavaMapp().split(","));
			String[] invoiceColumns = selectedColumnList.toArray(new String[selectedList.size()]);
				
			errorDumpCells.importCustomObjects(responseFromView, invoiceColumns, false, startRow,
						startcolumn, responseFromView.size(), true, "yyyy-mm-dd", false);

			}
		}
		return workbook;
	}

	
}
