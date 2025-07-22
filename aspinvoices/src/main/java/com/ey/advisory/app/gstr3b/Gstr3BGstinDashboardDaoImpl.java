/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("Gstr3BGstinDashboardDaoImpl")
public class Gstr3BGstinDashboardDaoImpl implements Gstr3BGstinDashboardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr3BGstinAspUserInputDto> getGstinComputeDtoList(String gstin,
			String taxPeriod) throws AppException {
		try {
			String queryString = createComputeQueryString(gstin, taxPeriod);
			// return getInputHardcodedData();
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
			List<Gstr3BGstinAspUserInputDto> retList = list.stream()
					.map(o -> convertUserInput(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3BGstinDashboardDaoImpl.getgstinComputeDtoList");
		}
	}

	@Override
	public List<Gstr3BGstinAspUserInputDto> getGstinUserInputDtoList(
			String gstin, String taxPeriod) throws AppException {
		try {
			String queryString = createUserInputQueryString(gstin, taxPeriod);
			// return getInputHardcodedData();
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstin", gstin);
			q.setParameter("taxPeriod", taxPeriod);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from user Input "
						+ "table for given gstin " + gstin + " and taxPeriod"
						+ taxPeriod + "and query = " + queryString + "");
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<Gstr3BGstinAspUserInputDto> retList = list.stream()
					.map(o -> convertUserInputNew(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3BGstinDashboardDaoImpl.getgstinuserInputDtoList");
		}
	}

	private String createComputeQueryString(String gstin, String taxPeriod) {

		String query = "select CESS ,CGST,IGST,SGST,TAXABLE_VALUE,SECTION_NAME,"
				+ "SUBSECTION_NAME,POS,INTERSTATE,INTRA_STATE,UI_ROW_NAME "
				+ "from GSTR3B_ASP_COMPUTE"
				+ " where GSTIN = :gstin AND TAX_PERIOD = :taxPeriod AND"
				+ " IS_ACTIVE = TRUE";
		return query;
	}

	private String createUserInputQueryString(String gstn, String taxPeriod) {
		String query = "select CESS ,CGST,IGST,SGST,TAXABLE_VALUE,SECTION_NAME,"
				+ "SUBSECTION_NAME, POS,INTERSTATE,INTRA_STATE,UI_ROW_NAME,ALL_OTHER_ITC_FLAG "
				+ "from GSTR3B_ASP_USER where GSTIN =:gstin "
				+ "AND TAX_PERIOD =:taxPeriod AND IS_ACTIVE =TRUE AND "
				+ "IS_ITC_ACTIVE=TRUE";
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
		obj.setRowName((String) arr[10]);
		//obj.setRadioFlag((Boolean) arr[11]);
		return obj;
	}

	private Gstr3BGstinAspUserInputDto convertUserInputForSavePst(
			Object[] arr) {

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
		obj.setSectionName((String) arr[4]);
		obj.setSubSectionName((String) arr[5]);
		obj.setRowName((String) arr[6]);
		obj.setUserRetPeriod((String) arr[7]);
		return obj;
	}

	@Override
	public List<Gstr3BGstinAspUserInputDto> getUserInputDtoBySection(
			String taxPeriod, String gstin, List<String> sections) {
		try {
			String queryString = "select CESS ,CGST,IGST,SGST,TAXABLE_VALUE,"
					+ "SECTION_NAME,"
					+ "SUBSECTION_NAME,POS,INTERSTATE,INTRA_STATE,UI_ROW_NAME "
					+ " from " + "GSTR3B_ASP_USER where GSTIN = :gstin AND "
					+ "TAX_PERIOD = :taxPeriod AND"
					+ " IS_ACTIVE = TRUE AND SECTION_NAME IN (:sections)";
			// return getInputHardcodedData();
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstin", gstin);
			q.setParameter("taxPeriod", taxPeriod);
			q.setParameter("sections", sections);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from user Input "
						+ "table for given gstin " + gstin + " and taxPeriod"
						+ taxPeriod + "and query = " + queryString + "");
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<Gstr3BGstinAspUserInputDto> retList = list.stream()
					.map(o -> convertUserInput(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3BGstinDashboardDaoImpl.getgstinuserInputDtoList");

		}
	}

	@Override
	public List<Gstr3BGstinAspUserInputDto> getUserInputDtoforSavePst(
			String taxPeriod, String gstin, List<String> sections) {
		try {
			String queryString = "select CESS,CGST,IGST,SGST,SECTION_NAME,"
					+ "SUBSECTION_NAME,UI_ROW_NAME,USER_RETPERIOD " + " from "
					+ "GSTR3B_ASP_USER where GSTIN = :gstin AND "
					+ "TAX_PERIOD = :taxPeriod AND"
					+ " IS_ACTIVE = TRUE AND SECTION_NAME IN (:sections)";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstin", gstin);
			q.setParameter("taxPeriod", taxPeriod);
			q.setParameter("sections", sections);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from user Input "
						+ "table for given gstin " + gstin + " and taxPeriod"
						+ taxPeriod + "and query = " + queryString + "");
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<Gstr3BGstinAspUserInputDto> retList = list.stream()
					.map(o -> convertUserInputForSavePst(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3BGstinDashboardDaoImpl.getgstinuserInputDtoList");

		}
	}

	private String userInputQuery(List<String> gstins, String taxPeriod) {
		String query = "select GSTIN, CESS ,CGST,IGST,SGST, "
				+ "SECTION_NAME, SUBSECTION_NAME, POS,INTERSTATE,INTRA_STATE, "
				+ "TAXABLE_VALUE, TAX_PERIOD from GSTR3B_ASP_USER where "
				+ "GSTIN IN (:gstins) AND IS_ACTIVE = TRUE";
		if (!Strings.isNullOrEmpty(taxPeriod)) {
			query = query + "" + " AND TAX_PERIOD = :taxPeriod";
		}

		return query;
	}

	private Gstr3BSummaryDto converInput(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to Gstr3BSummaryDto object";
			LOGGER.debug(str);
		}
		Gstr3BSummaryDto obj = new Gstr3BSummaryDto();
		obj.setGstin((String) arr[0]);
		obj.setCess((BigDecimal) arr[1]);
		obj.setCgst((BigDecimal) arr[2]);
		obj.setIgst((BigDecimal) arr[3]);
		obj.setSgst((BigDecimal) arr[4]);
		obj.setSectionName((String) arr[5]);
		obj.setSubSectionName((String) arr[6]);
		obj.setPos((String) arr[7]);
		obj.setInterState((BigDecimal) arr[8]);
		obj.setIntraState((BigDecimal) arr[9]);
		obj.setTaxableVal((BigDecimal) arr[10]);
		obj.setTaxPeriod((String) arr[11]);
		return obj;
	}

	@Override
	public List<Gstr3BSummaryDto> getAllGstinUserInputDtoList(
			List<String> gstins, String taxPeriod) throws AppException {
		try {
			String queryString = userInputQuery(gstins, taxPeriod);
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstins", gstins);
			if (taxPeriod != null && !taxPeriod.isEmpty()) {
				q.setParameter("taxPeriod", taxPeriod);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from user Input "
						+ "table for given gstin :" + gstins + ", taxPeriod : "
						+ taxPeriod + "and query = " + queryString + "");
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<Gstr3BSummaryDto> retList = list.stream()
					.map(o -> converInput(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3BGstinDashboardDaoImpl.getAllGstinUserInputDtoList");
		}
	}

	@Override
	public List<Gstr3BGstinAspUserInputDto> getGstnDtoList(String gstin,
			String taxPeriod) throws AppException {
		try {
			String queryString = createGstnQueryString(gstin, taxPeriod);
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstin", gstin);
			q.setParameter("taxPeriod", taxPeriod);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from GSTN "
						+ "table for given gstin " + gstin + " and taxPeriod"
						+ taxPeriod + "and query = " + queryString + "");
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<Gstr3BGstinAspUserInputDto> retList = list.stream()
					.map(o -> convertUserInput(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3BGstinDashboardDaoImpl.getGstnDtoList");
		}
	}

	private String createGstnQueryString(String gstn, String taxPeriod) {
		String query = "select CESS ,CGST,IGST,SGST,TAXABLE_VALUE,SECTION_NAME,"
				+ "SUBSECTION_NAME, POS,INTERSTATE,INTRA_STATE, NULL "
				+ "from GSTR3B_GSTN where GSTIN = :gstin "
				+ "AND TAX_PERIOD = :taxPeriod AND IS_ACTIVE = TRUE";
		return query;
	}

	@Override
	public List<Gstr3BGstinAspUserInputDto> getComputeDtoBySection(
			String taxPeriod, String gstin, List<String> sections) {
		try {
			String queryString = "select CESS ,CGST,IGST,SGST,TAXABLE_VALUE,"
					+ "SECTION_NAME,"
					+ "SUBSECTION_NAME ,POS,INTERSTATE,INTRA_STATE,UI_ROW_NAME"
					+ "  from " + "GSTR3B_ASP_COMPUTE where GSTIN = :gstin AND "
					+ "TAX_PERIOD = :taxPeriod AND"
					+ " IS_ACTIVE = TRUE AND SECTION_NAME IN (:sections)";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstin", gstin);
			q.setParameter("taxPeriod", taxPeriod);
			q.setParameter("sections", sections);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from Compute "
						+ "table for given gstin " + gstin + " and taxPeriod"
						+ taxPeriod + "and query = " + queryString + "");
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<Gstr3BGstinAspUserInputDto> retList = list.stream()
					.map(o -> convertUserInput(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3BGstinDashboardDaoImpl.getComputeDtoBySection");

		}
	}

	@Override
	public List<Gstr3BGstinAspUserInputDto> getGstinAutoCalDtoList(String gstin,
			String taxPeriod) throws AppException {
		try {
			String queryString = createAutoCalQueryString(gstin, taxPeriod);
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstin", gstin);
			q.setParameter("taxPeriod", taxPeriod);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from "
						+ "GSTR3B_AUTO_CALC table for given gstin " + gstin
						+ " and taxPeriod " + taxPeriod + " and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<Gstr3BGstinAspUserInputDto> retList = list.stream()
					.map(o -> convertUserInput(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3BGstinDashboardDaoImpl.getGstinAutoCalDtoList()");
		}
	}

	private String createAutoCalQueryString(String gstin, String taxPeriod) {
		String query = "select CESS ,CGST,IGST,SGST,TAXABLE_VALUE,SECTION_NAME,"
				+ "SUBSECTION_NAME, POS,INTERSTATE,INTRA_STATE,NULL "
				+ "from GSTR3B_AUTO_CALC where GSTIN = :gstin "
				+ "AND TAX_PERIOD = :taxPeriod AND IS_ACTIVE = TRUE";
		return query;
	}

	@Override
	public List<Gstr3BGstinAspUserInputDto> getAutoCalDtoBySection(
			String taxPeriod, String gstin, List<String> sections) {
		try {
			String queryString = "select CESS ,CGST,IGST,SGST,TAXABLE_VALUE,"
					+ "SECTION_NAME,SUBSECTION_NAME ,POS,INTERSTATE, "
					+ "INTRA_STATE, NULL from GSTR3B_AUTO_CALC where GSTIN = :gstin"
					+ " AND TAX_PERIOD = :taxPeriod AND "
					+ " IS_ACTIVE = TRUE AND SECTION_NAME IN (:sections)";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("gstin", gstin);
			q.setParameter("taxPeriod", taxPeriod);
			q.setParameter("sections", sections);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from "
						+ "GSTR3B_AUTO_CALC " + "table for given gstin " + gstin
						+ " and taxPeriod" + taxPeriod + "and query = "
						+ queryString + "");
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<Gstr3BGstinAspUserInputDto> retList = list.stream()
					.map(o -> convertUserInput(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3BGstinDashboardDaoImpl.getAutoCalDtoBySection()");

		}

	}
	
	private Gstr3BGstinAspUserInputDto convertUserInputNew(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting  generic object to"
					+ " Gstr3BGstinAspUserInputDtoNEW object";
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
		obj.setRowName((String) arr[10]);
		obj.setRadioFlag((Boolean) arr[11]);
		return obj;
	}
}
