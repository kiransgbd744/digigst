/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.views.client.TdsTdsaTotaldto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import joptsimple.internal.Strings;


/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("Gstr2xErrorReportServiceImpl")
public class Gstr2xErrorReportServiceImpl implements Gstr2xReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2xErrorReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2xErrorReportDaoImpl")
	private Gstr2xReportDao gstr2xErrorReportDao;

	@Override
	public Workbook downloadReports(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1VerticalDownloadReportsReqDto request = (Gstr1VerticalDownloadReportsReqDto) criteria;

		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		//List<Object> responseFromView = new ArrayList<>();
		//responseFromView = gstr2xErrorReportDao.getGstr2xReports(request);

		/*workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"TDS _TDSA _TCS_ TCSA_Data_GSTR-2X.xlsx");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("TDS data response {}"  ,responseFromView);
		}
		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("tds.error.report.headers").split(",");
			if(workbook!=null && workbook.getWorksheets()!=null){
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}
		}*/
		List<Object[]> list = null;

		Long fileId = request.getFileId();
		String type = request.getType();

		
		StoredProcedureQuery storedProcSummaryReport = entityManager
				.createStoredProcedureQuery(
						"USP_TCS_TDS_FILE_STATUS_REPORT");

		storedProcSummaryReport.registerStoredProcedureParameter(
				"P_FILE_ID", Long.class, ParameterMode.IN);

		storedProcSummaryReport.setParameter("P_FILE_ID", fileId);
		
		storedProcSummaryReport.registerStoredProcedureParameter(
				"P_REPORT_TYPE", String.class, ParameterMode.IN);

		storedProcSummaryReport.setParameter("P_REPORT_TYPE", type);
		
		list = storedProcSummaryReport.getResultList();

		List<TdsTdsaTotaldto> dataList = list
				.stream().map(o -> convertVerticalTotal(o))
				.collect(Collectors
						.toCollection(ArrayList::new));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("TDS data response {}"  ,dataList);
		}
		if (dataList != null && dataList.size() > 0) {
			
			workbook = new Workbook();

			String[] invoiceHeaders = commonUtility
					.getProp("tds.error.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"TDS _TDSA _TCS_ TCSA_Data_GSTR-2X.xlsx");
			 Worksheet worksheet = workbook.getWorksheets().get(0);
			 Cells errorDumpCells = worksheet.getCells();
					//.getCells();
			errorDumpCells.importCustomObjects(dataList, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					dataList.size(), true, "yyyy-mm-dd", false);
			//to delete last row of the report.
			int lastRowIndex = worksheet.getCells().getMaxDataRow();
			worksheet.getCells().deleteRow(lastRowIndex + 1);
			}
		
		return workbook;
	}

	private Workbook createWorkbookWithExcelTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL templateDir = classLoader.getResource(folderName + "/");
			String templatePath = templateDir.getPath() + fileName;
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
	private TdsTdsaTotaldto convertVerticalTotal(Object[] arr) {
		TdsTdsaTotaldto obj = new TdsTdsaTotaldto();
		
		String errDesc = null;
		
		String errCode = (arr[0] != null) ? arr[0].toString() : null;
		obj.setAspErrorCode(errCode);

		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "GSTR2X_VERTICAL");
		}
		obj.setAspErrorDesc(errDesc);
		obj.setActionSavedatDigiGST(arr[2] != null ? arr[2].toString() : null);
		obj.setDigiGstRemarks(arr[3] != null ? arr[3].toString() : null);
		obj.setDigiGstComment(arr[4] != null ? arr[4].toString() : null);
		obj.setGstin(
				arr[5] != null ? arr[5].toString() : null);
		obj.setType(
				arr[6] != null ? arr[6].toString() : null);
		obj.setTaxPeriod(
				arr[7] != null ? arr[7].toString() : null);
		obj.setMonth(arr[8] != null ? arr[8].toString() : null);
		obj.setGstinOfDeductor(
				arr[9] != null ? arr[9].toString() : null);
		obj.setDeductorName(
				arr[10] != null ? arr[10].toString() : null);
		obj.setDocNo(arr[11] != null ? arr[11].toString() : null);
		obj.setDocDate(
				arr[12] != null ? arr[12].toString() : null);
		obj.setOrgMonth(arr[13] != null ? arr[13].toString() : null);
		obj.setOrgDocNo(arr[14] != null ? arr[14].toString() : null);
		obj.setOrgDocDate(arr[15] != null ? arr[15].toString() : null);
		obj.setSuppliesCollected(arr[16] != null ? arr[16].toString() : null);
		obj.setSuppliesReturned(
				arr[17] != null ? arr[17].toString() : null);
		obj.setNetSupplies(
				arr[18] != null ? arr[18].toString() : null);
		obj.setIGST(
				arr[19] != null ? arr[19].toString() : null);
		obj.setCGST(
				arr[20] != null ? arr[20].toString() : null);
		obj.setSGST(
				arr[21] != null ? arr[21].toString() : null);
		obj.setInvoiceValue(
				arr[22] != null ? arr[22].toString() : null);
		obj.setOrgTaxableValue(
				arr[23] != null ? arr[23].toString() : null);
		obj.setOrgInvoiceValue(
				arr[24] != null ? arr[24].toString() : null);
		obj.setPos(
				arr[25] != null ? arr[25].toString() : null);
		obj.setChkSum(
				arr[26] != null ? arr[26].toString() : null);
		obj.setActionSavedatGSTN(
				arr[27] != null ? arr[27].toString() : null);
		obj.setGstnRemarks(
				arr[28] != null ? arr[28].toString() : null);
		obj.setGstnComment(
				arr[29] != null ? arr[29].toString() : null);

		return obj;
	}

}
