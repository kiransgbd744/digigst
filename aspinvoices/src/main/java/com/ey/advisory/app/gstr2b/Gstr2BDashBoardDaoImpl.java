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

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr2BDashBoardDaoImpl")
public class Gstr2BDashBoardDaoImpl implements Gstr2BDashBoardDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr2BDashBoardErrorDto> getErrorCodeforGetCall(
			List<String> gstins, int derivedStartPeriod, int derivedEndPeriod) {
		
		List<Gstr2BDashBoardErrorDto> resp = null;

		try{
		String querystr = querySting(gstins, derivedStartPeriod,
				derivedEndPeriod);

		Query q = entityManager.createNativeQuery(querystr);
		q.setParameter("gstins", gstins);
		q.setParameter("derivedStartPeriod", derivedStartPeriod);
		q.setParameter("derivedEndPeriod", derivedEndPeriod);

		@SuppressWarnings("unchecked")
		List<Object[]> records = q.getResultList();

		resp = records.stream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		}catch(Exception ex){
			String msg = String.format("error occure in Gstr2BDashBoardDaoImpl"
					+ ".getErrorCodeforGetCall() method {} gstins %s, "
					+ "derivedStartPeriod %d,  derivedEndPeriod %d ", 
					gstins, derivedStartPeriod, derivedEndPeriod);
			LOGGER.error(msg,ex);
			ex.printStackTrace();
			throw new AppException(ex);
		}
		return resp;
	}

	private String querySting(List<String> gstins, int derivedStartPeriod,
			int derivedEndPeriod) {

		return "select A.ERROR_CODE,A.ERROR_DESC,A.GSTIN,A.RETURN_PERIOD,"
				+ "A.STATUS,G.UPDATED_ON,G.CSV_FILE_PATH, G.DOC_ID "
				+ "from GETANX1_BATCH_TABLE A "
				+ "INNER JOIN GSTN_GET_STATUS G "
				+ "ON A.GSTIN=G.GSTIN AND A.RETURN_PERIOD=G.TAX_PERIOD AND "
				+ " G.RETURN_TYPE = 'GSTR2B' AND GET_TYPE = 'GSTR2B_GET_ALL' "
				+ "  AND A.IS_DELETE = FALSE "
				+ " WHERE A.GSTIN IN(:gstins) AND "
				+ " CONCAT(RIGHT(Return_Period,4),LEFT(Return_Period,2)) "
				+ " between :derivedStartPeriod AND :derivedEndPeriod";

	}

	private Gstr2BDashBoardErrorDto convert(Object[] arr) {

		Gstr2BDashBoardErrorDto obj = new Gstr2BDashBoardErrorDto();
		obj.setErrorCode((String) arr[0]);
		obj.setErrorMsg((String) arr[1]);
		obj.setGstin((String) arr[2]);
		obj.setStatus((String) arr[4]);
		obj.setTaxPeriod((String) arr[3]);
		if(arr[5] != null){
		Timestamp date = (Timestamp) arr[5];
		obj.setCreatedOn(EYDateUtil.toISTDateTimeFromUTC(date
				.toLocalDateTime()));
		}
		obj.setFilePath((String) arr[6]);
		obj.setDocId((String) arr[7]);
		return obj;

	}

}
