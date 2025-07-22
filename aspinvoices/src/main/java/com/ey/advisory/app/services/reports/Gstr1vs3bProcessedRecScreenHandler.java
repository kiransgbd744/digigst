package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

/**
 * @author Sasidhar
 *
 * 
 */
@Component("Gstr1vs3bProcessedRecScreenHandler")
public class Gstr1vs3bProcessedRecScreenHandler {

    @Autowired
    @Qualifier("Gstr1vs3bProcessedRecordsScreenServiceImpl")
    private Gstr1vs3bProcessedRecordsScreenServiceImpl gstr1vs3bProcessedRecordsScreenServiceImpl;

    @Autowired
    @Qualifier("Gstr1vs3bReviewSummaryScreenDownloadServiceImpl")
    private Gstr1vs3bReviewSummaryScreenDownloadServiceImpl gstr1vs3bReviewSummaryDownloadServiceImpl;

    public Workbook getGstr1vs3bSumTablesReports(
            Gstr1VsGstr3bProcessSummaryReqDto criteria) throws Exception {

        return gstr1vs3bProcessedRecordsScreenServiceImpl
                .findGstr1vs3bProcessedScreenDownload(criteria);

    }

    public Workbook getGstr1vs3bRevSumTablesReports(
            Gstr1VsGstr3bProcessSummaryReqDto criteria) throws Exception {

        return gstr1vs3bReviewSummaryDownloadServiceImpl
                .findGstr1vs3bRevSummTablesReports(criteria);

    }

}
