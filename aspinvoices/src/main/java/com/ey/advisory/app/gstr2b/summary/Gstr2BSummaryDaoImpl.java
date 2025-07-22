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
@Component("Gstr2BSummaryDaoImpl")
public class Gstr2BSummaryDaoImpl implements Gstr2BSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr2BSummaryDto> getSummaryResp(List<String> gstins,
			String toTaxPeriod, String fromTaxPeriod) {

		List<Gstr2BSummaryDto> resp = new ArrayList<>();

		try {
			String msg = String.format(
					"Inside Gstr2BSummaryDaoImpl"
							+ ".getSummaryResp() method {} gstins %s, toTaxPeriod %s "
							+ " fromTaxPeriod %s",
					gstins, toTaxPeriod, fromTaxPeriod);
			LOGGER.debug(msg);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_GETGSTR2B_ENTSMRY_RPT");

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);

			storedProc.setParameter("P_GSTIN_LIST", String.join(",", gstins));

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
					"Error while calling store proc USP_GETGSTR2B_ENTSMRY_RPT "
							+ ":: gstins %s, toTaxPeriod %s fromTaxPeriod %s ",
					gstins, toTaxPeriod, fromTaxPeriod);
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

	}

	private Gstr2BSummaryDto convert(Object[] o) {

		Gstr2BSummaryDto dto = new Gstr2BSummaryDto();

		BigDecimal zero = BigDecimal.ZERO;

		dto.setGstin((String) o[0]);
		dto.setGetGstr2bStatus(o[1] != null ? (String) o[1].toString() : "");

		dto.setVendorGstinCount(o[2] != null ? (Integer) o[2] : 0);

		dto.setCount(o[3] != null ? (Integer) o[3] : 0);

		dto.setTotalTaxIgst(o[4] != null ? (BigDecimal) o[4] : zero);
		dto.setTotalTaxCgst(o[5] != null ? (BigDecimal) o[5] : zero);
		dto.setTotalTaxSgst(o[6] != null ? (BigDecimal) o[6] : zero);
		dto.setTotalTaxCess(o[7] != null ? (BigDecimal) o[7] : zero);

		dto.setAvailItcIgst(o[8] != null ? (BigDecimal) o[8] : zero);
		dto.setAvailItcCgst(o[9] != null ? (BigDecimal) o[9] : zero);
		dto.setAvailItcSgst(o[10] != null ? (BigDecimal) o[10] : zero);
		dto.setAvailItcCess(o[11] != null ? (BigDecimal) o[11] : zero);

		dto.setNonAvailItcIgst(o[12] != null ? (BigDecimal) o[12] : zero);
		dto.setNonAvailItcCgst(o[13] != null ? (BigDecimal) o[13] : zero);
		dto.setNonAvailItcSgst(o[14] != null ? (BigDecimal) o[14] : zero);
		dto.setNonAvailItcCess(o[15] != null ? (BigDecimal) o[15] : zero);
		
		dto.setRejectedItcIgst(o[16] != null ? (BigDecimal) o[16] : zero);
		dto.setRejectedItcCgst(o[17] != null ? (BigDecimal) o[17] : zero);
		dto.setRejectedItcSgst(o[18] != null ? (BigDecimal) o[18] : zero);
		dto.setRejectedItcCess(o[19] != null ? (BigDecimal) o[19] : zero);

		dto.setStatus(
				o[20] != null ? (String) o[20].toString() : "not initiated");
		dto.setVendorPanCount(o[21] != null ? (Integer) o[21] : 0);

		return dto;
	}

}
