package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3bComuteCopyToUserInputDaoImpl")
public class Gstr3bComuteCopyToUserInputDaoImpl
		implements Gstr3bComuteCopyToUserInputDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Override
	public List<Gstr3BGstinAspUserInputDto> gstr3bCopyData(String taxPeriod,
			String gstin) throws AppException {

		try {
			String queryString = createQueryString(gstin, taxPeriod);
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstin", gstin);
			q.setParameter("taxPeriod", taxPeriod);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from compute "
						+ "table for given gstin " + gstin + " and taxPeriod"
						+ taxPeriod + "and query = " + queryString + "");
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<Gstr3BGstinAspUserInputDto> retList = list.parallelStream()
					.map(o -> convertUserInput(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3bComuteCopyToUserInputDaoImpl.gstr3bCopyData");
		}
	}
	private String createQueryString(String gstin, String taxPeriod) {

		String query = "Select CESS,CGST,IGST,SGST,TAXABLE_VALUE,SECTION_NAME,"
				+ " SUBSECTION_NAME,POS,INTERSTATE,INTRA_STATE "
				+ " from GSTR3B_ASP_COMPUTE"
				+ " Where GSTIN = :gstin AND TAX_PERIOD = :taxPeriod AND "
				+ " IS_ACTIVE = TRUE";
		return query;

}
	private Gstr3BGstinAspUserInputDto convertUserInput(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr3BGstinAspUserInputDto object";
			LOGGER.debug(str);
		}
		Gstr3BGstinAspUserInputDto obj = new Gstr3BGstinAspUserInputDto();
		obj.setCess((BigDecimal) arr[0]);
		obj.setCgst((BigDecimal) arr[1]);
		obj.setIgst((BigDecimal) arr[2]);
		obj.setSgst((BigDecimal) arr[3]);
		obj.setTaxableVal((BigDecimal) arr[4]);
		obj.setSectionName((String) arr[5]);
		obj.setSubSectionName((String) arr[6]);
		obj.setPos((String) arr[7]);
		obj.setInterState((BigDecimal) arr[8]);
		obj.setIntraState((BigDecimal) arr[9]);
		return obj;
	}
	
	}
