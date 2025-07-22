package com.ey.advisory.app.services.search.docsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr2BGenerateReportTypeEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2BGenerateReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bGet2bRequestStatusRepository;
import com.ey.advisory.app.gstr2b.Gstr2bGet2bRequestStatusEntity;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Component("Gstr2BGenerateReportFileIdWiseServiceImpl")
public class Gstr2BGenerateReportFileIdWiseServiceImpl
		implements Gstr2BGenerateReportFileIdWiseService {

	@Autowired
	@Qualifier("Gstr2BGenerateReportTypeRepository")
	Gstr2BGenerateReportTypeRepository childRepo;


	@Autowired
	@Qualifier("Gstr2bGet2bRequestStatusRepository")
	Gstr2bGet2bRequestStatusRepository  parentConfigRepo;

	@Override
	public List<Gstr2BReportDownloadDto> getDownloadData(Long requestId) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside Gstr2BGenerateReportFileIdWiseServiceImpl for reqId {}",
						requestId);
			}
			Optional<Gstr2bGet2bRequestStatusEntity> parntEntityList = parentConfigRepo
					.findById(requestId);
			String reportType = "";
			String rptType = parntEntityList.get().getReportType();
			if (rptType != null && !rptType.isEmpty()
					&& rptType.equalsIgnoreCase("ITC")) {
				reportType = "GSTR2B_ItcAvailable_";
			}
			if (rptType != null && !rptType.isEmpty()
					&& rptType.equalsIgnoreCase("NITC")) {
				reportType = "GSTR2B_ItcNonAvailable_";
			}
			if (rptType != null && !rptType.isEmpty()
					&& rptType.equalsIgnoreCase("ALL")) {
				reportType = "GSTR2B_CompleteReport_";
			}
			if (rptType != null && !rptType.isEmpty()
					&& rptType.equalsIgnoreCase("REJ")) {
				reportType = "GSTR2B_RejectedReport_";
			}
			List<Gstr2BReportDownloadDto> respList = new ArrayList<>();
			if (parntEntityList.isPresent()) {
				Gstr2bGet2bRequestStatusEntity parntEntity = parntEntityList.get();
				if ("REPORT_GENERATED"
						.equalsIgnoreCase(parntEntity.getStatus())) {
					if (parntEntity.getFilePath() != null) {
						Gstr2BReportDownloadDto dto = new Gstr2BReportDownloadDto();
						dto.setConfigId(requestId);
						dto.setDwnld(true);
						dto.setReportType(reportType + "1");
						respList.add(dto);
					} else {
						List<Gstr2BGenerateReportTypeEntity> chdENtiy = childRepo
								.findByReportDwnldIdOrderByIdAsc(requestId);
						int counter = 1;
						for (Gstr2BGenerateReportTypeEntity entity : chdENtiy) {
							Gstr2BReportDownloadDto dto = new Gstr2BReportDownloadDto();
							dto.setConfigId(entity.getId());
							dto.setDwnld(true);
							dto.setReportType(reportType + counter);
							respList.add(dto);
							counter++;
						}
					}
				} else {
					Gstr2BReportDownloadDto dto = new Gstr2BReportDownloadDto();
					dto.setConfigId(requestId);
					dto.setDwnld(false);
					respList.add(dto);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Ended Gstr2BGenerateReportFileIdWiseServiceImpl for reqId {} with respList size {}",
							requestId, respList.size());
				}
			}
			return respList;

		} catch (Exception ex)

		{
			String msg = "Unexpected error while getting the report download data";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}

}
