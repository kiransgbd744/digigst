package com.ey.advisory.app.gstr2b;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2BRegenerationDashBoardDaoImpl")
public class Gstr2BRegenerationDashBoardDaoImpl implements Gstr2BRegenerationDashBoardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr2BDashBoardErrorDto> getErrorCodeforGetCall(List<String> gstins, int derivedStartPeriod,
			int derivedEndPeriod) {

		List<Gstr2BDashBoardErrorDto> resp = null;

		try {
			String querystr = querySting(gstins, derivedStartPeriod, derivedEndPeriod);

			Query q = entityManager.createNativeQuery(querystr);
			q.setParameter("gstins", gstins);
			q.setParameter("derivedStartPeriod", derivedStartPeriod);
			q.setParameter("derivedEndPeriod", derivedEndPeriod);

			@SuppressWarnings("unchecked")
			List<Object[]> records = q.getResultList();

			resp = records.stream().map(o -> convert(o)).collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format(
					"error occure in Gstr2BDashBoardDaoImpl" + ".getErrorCodeforGetCall() method {} gstins %s, "
							+ "derivedStartPeriod %d,  derivedEndPeriod %d ",
					gstins, derivedStartPeriod, derivedEndPeriod);
			LOGGER.error(msg, ex);
			ex.printStackTrace();
			throw new AppException(ex);
		}
		return resp;
	}

	private String querySting(List<String> gstins, int derivedStartPeriod, int derivedEndPeriod) {

		return "select A.ERROR_CODE, A.ERROR_DESC, A.GSTIN, A.RETURN_PERIOD, " + "A.STATUS, A.CREATED_ON "
				+ "from GETANX1_BATCH_TABLE A " + "WHERE A.API_SECTION = 'GSTR2B_REGENERATE' "
				+ "AND A.IS_DELETE = FALSE " + "AND A.GSTIN IN (:gstins) "
				+ "AND CONCAT(RIGHT(A.RETURN_PERIOD, 4), LEFT(A.RETURN_PERIOD, 2)) "
				+ "BETWEEN :derivedStartPeriod AND :derivedEndPeriod";

	}

	private Gstr2BDashBoardErrorDto convert(Object[] arr) {

		Gstr2BDashBoardErrorDto obj = new Gstr2BDashBoardErrorDto();
		obj.setErrorCode((String) arr[0]);
		obj.setErrorMsg((String) arr[1]);
		obj.setGstin((String) arr[2]);
		obj.setStatus((String) arr[4]);
		obj.setTaxPeriod((String) arr[3]);
		if (arr[5] != null) {
			Timestamp date = (Timestamp) arr[5];
			obj.setCreatedOn(EYDateUtil.toISTDateTimeFromUTC(date.toLocalDateTime()));
		}
		return obj;

	}

}
