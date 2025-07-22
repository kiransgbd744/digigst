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
@Component("Gstr2avs3bProcessedRecScreenHandler")
public class Gstr2avs3bProcessedRecScreenHandler {

    @Autowired
    @Qualifier("Gstr2avs3bProcessedRecordsScreenServiceImpl")
    private Gstr2avs3bProcessedRecordsScreenServiceImpl gstr2avs3bProcessedRecordsScreenServiceImpl;

    @Autowired
    @Qualifier("Gstr2avs3bReviewSummaryDownloadServiceImpl")
    private Gstr2avs3bReviewSummaryDownloadServiceImpl gstr2avs3bReviewSummaryDownloadServiceImpl;

    public Workbook getGstr2avs3bSumTablesReports(
            Gstr1VsGstr3bProcessSummaryReqDto criteria) throws Exception {

        return gstr2avs3bProcessedRecordsScreenServiceImpl
                .findGstr2avs3bProcessedScreenDownload(criteria);

    }

    public Workbook getGstr2avs3bRevSumTablesReports(
            Gstr1VsGstr3bProcessSummaryReqDto criteria) throws Exception {

        return gstr2avs3bReviewSummaryDownloadServiceImpl
                .findGstr2avs3bRevSummTablesReports(criteria);

    }

}
