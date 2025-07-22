package com.ey.advisory.app.gstr2b.summary;

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

@Slf4j
@Component("Gstr2BDetailsDaoImpl")
public class Gstr2BDetailsDaoImpl implements Gstr2BDetailsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr2BDetailsDto> getDetailsResp(List<String> gstins,
			String toTaxPeriod, String fromTaxPeriod) {

		List<Gstr2BDetailsDto> resp = new ArrayList<>();

		try {
			String msg = String.format(
					"Inside Gstr2BDetailsDaoImpl"
							+ ".getDetailsResp() method {} gstins %s, toTaxPeriod %s "
							+ "fromTaxPeriod %s ",
					gstins, toTaxPeriod, fromTaxPeriod);
			LOGGER.debug(msg);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_GETGSTR2B_TBLSMRY_RPT");

			storedProc.registerStoredProcedureParameter("P_gstin_LIST",
					String.class, ParameterMode.IN);

			storedProc.setParameter("P_gstin_LIST", String.join(",", gstins));

			storedProc.registerStoredProcedureParameter("FROM_RET_PERIOD",
					String.class, ParameterMode.IN);

			storedProc.setParameter("FROM_RET_PERIOD", fromTaxPeriod);

			storedProc.registerStoredProcedureParameter("TO_RET_PERIOD",
					String.class, ParameterMode.IN);

			storedProc.setParameter("TO_RET_PERIOD", toTaxPeriod);

			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();

			LOGGER.debug("Converting Query And converting to List BEGIN");
			resp = list.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Converting Query And converting to List END");
			return resp;

		} catch (Exception e) {
			String msg = String.format(
					"Error while calling store proc "
							+ "USP_GETGSTR2B_TBLSMRY_RPT :: gstins %s, toTaxPeriod %s "
							+ "fromTaxPeriod %s ",
					gstins, toTaxPeriod, fromTaxPeriod);
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

	}

	private Gstr2BDetailsDto convert(Object[] o) {

		Gstr2BDetailsDto dto = new Gstr2BDetailsDto();

		BigDecimal zero = BigDecimal.ZERO;

		dto.setRgstin((String) o[0]);
		dto.setTableName((String) o[1]);
		
		dto.setTotalTaxIgst(o[2] != null ? (BigDecimal) o[2] : zero);
		dto.setTotalTaxCgst(o[3] != null ? (BigDecimal) o[3] : zero);
		dto.setTotalTaxSgst(o[4] != null ? (BigDecimal) o[4] : zero);
		dto.setTotalTaxCess(o[5] != null ? (BigDecimal) o[5] : zero);
		
		dto.setAvailItcIgst(o[6] != null ? (BigDecimal) o[6] : zero);
		dto.setAvailItcCgst(o[7] != null ? (BigDecimal) o[7] : zero);
		dto.setAvailItcSgst(o[8] != null ? (BigDecimal) o[8] : zero);
		dto.setAvailItcCess(o[9] != null ? (BigDecimal) o[9] : zero);

		dto.setNonAvailItcIgst(o[10] != null ? (BigDecimal) o[10] : zero);
		dto.setNonAvailItcCgst(o[11] != null ? (BigDecimal) o[11] : zero);
		dto.setNonAvailItcSgst(o[12] != null ? (BigDecimal) o[12] : zero);
		dto.setNonAvailItcCess(o[13] != null ? (BigDecimal) o[13] : zero);
		
		dto.setRejectedItcIgst(o[14] != null ? (BigDecimal) o[14] : zero);
		dto.setRejectedItcCgst(o[15] != null ? (BigDecimal) o[15] : zero);
		dto.setRejectedItcSgst(o[16] != null ? (BigDecimal) o[16] : zero);
		dto.setRejectedItcCess(o[17] != null ? (BigDecimal) o[17] : zero);
		
		return dto;
	}

}
