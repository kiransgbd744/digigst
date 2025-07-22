/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.pr2b.reports;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.InwardEinvoiceReconAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2APRReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceChildRReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceReconAddlReportsRepository;
import com.ey.advisory.asprecon.gstr2.ap.recon.Gstr2ApManualGenerateReportServiceImpl;
import com.ey.advisory.asprecon.gstr2.ap.recon.Gstr2NonApManualGenerateReportService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.inwardEinvoice.initiateRecon.InwardEinvoiceInitiateReconFetchReportDetailsImpl;
import com.ey.advisory.service.asprecon.auto.recon.erp.report.Gstr2AutoReconErpReportFetchDetails;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2ReconReportReGenerateHandler")
public class Gstr2ReconReportReGenerateHandler {

	@Autowired
	@Qualifier("Gstr2InitiateRecon2BPRFetchReportDetailsImpl")
	Gstr2InitiateRecon2BPRFetchReportDetailsImpl fetch2BPRReportDetails;

	@Autowired
	@Qualifier("Gstr2NonApManualGenerateReportServiceImpl")
	Gstr2NonApManualGenerateReportService nonApFetchReportDetails;

	@Autowired
	@Qualifier("Gstr2ApManualGenerateReportServiceImpl")
	Gstr2ApManualGenerateReportServiceImpl apFetchReportDetails;

	@Autowired
	@Qualifier("Gstr2AutoReconErpReportFetchDetailsImpl")
	Gstr2AutoReconErpReportFetchDetails erpReport;

	@Autowired
	@Qualifier("Gstr2Recon2BPRAddlReportsRepository")
	Gstr2Recon2BPRAddlReportsRepository dwnld2bprRepo;
	
	@Autowired
	@Qualifier("InwardEinvoiceReconAddlReportsRepository")
	InwardEinvoiceReconAddlReportsRepository inwardEinvoiceAddRepo;
	
	@Autowired
	@Qualifier("InwardEinvoiceChildRReportTypeRepository")
	private InwardEinvoiceChildRReportTypeRepository inwardEinvoiceChildRepo;
	
	@Autowired
	@Qualifier("InwardEinvoiceInitiateReconFetchReportDetailsImpl")
	InwardEinvoiceInitiateReconFetchReportDetailsImpl fetchInwardEinvoiceReportDetails;
	
	@Autowired
	@Qualifier("Gstr2Recon2BPRReportTypeRepository")
	private Gstr2Recon2BPRReportTypeRepository dwnld2bprChildRepo;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository dwnld2aprRepo;

	@Autowired
	@Qualifier("Gstr2Recon2APRReportTypeRepository")
	private Gstr2Recon2APRReportTypeRepository dwnld2aprChildRepo;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	public void reportHandle(Long configId, String reconType) {

		// NON_AP_M_2APR, AUTO_2APR, AP_M_2APR, 2BPR

		try {

			if ("AP_M_2APR".equalsIgnoreCase(reconType)
					|| "NON_AP_M_2APR".equalsIgnoreCase(reconType)
					|| "AUTO_2APR".equalsIgnoreCase(reconType)) {

				List<Gstr2ReconAddlReportsEntity> repoEntity = dwnld2aprRepo
						.findByConfigId(configId);

				List<Long> reportIds = repoEntity.stream()
						.filter(o -> o.getIsReportProcExecuted() == null
								|| !o.getIsReportProcExecuted())
						.map(o -> o.getId()).collect(Collectors.toList());

				int count = dwnld2aprChildRepo
						.deleteByReportDwnldIdIn(reportIds);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2ReconReportReGenerateHandler, Invoking "
									+ "reportHandle() configId %s,- : "
									+ "no of row deleted form child table %d ",
							configId.toString(), count);
					LOGGER.debug(msg);

				}

				List<String> reportList = repoEntity.stream()
						.filter(o -> o.getIsReportProcExecuted() == null
								|| !o.getIsReportProcExecuted())
						.map(o -> o.getReportType())
						.collect(Collectors.toList());

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2ReconReportReGenerateHandler, Invoking "
									+ "reportHandle() configId %s,- : "
									+ "reports to be regenerated %s ",
							configId.toString(), reportList);
					LOGGER.debug(msg);

				}

				Gstr2ReconConfigEntity entity = reconConfigRepo
						.findByConfigId(configId);

				if ("AP_M_2APR".equalsIgnoreCase(reconType)) {

					apFetchReportDetails.generateReport(configId,
							entity.getEntityId(), reportList);

				} else if ("NON_AP_M_2APR".equalsIgnoreCase(reconType)) {

					nonApFetchReportDetails.generateReport(configId, reportList);

				} else if ("AUTO_2APR".equalsIgnoreCase(reconType)) {

					erpReport.generateReport(configId, entity.getEntityId());

				}

			} else if ("2BPR".equalsIgnoreCase(reconType)) {
				List<Gstr2Recon2BPRAddlReportsEntity> repoEntity = dwnld2bprRepo
						.findByConfigId(configId);

				List<Long> reportIds = repoEntity.stream()
						.filter(o -> o.getIsReportProcExecuted() == null
								|| !o.getIsReportProcExecuted())
						.map(o -> o.getId()).collect(Collectors.toList());

				int count = dwnld2bprChildRepo
						.deleteByReportDwnldIdIn(reportIds);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2ReconReportReGenerateHandler, Invoking "
									+ "reportHandle() configId %s,- : "
									+ "no of row deleted form child table %d ",
							configId.toString(), count);
					LOGGER.debug(msg);
				}

				List<String> reportList = repoEntity.stream()
						.filter(o -> o.getIsReportProcExecuted() == null
								|| !o.getIsReportProcExecuted())
						.map(o -> o.getReportType())
						.collect(Collectors.toList());

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2ReconReportReGenerateHandler, Invoking "
									+ "reportHandle() configId %s,- : "
									+ "reports to be regenerated %s ",
							configId.toString(), reportList);
					LOGGER.debug(msg);

				}

				fetch2BPRReportDetails.get2BPRReconReportData(configId,
						reportList);
			}else if ("EINVPR".equalsIgnoreCase(reconType)) {
				List<InwardEinvoiceReconAddlReportsEntity> repoEntity = inwardEinvoiceAddRepo
						.findByConfigId(configId);

				List<Long> reportIds = repoEntity.stream()
						.filter(o -> o.getIsReportProcExecuted() == null
								|| !o.getIsReportProcExecuted())
						.map(o -> o.getId()).collect(Collectors.toList());

				int count = inwardEinvoiceChildRepo
						.deleteByReportDwnldIdIn(reportIds);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2ReconReportReGenerateHandler, Invoking "
									+ "reportHandle() configId %s,- : "
									+ "no of row deleted form child table %d ",
							configId.toString(), count);
					LOGGER.debug(msg);
				}

				List<String> reportList = repoEntity.stream()
						.filter(o -> o.getIsReportProcExecuted() == null
								|| !o.getIsReportProcExecuted())
						.map(o -> o.getReportType())
						.collect(Collectors.toList());

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2ReconReportReGenerateHandler, Invoking "
									+ "reportHandle() configId %s,- : "
									+ "reports to be regenerated %s ",
							configId.toString(), reportList);
					LOGGER.debug(msg);

				}

				fetchInwardEinvoiceReportDetails.getInwardEinvoiceReconReportData(configId,reportList);
			}

		} catch (

		Exception ex) {
			LOGGER.error("Error occured in Gstr2ReconReportReGenerateHandler  "
					+ "configId {} ", configId.toString());
			
			reconConfigRepo.updateReconConfigStatusAndReportName(
					"REPORT_GENERATION_FAILED", null, LocalDateTime.now(),
					configId);
			
			throw new AppException(ex);
		}
	}
}
