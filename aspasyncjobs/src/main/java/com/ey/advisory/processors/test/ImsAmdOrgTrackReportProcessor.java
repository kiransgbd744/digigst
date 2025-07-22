package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("ImsAmdOrgTrackReportProcessor")
public class ImsAmdOrgTrackReportProcessor implements TaskProcessor {

    @Autowired
    FileStatusDownloadReportRepository downloadRepository;

    @Autowired
    @Qualifier("ImsAmdOrgTrackReportServiceImpl")
    AsyncReportDownloadService asyncReportDownloadService;

    @Override
    public void execute(Message message, AppExecContext context) {
        String jsonString = message.getParamsJson();
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        try {
            Long id = json.get("id").getAsLong();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Inside Ims Amendment Original Track Report processor with Report id : {}", id);
            }
            downloadRepository.updateStatus(id, ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
                    EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
            asyncReportDownloadService.generateReports(id);
        } catch (Exception ex) {
            String msg = "Exception occured while downloading csv report";
            LOGGER.error(msg, ex);
            throw new AppException(msg, ex);
        }
    }
}