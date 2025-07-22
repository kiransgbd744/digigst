package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.master.PRTaggingGroups;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.master.PRTaggingGroupsRepo;
import com.ey.advisory.app.data.entities.client.asprecon.GetIrnListEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRInwardEinvoiceTaggingEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRInwardEinvoiceTaggingRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.Lists;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 * 
 * Monitor job is responsible for Inward einvoice and QR linking 
 * And Initial time PR linking
 *
 */
@Slf4j
@Service("MonitorInwardEinvoiceQRAndPRTaggingProcessor")
@Transactional(value = "clientTransactionManager")
public class MonitorInwardEinvoiceQRAndPRTaggingProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepository;

	@Autowired
	GetIrnListingRepository listingRepo;

	@Autowired
	QRInwardEinvoiceTaggingRepository qrTaggingRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	PRTaggingGroupsRepo prGroupTag;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorInwardEinvoiceQRAndPRTaggingProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			if (listingRepo.getActiveCount() > 0) {
				if (qrTaggingRepo.getActiveCount() > 0) {

					// Fetch all active QR records
					List<QRInwardEinvoiceTaggingEntity> qrRecords = qrTaggingRepo
							.getActiveRecords();

					// Fetch all GetIrnListEntity records that match the IRNs
					// from the QR records
					List<String> irns = qrRecords.stream()
							.map(QRInwardEinvoiceTaggingEntity::getIrn)
							.collect(Collectors.toList());
					List<GetIrnListEntity> einvoices = listingRepo
							.findByIrnIn(irns);

					// Create a map of IRN to GetIrnListEntity for faster lookup
					Map<String, GetIrnListEntity> einvoiceMap = einvoices
							.stream()
							.collect(Collectors.toMap(GetIrnListEntity::getIrn,
									Function.identity()));

					// Update the GetIrnListEntity records and the QR records
					for (QRInwardEinvoiceTaggingEntity qrRecord : qrRecords) {
						GetIrnListEntity einvoice = einvoiceMap
								.get(qrRecord.getIrn());
						if (einvoice != null) {
							einvoice.setQrCodeValidated(
									qrRecord.getQrCodeValidated());
							einvoice.setQrCodeValidationResult(
									qrRecord.getQrCodeValidationResult());
							einvoice.setQrCodeMatchCount(
									qrRecord.getQrCodeMatchCount());
							einvoice.setQrCodeMismatchCount(
									qrRecord.getQrCodeMismatchCount());
							einvoice.setQrCodeMismatchAttributes(
									qrRecord.getQrCodeMismatchAttributes());
							// Set the QR tagging time status as localdatetime
							einvoice.setQrTaggingTimeStatus(
									LocalDateTime.now());

							qrRecord.setIsTagged(true);
							// set qr timestamp as well
							qrRecord.setModifiedOn(qrRecord.getCreatedOn());

						}
					}

					// Save all the updated GetIrnListEntity records and QR
					// records at once
					listingRepo.saveAll(einvoices);
					qrTaggingRepo.saveAll(qrRecords);

				} else {
					LOGGER.error("No QR records found for processing");
				}

				// check for PR linking

				// fetch all the Inward einvoice data where PR tagging is not
				// done and isDelete = false and getIrnDetSts = 'SUCCESS'

				LOGGER.debug(" Moving to PR tagging check for group {} ",
						group.getGroupName());

				boolean isUpdated = false;
				List<PRTaggingGroups> prGroups = prGroupTag
						.getAllPRTaggingReqGroupCode();
				// check if group is present in prGroups
				if (prGroups != null && !prGroups.isEmpty()) {

					List<String> eligibleGroups = prGroups.stream()
							.map(o -> o.getGroupCode())
							.collect(Collectors.toCollection(ArrayList::new));

					if (!eligibleGroups.contains(group.getGroupCode()))
						return;
					else {
						List<String> einvoiceList = listingRepo
								.findUntaggedAndSuccessfulRecordsWithDetails();
						// to check if the einvoiceList is not empty
						if (einvoiceList != null && !einvoiceList.isEmpty()) {

							// get the active pr doc numbers where at a time
							// 2000 envoiceList records
							// will be processed
							List<List<String>> chunksInwardEinvoice = Lists
									.partition(
											new ArrayList<String>(einvoiceList),
											2000);
							for (List<String> chunk : chunksInwardEinvoice) {
								Set<String> activePrDocKeys = getActivePrDocKeys(
										chunk);
								// to check if the activePrDocKeys is not empty

								if (activePrDocKeys != null
										&& !activePrDocKeys.isEmpty()) {
									// update the PR tagging status in
									// GetIrnListEntity
									int count = listingRepo.updateDocKeys(
											new ArrayList<>(activePrDocKeys));
									if (count > 0) {
										isUpdated = true;

									}
								} else {
									// print the log message
									LOGGER.error(
											"No active PR doc numbers found for processing");
									return;

								}
							}

						}

						else {
							// print the log message
							LOGGER.error(
									"No inward einvoice records found for processing fro groupcode {} ",
									group.getGroupCode());
							return;
						}

						if (isUpdated) {
							prGroupTag
									.updatePrTaggedGroups(group.getGroupCode());
						}
					}
				}
			}
		} catch (Exception ex) {

			throw new AppException(
					"Exception in MonitorInwardEinvoiceQRAndPRTaggingProcessor ");

		}
	}

	public Set<String> getActivePrDocKeys(List<String> docNumInwardEinvoice) {
		Set<String> activeDocNumPR = null;
		try {
			String queryString = "SELECT DISTINCT (SUPPLIER_GSTIN || '|' || "
					+ " CUST_GSTIN || '|' || DOC_NUM || '|' || DOC_TYPE || '|' || "
					+ " TO_VARCHAR(DOC_DATE, 'DD.MM.YYYY')) "
					+ " FROM ANX_INWARD_DOC_HEADER "
					+ " WHERE IS_PROCESSED = TRUE " + " AND IS_DELETE=FALSE "
					+ " AND (SUPPLIER_GSTIN || '|' || CUST_GSTIN || '|' "
					+ " || DOC_NUM || '|' || DOC_TYPE || '|' || "
					+ " TO_VARCHAR(DOC_DATE, 'DD.MM.YYYY')) in (:docKeys) ;";
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("docKeys", docNumInwardEinvoice);
			String msg = String.format(
					"Inside ANX_INWARD_DOC_HEADER docNumInwardEinvoice,query - %s ",
					queryString);

			LOGGER.debug(msg);

			@SuppressWarnings("unchecked")
			List<String> list = q.getResultList();
			activeDocNumPR = list.stream().map(o -> o.toString())
					.collect(Collectors.toCollection(HashSet::new));

		} catch (Exception ex) {
			String msg = "Exception Occured in "
					+ "getActivePRDocNumbers() method";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return activeDocNumPR;
	}

}
