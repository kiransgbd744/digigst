package com.ey.advisory.app.services.annexure1fileupload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr3bEntity;
import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("BasicGstr3BUploadDaoImpl")
public class BasicGstr3BUploadDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BasicGstr3BUploadDaoImpl.class);

	public List<Gstr3bEntity> loadBasicSummarySection() {
		// TODO Auto-generated method stub
		
	/*	Set<String> gstin,
		Set<String> taxPeriod, Set<String> tableSection*/

	/*	StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			build.append(" GSTIN IN :gstin ");

		}
		if (taxPeriod != null) {

			build.append(" AND RETURN_PERIOD IN :taxPeriod ");
		}
		if (tableSection != null) {

			build.append(" AND TABLE_SECTION IN :tableSection ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");
*/
		String queryStr = createQueryString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Executing Gstr3B Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

		/*	if (gstin != null && !gstin.isEmpty()) {
				q.setParameter("gstin", gstin);
			}
			if (tableSection != null) {
				q.setParameter("tableSection", tableSection);
			}
			if (taxPeriod != null) {
				q.setParameter("taxPeriod", taxPeriod);
			}
*/
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Gstr3bEntity> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Gstr3B Query Execution getting the data ----->"
					+ retList);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.debug("Executing Gstr3B Query ", e);
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr3bEntity convert(Object[] arr) {
		Gstr3bEntity obj = new Gstr3bEntity();
		obj.setGstin((String) arr[0]);
		obj.setTableSection((String) arr[1]);
		obj.setTableDescription((String) arr[2]);
		obj.setIgstAmnt((BigDecimal) arr[3]);
		obj.setCgstAmnt((BigDecimal) arr[4]);
		obj.setSgstAmnt((BigDecimal) arr[5]);
		obj.setCessAmnt((BigDecimal) arr[6]);
		obj.setRetPeriod((String) arr[7]);
		LOGGER.debug("Array data Setting to Dto");
		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString() {
		// TODO Auto-generated method stub
		LOGGER.debug("Gstr3B Processing  Query Execution BEGIN ");

		String queryStr = "SELECT GSTIN,TABLE_SECTION,TABLE_DESCRIPTION,"
				+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
				+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
				+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
				+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT,RETURN_PERIOD "
				+ "FROM GSTR3B_ITC_AS_PROCESSED "
				+ "WHERE FILE_ID IN (SELECT MAX(FILE_ID) FROM GSTR3B_ITC_AS_PROCESSED ) "
				+ "GROUP BY GSTIN,RETURN_PERIOD,TABLE_SECTION,TABLE_DESCRIPTION";
		LOGGER.debug("Gstr3B Processing  Query Execution END ");
		return queryStr;
	}
}
