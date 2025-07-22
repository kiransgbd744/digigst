package com.ey.advisory.app.inward.einvoice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository;
import com.ey.advisory.common.AppException;
import com.google.common.collect.Lists;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.Jain
 *
 */
@Slf4j
@Component("InwardEinvoiceInvMngtPRTaggingServiceImpl")
@Transactional(value = "clientTransactionManager")
public class InwardEinvoiceInvMngtPRTaggingServiceImpl
		implements InwardEinvoiceInvMngtPRTaggingService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GetIrnListingRepository")
	public GetIrnListingRepository listingRepo;

	@Override
	public String updatePRTagging(EinvoiceNestedDetailedReportRequestDto dto) {

		try {
			// check if ids are present in dto
			if (dto.getIds() == null || dto.getIds().isEmpty()) {
				throw new AppException(
						"IRN Ids are not present in the request");
			} else {
				// get the list of ids from dto
				List<Long> ids = dto.getIds();
				// get the 5 part key from listing repo
				List<String> fivePartKeyList = listingRepo
						.findSuccessfulRecordsWithDetailsWithListingId(ids);

				// get the active pr doc numbers where at a time 2000
				// envoiceList records
				// will be processed
				List<List<String>> chunksInwardEinvoice = Lists.partition(
						new ArrayList<String>(fivePartKeyList), 2000);
				for (List<String> chunk : chunksInwardEinvoice) {
					
					LOGGER.debug(" chunk - {} ",chunk);
					
					Set<String> activePrDocKeys = getActivePrDocKeys(chunk);
					// to check if the activePrDocKeys is not empty
					
					LOGGER.debug(" activePrDocKeys - {} ",activePrDocKeys);
					if (activePrDocKeys != null && !activePrDocKeys.isEmpty()) {
						// update the PR tagging status in GetIrnListEntity
						
						int count = listingRepo.updateDocKeys(new ArrayList<>(activePrDocKeys));
						LOGGER.debug(" number of pr updated {} ",count);

					} else {
						// print the log message
						LOGGER.error(
								"No active PR doc numbers found for processing");

					}
				}

			}
			return "SUCCESS";
		} catch (Exception ex) {
			LOGGER.error(
					"Exception in InwardEinvoiceInvMngtPRTaggingServiceImpl {} ",
					ex);

			throw new AppException(ex);
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
			activeDocNumPR = list.stream()
                    .map(o -> (String) o) 
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
