package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.services.anx1.Gstr1ProcessedRecordsFetchService;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ProcessedSummResponseDto;
import com.ey.advisory.app.docs.services.gstr6.Gstr6ProcessedDataService;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.search.docsummarysearch.GstnSummarySectionService;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenAdvReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenDocReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenHSNReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenSezReqRespHandler;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.ibm.icu.math.BigDecimal;

/**
 * @author Sasidhar
 *
 * 
 */

@Service("Gstr6ProcessedRecordsScreenServiceImpl")
public class Gstr6ProcessedRecordsScreenServiceImpl {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Gstr6ProcessedRecordsScreenServiceImpl.class);

    @Autowired
    CommonUtility commonUtility;

    @Autowired
    @Qualifier("Gstr6ProcessedDataServiceImpl")
    private Gstr6ProcessedDataService gstr6ProcessedDataService;

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
    @Qualifier("entityInfoDetailsRepository")
    EntityInfoDetailsRepository repo;

    public Workbook findGstr6ProcessedScreenDownload(
            Gstr6SummaryRequestDto criteria) {
        Workbook workbook = new Workbook();
        int startRow = 4;
        int startcolumn = 0;
        boolean isHeaderRequired = false;

        List<Gstr6ProcessedSummResponseDto> respDtos = gstr6ProcessedDataService
                .getGstr6ProcessedRec(criteria);
        List<Gstr6ProcessedSummaryScreenDto> responseFromViewProcess = convertProcessSummaryRecordsToScreenDtos(
                respDtos);

        workbook = createWorkbookWithExcelTemplate("ReportTemplates",
                "GSTR-6_Processed_Summary_Screen Download.xlsx");

        LOGGER.debug(
                "Gstr6 Processed rec data response" + responseFromViewProcess);

        if (responseFromViewProcess != null
                && responseFromViewProcess.size() > 0) {
            String[] invoiceHeaders = commonUtility
                    .getProp("gstr6.processed.rec.screen.report.headers")
                    .split(",");

            Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();

            List<Long> entityId = criteria.getEntityId();
            EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
            for (Long entity : entityId) {
                findEntityByEntityId = repo.findEntityByEntityId(entity);
            }
            errorDumpCells.get("A1").setValue(" GSTR-6 Processed Summary ");
            if (findEntityByEntityId != null) {
                errorDumpCells.get("A2").setValue(
                        "Entity Name- " + findEntityByEntityId.getEntityName());
            }

            LocalDateTime now = EYDateUtil
                    .toUTCDateTimeFromLocal(LocalDateTime.now());

            LocalDateTime istDateTimeFromUTC = EYDateUtil
                    .toISTDateTimeFromUTC(now);
            DateTimeFormatter FOMATTER = DateTimeFormatter
                    .ofPattern("dd-MM-yyyy");

            DateTimeFormatter FOMATTER1 = DateTimeFormatter
                    .ofPattern("HH:mm:ss");

            String date = FOMATTER.format(istDateTimeFromUTC);
            String time = FOMATTER1.format(istDateTimeFromUTC);

            errorDumpCells.get("C2").setValue("Date-" + date);
            errorDumpCells.get("D2").setValue("Time-" + time);

            String fromTaxPeriod = criteria.getTaxPeriod();
            LocalDate startDate = LocalDate.of(
                    Integer.parseInt(fromTaxPeriod.substring(2)),
                    Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
            String taxPeriod = startDate.getMonth().getDisplayName(
                    TextStyle.SHORT, Locale.US) + " " + startDate.getYear();

            errorDumpCells.get("E2").setValue("TaxPeriod-" + taxPeriod);
            String gstins = criteria.getDataSecAttrs().get("GSTIN").get(0);
            errorDumpCells.get("B2").setValue("GSTIN-" + gstins);
            errorDumpCells.importCustomObjects(responseFromViewProcess,
                    invoiceHeaders, isHeaderRequired, startRow, startcolumn,
                    responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
        }

        return workbook;

    }

    private List<Gstr6ProcessedSummaryScreenDto> convertProcessSummaryRecordsToScreenDtos(
            List<Gstr6ProcessedSummResponseDto> result) {
        List<Gstr6ProcessedSummaryScreenDto> dtos = new ArrayList<Gstr6ProcessedSummaryScreenDto>();
        result.stream().forEach(dto -> {
            Gstr6ProcessedSummaryScreenDto screenDto = new Gstr6ProcessedSummaryScreenDto();
            screenDto.setGSTIN(dto.getGstin());
            screenDto.setCount(dto.getCount());
            screenDto.setInvoiceValue(dto.getInvoiceValue()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getInvoiceValue().toString()):BigDecimal.ZERO+".00");
            screenDto.setTaxableValue(dto.getTaxableValue()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getTaxableValue().toString()):BigDecimal.ZERO+".00");
            screenDto.setTotalTax(dto.getTotalTax()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getTotalTax().toString()):BigDecimal.ZERO+".00");
            screenDto.setTpIgst(dto.getTpIgst()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getTpIgst().toString()):BigDecimal.ZERO+".00");
            screenDto.setTpCgst(dto.getTpCgst()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getTpCgst().toString()):BigDecimal.ZERO+".00");
            screenDto.setTpSgst(dto.getTpSgst()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getTpSgst().toString()):BigDecimal.ZERO+".00");
            screenDto.setTpCess(dto.getTpCess()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getTpCess().toString()):BigDecimal.ZERO+".00");
            screenDto.setTotCreElig(dto.getTotCreElig()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getTotCreElig().toString()):BigDecimal.ZERO+".00");
            screenDto.setCeIgst(dto.getCeIgst()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getCeIgst().toString()):BigDecimal.ZERO+".00");
            screenDto.setCeCgst(dto.getCeCgst()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getCeCgst().toString()):BigDecimal.ZERO+".00");
            screenDto.setCeSgst(dto.getCeSgst()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getCeSgst().toString()):BigDecimal.ZERO+".00");
            screenDto.setCeCess(dto.getCeCess()!=null?DownloadReportsConstant.CSVCHARACTER
					.concat(dto.getCeCess().toString()):BigDecimal.ZERO+".00");
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
