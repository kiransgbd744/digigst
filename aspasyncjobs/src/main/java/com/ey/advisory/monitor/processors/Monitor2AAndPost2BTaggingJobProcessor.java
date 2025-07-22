package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2BMonitorTagging2ARepository;
import com.ey.advisory.app.gstr2b.Gstr2BMonitorTagging2AEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Monitor2AAndPost2BTaggingJobProcessor")
public class Monitor2AAndPost2BTaggingJobProcessor
		extends DefaultMultiTenantTaskProcessor {

	private static final List<String> GSTR2A_SECTIONS = ImmutableList.of(
			APIConstants.B2B.toUpperCase(), APIConstants.B2BA.toUpperCase(),
			APIConstants.CDNR.toUpperCase(), APIConstants.CDNRA.toUpperCase(),
			APIConstants.ECOM.toUpperCase(), APIConstants.ECOMA.toUpperCase());

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	private GetAnx1BatchRepository getAnx1BatchRepository;

	@Autowired
	private Gstr2BMonitorTagging2ARepository monitorTaggingRepo;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			List<String> activeGstinsList = gstNDetailRepository
					.getActiveGstins();
			Pair<String, String> taxPeriods = GenUtil
					.getCurrentAndPrevTaxPeriod();
			String pervTaxPeriod = taxPeriods.getValue1();

			// Fetch GSTR2A details
			List<GetAnx1BatchEntity> getGstr2ADetails = getAnx1BatchRepository
					.findBatchDetails(activeGstinsList,
							Arrays.asList(pervTaxPeriod),
							APIConstants.GSTR2A.toUpperCase(), GSTR2A_SECTIONS);

			if (getGstr2ADetails == null || getGstr2ADetails.isEmpty()) {
				LOGGER.error(
						"No Successful combination of GSTR2A is available for the previous tax period {}, Hence returning it.",
						pervTaxPeriod);
				return;
			}

			// Extract successful GSTINs from GSTR2A
			List<String> gstr2ASuccessGstins = getGstr2ADetails.stream()
					.map(GetAnx1BatchEntity::getSgstin)
					.collect(Collectors.toList());

			// Fetch GSTR2B details
			List<GetAnx1BatchEntity> getGstr2BDetails = getAnx1BatchRepository
					.findBatchDetails(gstr2ASuccessGstins,
							Arrays.asList(pervTaxPeriod),
							APIConstants.GSTR2B.toUpperCase(),
							Arrays.asList("GSTR2B_GET_ALL"));

			if (getGstr2BDetails == null || getGstr2BDetails.isEmpty()) {
				LOGGER.error(
						"No Successful combination of GSTR2B is available for the previous tax period {}, Hence returning it.",
						pervTaxPeriod);
				return;
			}

			// Extract successful GSTINs from GSTR2B
			List<String> gstr2BSuccessGstins = getGstr2BDetails.stream()
					.map(GetAnx1BatchEntity::getSgstin)
					.collect(Collectors.toList());

			// Process GSTR2B details and persist monitor tagging details
			gstr2BSuccessGstins.forEach(eligibleGstins -> {
				monitorTaggingRepo
						.findByGstinAndTaxPeriodAndIsActiveTrueAndSource(
								eligibleGstins, pervTaxPeriod,
								APIConstants.GSTR2A)
						.ifPresent(
								// GSTR2A entry found, do nothing
								entry -> LOGGER.error(
										"GSTR2A entry already exists for GSTIN: {} and tax period: {}, skipping.",
										eligibleGstins, pervTaxPeriod));

				// GSTR2A entry not found, persist monitor tagging details for
				// GSTR2B
				monitorTaggingRepo
						.findByGstinAndTaxPeriodAndIsActiveTrueAndSource(
								eligibleGstins, pervTaxPeriod,
								APIConstants.GSTR2B)
						.ifPresent(entry -> persistMonitorTaggingDtls(
								eligibleGstins, pervTaxPeriod,
								entry.getInvocationId()));
			});

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

	private void persistMonitorTaggingDtls(String gstin, String taxPeriod,
			Long invocationId) {
		Gstr2BMonitorTagging2AEntity monitorTagg = new Gstr2BMonitorTagging2AEntity();
		monitorTagg.setGstin(gstin);
		monitorTagg.setTaxPeriod(taxPeriod);
		monitorTagg.setStartedOn(LocalDateTime.now());
		monitorTagg.setStatus(APIConstants.INITIATED);
		monitorTagg.setInvocationId(invocationId);
		monitorTagg.setCreatedOn(LocalDateTime.now());
		monitorTagg.setCreatedBy("SYSTEM");
		monitorTagg.setIsActive(true);
		monitorTagg.setSource(APIConstants.GSTR2A);
		monitorTaggingRepo.inactiveExistingEntries(gstin, taxPeriod);
		monitorTaggingRepo.save(monitorTagg);
	}
}
