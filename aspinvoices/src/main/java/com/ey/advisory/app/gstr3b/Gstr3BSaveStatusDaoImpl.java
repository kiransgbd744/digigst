package com.ey.advisory.app.gstr3b;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Component("Gstr3BSaveStatusDaoImpl")
@Slf4j
public class Gstr3BSaveStatusDaoImpl implements Gstr3BSaveStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Gst3BSaveStatusDto> getSaveStatus(String gstin,
			String taxPeriod) {
		String queryString = CreateQuery();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Query for Save Status in GSTR3 : %s",
					queryString);
			LOGGER.debug(msg);
		}

		Query q = entityManager.createNativeQuery(queryString);
		q.setParameter("gstin", gstin);
		q.setParameter("taxPeriod", taxPeriod);

		@SuppressWarnings("unchecked")
		List<Object[]> result = q.getResultList();

		List<Gst3BSaveStatusDto> saveStatusList = result.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Result List for Save Status in GSTR3 : %s ",
					saveStatusList.toString());
			LOGGER.debug(msg);
		}

		return saveStatusList;
	}

	private Gst3BSaveStatusDto convert(Object[] obj) {

		Gst3BSaveStatusDto dto = new Gst3BSaveStatusDto();
		dto.setId(GenUtil.getBigInteger(obj[0]));
		dto.setRefId((String) obj[1]);
		dto.setStatus((String) obj[2]);
		dto.setErrorCnt(GenUtil.getBigInteger(obj[3]));
		Date date = (Timestamp) obj[4];
		dto.setAction(obj[5] != null ? String.valueOf(obj[5]) : null);

		// String ldt = date != null ? date.toString() : null;
		dto.setCreatedTime(EYDateUtil.toISTDateTimeFromUTC(date).toString());

		return dto;
	}

	private String CreateQuery() {
		String query = "SELECT ID, REF_ID, STATUS, ERROR_COUNT, CREATED_ON, API_ACTION FROM "
				+ "GSTR3B_SAVE_STATUS WHERE GSTIN = :gstin AND "
				+ "TAXPERIOD = :taxPeriod ORDER BY 1 DESC";
		return query;
	}

}
