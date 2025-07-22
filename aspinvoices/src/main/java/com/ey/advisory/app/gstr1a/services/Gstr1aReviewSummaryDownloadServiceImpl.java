package com.ey.advisory.app.gstr1a.services;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.services.anx1.Gstr1ProcessedRecordsFetchService;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenDocRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenNilRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.gstr1.einv.Gstr1aProcessedRecordsScreenServiceImpl;
import com.ey.advisory.app.services.reports.Gstr1ReviewSumDynamicExcel;
import com.ey.advisory.app.services.reports.Gstr1ReviewSummaryDownloadService;
import com.ey.advisory.app.services.search.docsummarysearch.GstnSummarySectionService;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenAdvReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenDocReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenHSNReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenSezReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleDocGstnSummarySearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.gstr1A.Gstr1ASimpleDocGstnSummarySearchService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchResult;

@Component("Gstr1aReviewSummaryDownloadServiceImpl")
public class Gstr1aReviewSummaryDownloadServiceImpl
		implements Gstr1ReviewSummaryDownloadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1aProcessedRecordsScreenServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1ProcessedRecordsFetchService")
	private Gstr1ProcessedRecordsFetchService gstr1ProcessedRecordsFetchService;

	@Autowired
	@Qualifier("Gstr1SummaryScreenReqRespHandler")
	private Gstr1SummaryScreenReqRespHandler gstr1ReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenAdvReqRespHandler")
	private Gstr1SummaryScreenAdvReqRespHandler gstr1AdvReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenSezReqRespHandler")
	private Gstr1SummaryScreenSezReqRespHandler gstr1SezReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenDocReqRespHandler")
	private Gstr1SummaryScreenDocReqRespHandler gstr1DocReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenHSNReqRespHandler")
	private Gstr1SummaryScreenHSNReqRespHandler gstr1HsnReqRespHandler;

	@Autowired
	@Qualifier("GstnSummarySectionService")
	private GstnSummarySectionService gstnService;
	@Autowired
	@Qualifier("Gstr1ASimpleDocGstnSummarySearchService")
	private Gstr1ASimpleDocGstnSummarySearchService gstr1aSimpleDocGstnSummarySearchService;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository repo;

	@Override
	public Workbook findGstr1RevSummTablesReports(
			Gstr1ReviwSummReportsReqDto criteria, Object object) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();

		Annexure1SummaryReqDto annexure1SummaryRequest = new Annexure1SummaryReqDto();
		annexure1SummaryRequest.setDataSecAttrs(request.getDataSecAttrs());
		annexure1SummaryRequest.setEntityId(request.getEntityId());
		annexure1SummaryRequest.setTaxPeriod(request.getTaxperiod());
		annexure1SummaryRequest.setReturnType(APIConstants.GSTR1A.toUpperCase());


		LOGGER.debug("GSTN Data Summary Execution BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult = (SearchResult<Gstr1CompleteSummaryDto>) gstr1aSimpleDocGstnSummarySearchService
				.find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug("OUTWARD GSTN Data Summary Execution END ");

		List<? extends Gstr1CompleteSummaryDto> gstnResult = gstnSumryResult
				.getResult();

		List<Gstr1SummaryScreenRespDto> outwardSummaryResponse = gstr1ReqRespHandler
				.handleGstr1ReqAndResp(annexure1SummaryRequest, gstnResult);
		Map<String, String> outwardMap = buildOutwardMap();
		appendTableTypeForSummaryData(outwardSummaryResponse, outwardMap);

		List<Gstr1SummaryScreenRespDto> hsnSummaryResponse = gstr1HsnReqRespHandler
				.handleGstr1HsnReqAndResp(annexure1SummaryRequest, gstnResult);
		appendTableTypeForSummaryData(hsnSummaryResponse, outwardMap);

		List<Gstr1SummaryScreenRespDto> sezSummaryResponse = gstr1SezReqRespHandler
				.handleGstr1SezReqAndResp(annexure1SummaryRequest);
		appendTableTypeForSummaryData(sezSummaryResponse, outwardMap);

		List<Gstr1SummaryScreenRespDto> advSummaryResponse = gstr1AdvReqRespHandler
				.handleGstr1AdvReqAndResp(annexure1SummaryRequest, gstnResult);
		appendTableTypeForSummaryData(advSummaryResponse, outwardMap);

		List<Gstr1SummaryScreenNilRespDto> nilSummaryResponse = gstr1DocReqRespHandler
				.handleGstr1NilReqAndResp(annexure1SummaryRequest, gstnResult);
		appendTableTypeForNilSummaryData(nilSummaryResponse, outwardMap);

		List<Gstr1SummaryScreenDocRespDto> docSummaryResponse = gstr1DocReqRespHandler
				.handleGstr1DocReqAndResp(annexure1SummaryRequest, gstnResult);
		appendTableTypeForDocSummaryData(docSummaryResponse, outwardMap);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-1A_ReviewSummary_Screen_Download.xlsx");

		LOGGER.debug("GSTR1 Review Summary screen download Response"
				+ outwardSummaryResponse + hsnSummaryResponse
				+ sezSummaryResponse + advSummaryResponse + nilSummaryResponse
				+ docSummaryResponse);

		try {
			String entityName = repo.findEntityNameByEntityId(
					request.getEntityId().stream().findFirst().get());
			String fromTaxPeriod = request.getTaxperiod();
			LocalDate startDate = LocalDate.of(
					Integer.parseInt(fromTaxPeriod.substring(2)),
					Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
			String taxPeriod = startDate.getMonth().getDisplayName(
					TextStyle.SHORT, Locale.US) + "-" + startDate.getYear();

			String date = null;
			String time = null;
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy");

			DateTimeFormatter FOMATTER1 = DateTimeFormatter.ofPattern("HH:mm:ss");

			date = FOMATTER.format(istDateTimeFromUTC);
			time = FOMATTER1.format(istDateTimeFromUTC);

			Gstr1ReviewSumDynamicExcel.generateExcelForGstr1a(workbook,
					annexure1SummaryRequest, outwardSummaryResponse,
					hsnSummaryResponse, sezSummaryResponse, advSummaryResponse,
					nilSummaryResponse, docSummaryResponse, entityName, date,
					time, taxPeriod);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return workbook;
	}

	private void appendTableTypeForDocSummaryData(
			List<Gstr1SummaryScreenDocRespDto> docSummaryResponse,
			Map<String, String> map) {
		docSummaryResponse.stream().forEach(dto -> {
			dto.setTaxDocType(map.get(dto.getTaxDocType()));
		});

	}

	private void appendTableTypeForNilSummaryData(
			List<Gstr1SummaryScreenNilRespDto> nilSummaryResponse,
			Map<String, String> map) {
		nilSummaryResponse.stream().forEach(dto -> {
			dto.setTaxDocType(map.get(dto.getTaxDocType()));
		});

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

	private void appendTableTypeForSummaryData(
			List<Gstr1SummaryScreenRespDto> outwardSummaryResponse,
			Map<String, String> map) {
		outwardSummaryResponse.stream().forEach(dto -> {
			dto.setTaxDocType(map.get(dto.getTaxDocType()));
		});

	}

	private Map<String, String> buildOutwardMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("B2B", "B2B (4, 6B, 6C)");
		map.put("B2BA", "B2B (Amendment) (9A)");
		map.put("B2CL", "B2CL (5)");
		map.put("B2CLA", "B2CL (Amendment) (9A)");
		map.put("EXPORTS", "Exports (6A)");
		map.put("EXPORTS-A", "Exports (Amendment) (9A)");
		map.put("CDNR", "CDNR (9B)");
		map.put("CDNRA", "CDNR (Amendment) (9C)");
		map.put("CDNUR", "CDNUR (9B)");
		map.put("CDNURA", "CDNUR (Amendment) (9C)");
		map.put("B2CS", "B2CS (7)");
		map.put("B2CSA", "B2CS (Amendment) (10)");
		map.put("NILEXTNON", "NIL, Exempt, Non GST");
		map.put("ADV REC", "Advance Received (11A- Part I)");
		map.put("ADV ADJ", "Advance Adjusted (11B- Part I)");
		map.put("ADV REC-A", "Advance Received (Amendment) (11A- Part II)");
		map.put("ADV ADJ-A", "Advance Adjusted (Amendment) (11B- Part II)");
		map.put("HSN", "HSN Summary (12)");
		map.put("DOC ISSUED", "Document Issued (13)");
		map.put("Total", "Total");
		map.put("SEZWP", "SEZ With Tax");
		map.put("SEZWOP", "SEZ Without Tax");
		return map;
	}
}
