package com.ey.advisory.app.services.reports;

import java.net.URL;
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
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.customisedreport.CustomisedFieldSelEntity;
import com.ey.advisory.app.data.repositories.client.customisedreport.CustomisedFieldSelRepo;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.report.convertor.AspSyncCustomisedReportConvertor;
import com.ey.advisory.app.services.common.Gstr1CommonUtility;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

@Service("Gstr1B2CSSectionReportServiceImpl")
public class Gstr1B2CSSectionReportServiceImpl
		implements Gstr1ReviewSummReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1B2CSSectionReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1SecB2CSSavableTotalSummaryDaoImpl")
	private Gstr1ASPB2CSSavableTotalDao gstr1ASPB2CSSavableTotalDao;

	@Autowired
	@Qualifier("Gstr1SecSectionReportDaoImpl")
	private Gstr1ReviewSummaryCustomizedReportsDao gstr1ASPB2CSSavableTransactionalDao;

	@Autowired
	@Qualifier("Gstr1SecB2CSSavableSummaryLevelDaoImpl")
	private Gstr1ASPB2CSSavableSummaryLevelDao gstr1ASPB2CSSavableSummaryLevelDao;

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
		boolean isWorkbookCreated = false;

		try {
			List<Object> responseFromTot = new ArrayList<>();
			responseFromTot = gstr1ASPB2CSSavableTotalDao
					.getGstr1B2CSSavableReports(request);

			Long entityId = request.getEntityId().get(0);

			Pair<List<Object[]>, List<Object>> gstr1rsReports = gstr1ASPB2CSSavableTransactionalDao
					.getGstr1RSReports(request);

			List<Object> responseFromViewTrans = gstr1rsReports.getValue1();
			List<DataStatusEinvoiceDto> responseFromViewObj = new ArrayList<>();

			List<Object> responseFromViewSum = new ArrayList<>();
			responseFromViewSum = gstr1ASPB2CSSavableSummaryLevelDao
					.getGstr1B2CSSavableReports(request);

			workbook = gstr1CommonUtility.createWorkbookWithExcelTemplate("ReportTemplates",
					"GSTR-1_ASP_B2CS_Savable.xlsx");

			if (responseFromTot != null && responseFromTot.size() > 0) {
				isWorkbookCreated=true;
				String[] invoiceHeaders = commonUtility
						.getProp(
								"gstr1.Savable.b2cS.totalsummary.report.headers")
						.split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				errorDumpCells.importCustomObjects(responseFromTot,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromTot.size(), true, "yyyy-mm-dd", false);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 processed data Tx level response"
						+ responseFromViewTrans.size());
			}

			List<CustomisedFieldSelEntity> isActiveFieldSel = custFieldSeleRepo
					.findByEntityIdAndReportTypeAndIsActiveTrue(entityId,
							APIConstants.GSTR1_CUST_INV_REPORT);

			if (responseFromViewTrans != null
					&& responseFromViewTrans.size() > 0) {
				isWorkbookCreated=true;

				if (isActiveFieldSel == null || isActiveFieldSel.isEmpty()) {

					String msg = String
							.format("B2CS Gstr1_Asp_Processed_TransactionalReport :"
									+ " No Active column List found for Entity Id %s  "
									+ "and ReportType 'GSTR1_TRANS_LEVEL' Generating "
									+ "Default Report ", entityId);
					LOGGER.debug(msg);
					String[] invoiceCols = commonUtility
							.getProp("anx1.api.new.report.headers.new").split(",");

					String[] invoiceHeadersNum = commonUtility
							.getProp("gsrt1.b2cs.headersNums").split(",");

					String[] invoiceHeaderNames = commonUtility
							.getProp("gsrt1.b2cs.headersNames").split(",");

					Cells errorDumpCells = workbook.getWorksheets().get(1)
							.getCells();

					errorDumpCells.importArray(invoiceHeadersNum, 0, 0, false);
					errorDumpCells.importArray(invoiceHeaderNames, 1, 0, false);
					startRow = 2;
					errorDumpCells.importCustomObjects(responseFromViewTrans,
							invoiceCols, isHeaderRequired, startRow,
							startcolumn, responseFromViewTrans.size(), true,
							"yyyy-mm-dd", false);
				} else {

					List<String> selectedList = Arrays.asList(isActiveFieldSel
							.get(0).getHeaderMapping().split(","));

					Map<String, Object> ap = new HashMap<>();
					ap.put("selectedList", selectedList);

					String[] invoiceHeaders = selectedList
							.toArray(new String[selectedList.size()]);

					responseFromViewObj = responseFromViewTrans.stream()
							.map(o -> (DataStatusEinvoiceDto) aspUploadReptConverter
									.convert(ap, (DataStatusEinvoiceDto) o))
							.collect(Collectors.toCollection(ArrayList::new));
					startRow = 1;
					Cells errorDumpCells = workbook.getWorksheets().get(1)
							.getCells();

					workbook.getWorksheets().get(1).getCells().deleteRow(1);

					errorDumpCells.importArray(invoiceHeaders, 0, 0, false);

					List<String> selectedColumnList = Arrays.asList(
							isActiveFieldSel.get(0).getJavaMapp().split(","));
					String[] invoiceColumns = selectedColumnList
							.toArray(new String[selectedList.size()]);

					errorDumpCells.importCustomObjects(responseFromViewObj,
							invoiceColumns, false, startRow, startcolumn,
							responseFromViewObj.size(), true, "yyyy-mm-dd",
							false);

				}
			}
			if (responseFromViewSum != null && responseFromViewSum.size() > 0) {
				isWorkbookCreated=true;
				String[] invoiceHeaders = commonUtility
						.getProp(
								"gstr1.Savable.b2cS.summarylevel.report.headers")
						.split(",");

				isHeaderRequired = false;
				startRow = 2;
				Cells errorDumpCells = workbook.getWorksheets().get(2)
						.getCells();
				errorDumpCells.importCustomObjects(responseFromViewSum,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromViewSum.size(), true, "yyyy-mm-dd", false);
			}

			if(!isWorkbookCreated){
				return null;
			}
			return workbook;

		} catch (Exception ex) {

			LOGGER.error(
					"Exception Occuered in B2CS Gstr1_Asp_Processed_TransactionalReport Report {}",
					ex);
			throw new AppException(ex);
		}
	}

}
