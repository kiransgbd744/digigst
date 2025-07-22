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
import com.ey.advisory.app.data.services.anx1.Gstr1VsGstr3bProcessSummaryFetchService;
import com.ey.advisory.app.data.services.anx1.Gstr2aVsGstr3bPrSummaryFetchService;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bProcessSummaryFinalRespDto;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bProcessSummaryScreenRespDto;
import com.ey.advisory.app.docs.services.gstr6.Gstr6ProcessedDataService;
import com.ey.advisory.app.services.search.docsummarysearch.GstnSummarySectionService;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenAdvReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenDocReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenHSNReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenSezReqRespHandler;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

/**
 * @author Sasidhar
 *
 * 
 */

@Service("Gstr1vs3bProcessedRecordsScreenServiceImpl")
public class Gstr1vs3bProcessedRecordsScreenServiceImpl {

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

    @Autowired
    @Qualifier("Gstr2aVsGstr3bPrSummaryFetchService")
    Gstr2aVsGstr3bPrSummaryFetchService gstr2aVsGstr3bPrSummaryFetchService;

    @Autowired
    @Qualifier("Gstr1VsGstr3bProcessSummaryFetchService")
    Gstr1VsGstr3bProcessSummaryFetchService gstr1VsGstr3bPrSummaryFetchService;

    public Workbook findGstr1vs3bProcessedScreenDownload(
            Gstr1VsGstr3bProcessSummaryReqDto criteria) throws Exception {
        Workbook workbook = new Workbook();
        int startRow = 5;
        int startcolumn = 0;
        boolean isHeaderRequired = false;

        List<Gstr1VsGstr3bProcessSummaryFinalRespDto> response = gstr1VsGstr3bPrSummaryFetchService
                .response(criteria);
        List<Gstr1VsGstr3bProcessSummaryScreenRespDto> responseFromViewProcess = convertProcessSummaryRecordsToScreenDtos(
                response);

        workbook = createWorkbookWithExcelTemplate("ReportTemplates",
                "Gstr1vs3b_Processed_Records_Screen_Download.xlsx");

        LOGGER.debug("Gstr1vs3b Processed rec data response"
                + responseFromViewProcess);

        if (responseFromViewProcess != null
                && responseFromViewProcess.size() > 0) {
            String[] invoiceHeaders = commonUtility
                    .getProp("gstr1vs3b.processed.screen.report.headers")
                    .split(",");

            Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();

            List<Long> entityId = criteria.getEntityId();
            EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
            for (Long entity : entityId) {
                findEntityByEntityId = repo.findEntityByEntityId(entity);
            }
            errorDumpCells.get("A1")
                    .setValue("Gstr1vs3b_Processed_Records_Screen_Download");
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

            String fromTaxPeriod = criteria.getTaxPeriodFrom();
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

    private List<Gstr1VsGstr3bProcessSummaryScreenRespDto> convertProcessSummaryRecordsToScreenDtos(
            List<Gstr1VsGstr3bProcessSummaryFinalRespDto> response) {
        List<Gstr1VsGstr3bProcessSummaryScreenRespDto> dtos = new ArrayList<Gstr1VsGstr3bProcessSummaryScreenRespDto>();
        response.stream().forEach(dto -> {
            Gstr1VsGstr3bProcessSummaryScreenRespDto screenDto = new Gstr1VsGstr3bProcessSummaryScreenRespDto();
            screenDto.setGstin(dto.getGstin());
            screenDto.setGstr1TaxableValue(dto.getGstr1TaxableValue());
            screenDto.setGstr1TotalTax(dto.getGstr1TotalTax());
            screenDto.setGstr3bTaxableValue(dto.getGstr3bTaxableValue());
            screenDto.setGstr3bTotalTax(dto.getGstr3bTotalTax());
            screenDto.setDiffTaxableValue(dto.getDiffTaxableValue());
            screenDto.setDiffTotalTax(dto.getDiffTotalTax());

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
