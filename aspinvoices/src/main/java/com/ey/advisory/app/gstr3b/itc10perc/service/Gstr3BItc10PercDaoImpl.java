package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Component("Gstr3BItc10PercDaoImpl")
@Slf4j
public class Gstr3BItc10PercDaoImpl implements Gstr3BItc10PercDao {
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr3BITCInnerDto> getDbResp(String gstin, String taxPeriod) {
	
	try {

		LOGGER.debug("Inside Gstr3BItc10PercDaoImpl.getDbResp():: "
				+ "Invoking USP_ITC_PERM_3B_SCR Stored Proc");

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery("USP_ITC_PERM_3B_SCR");

		storedProc.registerStoredProcedureParameter(
				"RGSTIN", String.class, ParameterMode.IN);

		storedProc.setParameter("RGSTIN", gstin);

		storedProc.registerStoredProcedureParameter("RSPTAXPERIOD3B", 
				String.class, ParameterMode.IN);

		storedProc.setParameter("RSPTAXPERIOD3B", taxPeriod);

		@SuppressWarnings("unchecked")
		List<Object[]> list = storedProc.getResultList();

		LOGGER.debug("converting dto to List of Gstr3BITCInnerDto BEGIN");
		List<Gstr3BITCInnerDto> retList = list.stream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		LOGGER.debug("Converting Query And converting to List END");

		return retList;

	} catch (Exception e) {
		String msg = String.format(
				"Error while calling store proc "
						+ "USP_ITC_PERM_3B_SCR :: gstin %s , TaxPeriod %s",
				gstin, taxPeriod);
		LOGGER.error(msg,e);
		throw new AppException(e);
	}
}

	private Gstr3BITCInnerDto convert(Object[] o) {
		Gstr3BITCInnerDto dto = new Gstr3BITCInnerDto();

		BigDecimal zeroVal = BigDecimal.ZERO;
		
		dto.setIgst((BigDecimal) o[1] != null ? (BigDecimal) o[1] : zeroVal);
		dto.setCgst((BigDecimal) o[2] != null ? (BigDecimal) o[2] : zeroVal);
		dto.setSgst((BigDecimal) o[3] != null ? (BigDecimal) o[3] : zeroVal);
		dto.setCess((BigDecimal) o[4] != null ? (BigDecimal) o[4] : zeroVal);

		String type = ((String) o[0]);

		if (type.equalsIgnoreCase("TAX_PR")) {
			dto.setSectionName("4(a)(5)(a)");
			dto.setSubSectionName("APTPPR");

		} else if (type.equalsIgnoreCase("TAX_3B")) {
			dto.setSectionName("4(a)(5)(b)");
			dto.setSubSectionName("APTG3BR");

		} else if (type.equalsIgnoreCase("MAX_ITC")) {
			dto.setSectionName("4(a)(5)(c)");
			dto.setSubSectionName("APMPITCS36");

		} else if (type.equalsIgnoreCase("ITC_REV")) {
			dto.setSectionName("4(a)(5)(d)");
			dto.setSubSectionName("ITCRF");
		}
		return dto;
	}
}
