package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.AutoReconRequestEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2GenerateReportTypeEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoReconRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2GenerateReportTypeRepository;
import com.ey.advisory.app.filereport.ReportDownloadDto;
import com.ey.advisory.app.filereport.ReportFileStatusDownloadService;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr2GenerateReportFileIdWiseServiceImpl")
public class Gstr2GenerateReportFileIdWiseServiceImpl
		implements ReportFileStatusDownloadService {

	@Autowired
	@Qualifier("Gstr2GenerateReportTypeRepository")
	Gstr2GenerateReportTypeRepository childRepo;

	@Autowired
	@Qualifier("AutoReconRequestRepository")
	AutoReconRequestRepository parentConfigRepo;

	@Override
	public List<ReportDownloadDto> getDownloadData(Long requestId) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside Gstr2GenerateReportFileIdWiseServiceImpl for reqId {}",
						requestId);
			}
			Optional<AutoReconRequestEntity> parntEntityList = parentConfigRepo
					.findById(requestId);
			List<ReportDownloadDto> respList = new ArrayList<>();
			if (parntEntityList.isPresent()) {
				AutoReconRequestEntity parntEntity = parntEntityList.get();
				if ("REPORT_GENERATED"
						.equalsIgnoreCase(parntEntity.getStatus())) {
					if (parntEntity.getFilePath() != null) {
						ReportDownloadDto dto = new ReportDownloadDto();
						dto.setConfigId(requestId);
						dto.setDwnld(true);
						dto.setReportType(parntEntity.getRequestId()
								+ "_AIM_ReconReport_1");
						respList.add(dto);
					} else {
						List<Gstr2GenerateReportTypeEntity> chdENtiy = childRepo
								.findByReportDwnldIdOrderByIdAsc(requestId);
						int counter = 1;
						for (Gstr2GenerateReportTypeEntity entity : chdENtiy) {
							ReportDownloadDto dto = new ReportDownloadDto();
							dto.setConfigId(entity.getId());
							dto.setDwnld(true);
							dto.setReportType(entity.getReportDwnldId()
									+ "_AIM_ReconReport_" + counter);
							respList.add(dto);
							counter++;
						}
					}
				} else {
					ReportDownloadDto dto = new ReportDownloadDto();
					dto.setConfigId(requestId);
					dto.setDwnld(false);
					respList.add(dto);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Ended Gstr2GenerateReportFileIdWiseServiceImpl for reqId {} with respList size {}",
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
