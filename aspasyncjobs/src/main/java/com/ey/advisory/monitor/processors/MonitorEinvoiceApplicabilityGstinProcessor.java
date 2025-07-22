/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.EinvGstinClientEntity;
import com.ey.advisory.app.data.repositories.client.EinvClientGstinRepository;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.EinvGstinMasterEntity;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.EinvMasterGstinRepository;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Service("MonitorEinvoiceApplicabilityGstinProcessor")
public class MonitorEinvoiceApplicabilityGstinProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private EinvClientGstinRepository einvClientGstinRepo;

	@Autowired
	private EinvMasterGstinRepository einvMasterGstinRepo;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		List<EinvGstinClientEntity> einvClientGstinEntities = einvClientGstinRepo
				.findByisSynced(false);

		if (einvClientGstinEntities.isEmpty()) {
			if (LOGGER.isInfoEnabled())
				LOGGER.info(
						"No Einvoice applicability gstins found for group {} ",
						group.getGroupCode());
			return;
		}
		// handle try-catch and loggers
		einvClientGstinEntities.forEach(e -> {

			try {
				// update if cancalation date is avaiable in client gstin else
				// insert
				EinvGstinMasterEntity entity = einvMasterGstinRepo
						.findByGstin(e.getGstin());

				if (entity == null) {
					EinvGstinMasterEntity einvMaster = new EinvGstinMasterEntity();

					einvMaster.setGstin(e.getGstin() != null ? e.getGstin().toUpperCase() : e.getGstin());
					einvMaster.setSource(e.getSource());
					einvMaster.setCreatedDate(LocalDateTime.now());
					einvMaster.setCancelledDate(e.getCancelledDate());
					einvMaster.setPan(e.getPan());
					einvMasterGstinRepo.save(einvMaster);
				}
				if (!Strings.isNullOrEmpty(e.getCancelledDate())
						&& Strings.isNullOrEmpty(entity.getCancelledDate())) {
					einvMasterGstinRepo.updateCancellationDate(e.getGstin(),
							e.getCancelledDate(), LocalDateTime.now());
				}
			} catch (DataIntegrityViolationException ex) {

				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn(
							"Gstin already exists in einvoive applicability master table : {}",
							e.getGstin());
				}
			}

		});

		einvClientGstinEntities.forEach(e -> e.setSynced(true));

		einvClientGstinRepo.saveAll(einvClientGstinEntities);

	}

}
