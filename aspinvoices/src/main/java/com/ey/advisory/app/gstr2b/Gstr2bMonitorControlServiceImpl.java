package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.ControlGstnGetStatusEntity;
import com.ey.advisory.app.data.entities.client.MonitorGstnGetStatusEntity;
import com.ey.advisory.app.data.repositories.client.ControlGstnGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgsezHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BItcItemRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingB2bHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingB2baHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingCdnrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingCdnraHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingEcomHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingEcomaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BSuppSmryRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bAutoCommRepository;
import com.ey.advisory.app.data.repositories.client.MonitorGstnGetStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Service("Gstr2bMonitorControlServiceImpl")
public class Gstr2bMonitorControlServiceImpl
		implements Gstr2bMonitorControlService {

	@Autowired
	@Qualifier("ControlGstnGetStatusRepository")
	private ControlGstnGetStatusRepository controlGstnGetStatusRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingB2bHeaderRepository")
	private Gstr2GetGstr2BLinkingB2bHeaderRepository b2bHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingB2baHeaderRepository")
	private Gstr2GetGstr2BLinkingB2baHeaderRepository b2bAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingCdnrHeaderRepository")
	private Gstr2GetGstr2BLinkingCdnrHeaderRepository cdnrHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingCdnraHeaderRepository")
	private Gstr2GetGstr2BLinkingCdnraHeaderRepository cdnrAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BIsdHeaderRepository")
	private Gstr2GetGstr2BIsdHeaderRepository isdHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BIsdaHeaderRepository")
	private Gstr2GetGstr2BIsdaHeaderRepository isdAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BImpgHeaderRepository")
	private Gstr2GetGstr2BImpgHeaderRepository impgHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BImpgsezHeaderRepository")
	private Gstr2GetGstr2BImpgsezHeaderRepository impgSezHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BItcItemRepository")
	private Gstr2GetGstr2BItcItemRepository itcSummaryRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BSuppSmryRepository")
	private Gstr2GetGstr2BSuppSmryRepository suppSummaryRepo;

	@Autowired
	@Qualifier("MonitorGstnGetStatusRepository")
	private MonitorGstnGetStatusRepository monitorGstnGetStatusRepo;
	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("GstinApiCallRepository")
	private GstinApiCallRepository gstnStatusRepo;

	@Autowired
	@Qualifier("Gstr2bAutoCommRepository")
	private Gstr2bAutoCommRepository gstr2bAutoCommRepo;

	@Autowired
	private Gstr2BGetRevIntgHelper revIntgHelper;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingEcomHeaderRepository")
	private Gstr2GetGstr2BLinkingEcomHeaderRepository ecomHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingEcomaHeaderRepository")
	private Gstr2GetGstr2BLinkingEcomaHeaderRepository ecomaHeaderRepo;

	@Autowired
	private Gstr2BDupCheckProcCallHandler procCallHandler;

	@Override
	public void monitorControlEntries(
			List<MonitorGstnGetStatusEntity> monitorEntities) {

		String gstin = null;
		String taxPeriod = null;
		Long invocationId = null;
		Long monitorId = null;
		boolean isAuto = false;
		String groupCode = TenantContext.getTenantId();
		for (MonitorGstnGetStatusEntity monitorEntity : monitorEntities) {

			try {
				gstin = monitorEntity.getGstin();
				taxPeriod = monitorEntity.getTaxPeriod();
				invocationId = monitorEntity.getInvocationId();
				monitorId = monitorEntity.getMonitorId();
				isAuto = monitorEntity.isAutoRequest();

				List<ControlGstnGetStatusEntity> controlEntities = controlGstnGetStatusRepo
						.findByMonitorId(monitorId);

				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"There are %s records in Control table for "
									+ "group: '%s' with monitorId: %s",
							controlEntities.size(), monitorId, groupCode);
					LOGGER.debug(logMsg);
				}
				Set<String> jobStatuses = filterOnlyJobStatus(controlEntities);
				if (jobStatuses.contains("FAILED")) {
					// update only batch as failed and continue
					String logMsg = String.format(
							"There are 'FAILED' records in Control table for"
									+ " group: '%s' with monitorId: %s ,"
									+ " gstin: %s and taxPeriod: %s ."
									+ " Hence marking Failed in Batch Table",
							monitorId, groupCode, gstin, taxPeriod);
					LOGGER.error(logMsg);
					batchRepo.updateStatus(APIConstants.FAILED,
							APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B,
							gstin, taxPeriod, LocalDateTime.now(), true,
							"ER-1000", logMsg);

					monitorGstnGetStatusRepo.updateStatus(monitorId,
							LocalDateTime.now(), true);

					if (isAuto) {
						gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin,
								taxPeriod, APIConstants.GSTR2B,
								APIConstants.FAILED, "");
						gstnStatusRepo.updateStatus(gstin, taxPeriod,
								APIConstants.GSTR2B, APIConstants.FAILED, null,
								null, LocalDateTime.now());
					} else {
						gstnStatusRepo.updateStatus(gstin, taxPeriod,
								APIConstants.GSTR2B, APIConstants.FAILED, null,
								null, LocalDateTime.now());
					}
					continue;
				}
				if (jobStatuses.contains("INPROGRESS")
						|| jobStatuses.contains("INITIATED")) {
					continue;
				} else {

					updateAllHeaderTables(invocationId, gstin, taxPeriod);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								" Gstr2b dup check proc method getting called for groupCode - {} ",
								groupCode);
					}

					String response = procCallHandler.callDupCheckProcs(gstin,
							taxPeriod, invocationId);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								" Gstr2b dup check proc method ended for groupCode - {} ",
								groupCode);
					}
					if (response != null
							&& "SUCCESS".equalsIgnoreCase(response)) {

						batchRepo.updateStatus(APIConstants.SUCCESS,
								APIConstants.GSTR2B_GET_ALL,
								APIConstants.GSTR2B, gstin, taxPeriod,
								LocalDateTime.now(), true, null, null);

						monitorGstnGetStatusRepo.updateStatus(monitorId,
								LocalDateTime.now(), true);

						if (LOGGER.isDebugEnabled())
							LOGGER.debug(
									"GET2B Success Handler, About to post Report Job - {}",
									groupCode);

						if (isAuto) {
							gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin,
									taxPeriod, APIConstants.GSTR2B,
									APIConstants.SUCCESS, "");
							gstnStatusRepo.updateStatus(gstin, taxPeriod,
									APIConstants.GSTR2B, APIConstants.SUCCESS,
									null, null, LocalDateTime.now());
						} else {

							gstnStatusRepo.updateStatus(gstin, taxPeriod,
									APIConstants.GSTR2B, APIConstants.SUCCESS,
									null, null, LocalDateTime.now());
							if (LOGGER.isDebugEnabled())
								LOGGER.debug(
										"GET2B Success Handler, Processing is completed - {}",
										groupCode);

							JsonObject jsonParams = new JsonObject();
							jsonParams.addProperty("gstin", gstin);
							jsonParams.addProperty("taxPeriod", taxPeriod);

							jsonParams.addProperty("invocationId",
									invocationId);

							revIntgHelper.persistMonitorTaggingDtls(gstin,
									taxPeriod, invocationId);

							revIntgHelper.postRevIntgJob(invocationId, gstin,
									taxPeriod);
							asyncJobsService.createJob(groupCode,
									JobConstants.GSTR2B_GET_CSV_REPORT,
									jsonParams.toString(), "SYSTEM", 1L, null,
									null);
							if (LOGGER.isDebugEnabled())
								LOGGER.debug(
										"GET2B Success Handler, posted Report Job - {}",
										groupCode);

						}
					} else {

						String msg = "Gstr2b Proc Call Returned Invalid Response.";

						if (isAuto) {
							gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin,
									taxPeriod, APIConstants.GSTR2B,
									APIConstants.FAILED, msg);
							gstnStatusRepo.updateStatus(gstin, taxPeriod,
									APIConstants.GSTR2B, APIConstants.FAILED,
									null, msg, LocalDateTime.now());
						} else {
							gstnStatusRepo.updateStatus(gstin, taxPeriod,
									APIConstants.GSTR2B, APIConstants.FAILED,
									null, msg, LocalDateTime.now());
						}
						batchRepo.updateStatus(APIConstants.FAILED,
								APIConstants.GSTR2B_GET_ALL,
								APIConstants.GSTR2B, gstin, taxPeriod,
								LocalDateTime.now(), false, "ER-1000", msg);

						// TO-DO doubt
						monitorGstnGetStatusRepo.updateStatus(monitorId,
								LocalDateTime.now(), false);

					}
				}
			} catch (Exception ee) {
				String msg1 = String.format(
						"Exception occured while monitoring control entries. In %s",
						monitorEntity.toString());
				LOGGER.error("Exception while handling success "
						+ "GSTR2BMultiResponseProcessor ", ee);
				String msg = ee.getMessage().length() > 500
						? ee.getMessage().substring(0, 498) : ee.getMessage();
				if (isAuto) {
					gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.FAILED, msg);
					gstnStatusRepo.updateStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
							LocalDateTime.now());
				} else {
					gstnStatusRepo.updateStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
							LocalDateTime.now());
				}
				batchRepo.updateStatus(APIConstants.FAILED,
						APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B, gstin,
						taxPeriod, LocalDateTime.now(), false, "ER-1000", msg);

				monitorGstnGetStatusRepo.updateStatus(monitorId,
						LocalDateTime.now(), true);

				LOGGER.error(msg);
			}
		}
	}

	private void updateAllHeaderTables(Long invocationId, String gstin,
			String taxPeriod) {
		try {
			int cntItc = itcSummaryRepo.softlyDeleteHeader(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int itcSaveCnt = itcSummaryRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : Itc Summary table"
								+ " Row deleted {} and saved {}",
						cntItc, itcSaveCnt);
			}
			int suppSmryCnt = suppSummaryRepo.softlyDeleteHeader(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int suppSmrySaveCnt = suppSummaryRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : Supplier Summary"
								+ " table Row deleted {} and saved {}",
						suppSmryCnt, suppSmrySaveCnt);
			}

			int b2bCnt = b2bHeaderRepo.softlyDeleteHeader(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int b2bSaveCnt = b2bHeaderRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : B2B table "
								+ "Row deleted {} and Saved {}",
						b2bCnt, b2bSaveCnt);
			}

			int b2bAcnt = b2bAHeaderRepo.softlyDeleteHeader(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int b2baSaveCnt = b2bAHeaderRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : B2BA  table"
								+ " Row deleted {} and saved",
						b2bAcnt, b2baSaveCnt);
			}
			int cdnrCnt = cdnrHeaderRepo.softlyDeleteHeader(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int cdnrSaveCnt = cdnrHeaderRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : CDNR table Row"
								+ " deleted {} and saved {}",
						cdnrCnt, cdnrSaveCnt);
			}
			int cdnrACnt = cdnrAHeaderRepo.softlyDeleteHeader(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int cdnrASaveCnt = cdnrAHeaderRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : CDNRA table"
								+ " Row deleted {} and saved {}",
						cdnrACnt, cdnrASaveCnt);
			}
			int isdcnt = isdHeaderRepo.softlyDeleteHeader(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int isdSavecnt = isdHeaderRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : ISD table"
								+ " Row deleted {} and saved {}",
						isdcnt, isdSavecnt);
			}
			int isdACnt = isdAHeaderRepo.softlyDeleteHeader(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int isdASaveCnt = isdAHeaderRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : ISDA table "
								+ "Row deleted {} and saved {}",
						isdACnt, isdASaveCnt);
			}
			int impgCnt = impgHeaderRepo.softlyDeleteHeader(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int impgSaveCnt = impgHeaderRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : IMPG table "
								+ " Row deleted {} and saved {}",
						impgCnt, impgSaveCnt);
			}
			int impgSezCnt = impgSezHeaderRepo.softlyDeleteHeader(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int impgSezSaveCnt = impgSezHeaderRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : IMPG SEZ table"
								+ " Row deleted {} and saved {}",
						impgSezCnt, impgSezSaveCnt);
			}

			int ecomCnt = ecomHeaderRepo.softlyDeleteHeaderMultiProcessor(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int ecomSaveCnt = ecomHeaderRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : Ecom table "
								+ "Row deleted {} and Saved {}",
						ecomCnt, ecomSaveCnt);
			}

			int ecomAcnt = ecomaHeaderRepo.softlyDeleteHeaderMultiProcessor(
					Arrays.asList(invocationId), LocalDateTime.now(), gstin,
					taxPeriod);
			int ecomaSaveCnt = ecomaHeaderRepo.updateStatus(invocationId,
					LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2bMonitorControlServiceImpl : B2BA  table"
								+ " Row deleted {} and saved",
						ecomAcnt, ecomaSaveCnt);
			}
		} catch (Exception ee) {
			String msg = "Exception occured while softdeleting header tables";
			LOGGER.error(msg, ee);
			throw new AppException(ee, msg);
		}

	}

	private Set<String> filterOnlyJobStatus(
			List<ControlGstnGetStatusEntity> controlEntities) {
		return controlEntities.stream().distinct()
				.map(ControlGstnGetStatusEntity::getJobStatus)
				.collect(Collectors.toSet());
	}

}
