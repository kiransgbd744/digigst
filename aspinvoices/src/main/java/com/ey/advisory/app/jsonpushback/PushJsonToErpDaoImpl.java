/**
 * 
 */
package com.ey.advisory.app.jsonpushback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

/**
 * @author Khalid1.Khan
 *
 */
@Component("PushJsonToErpDaoImpl")
public class PushJsonToErpDaoImpl implements PushJsonToErpDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<BatchErrorResponseDto> getErrorRecords(Long batchId,
			String gstin) {

		String queryString = createQueryString(batchId);

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("batchId", batchId);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		return list.stream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private BatchErrorResponseDto convert(Object[] arr) {

		BatchErrorResponseDto obj = new BatchErrorResponseDto();
		obj.setSuppGstin(String.valueOf(arr[0]));
		obj.setCustGstin(String.valueOf(arr[1]));
		obj.setDocNo(String.valueOf(arr[2]));
		obj.setErrorCode(String.valueOf(arr[3]));
		obj.setErrorDesc(String.valueOf(arr[4]));
		obj.setRetPeriod(String.valueOf(arr[5]));
		obj.setDocType(String.valueOf(arr[6]));
		obj.setDocDate(String.valueOf(arr[7]));
		obj.setDocKey(String.valueOf(arr[8]));
		return obj;
	}

	private String createQueryString(Long batchId) {

		return "" + "select O.SUPPLIER_GSTIN ,O.CUST_GSTIN,O.DOC_NUM ,"
				+ "E.ERROR_CODE ,E.ERROR_DESCRIPTION,O.RETURN_PERIOD,O.DOC_TYPE,O.DOC_DATE,O.DOC_KEY "
				+ "FROM  ANX_OUTWARD_DOC_HEADER O "
				+ "INNER JOIN ANX_OUTWARD_DOC_ERROR  E"
				+ " ON O.ID = E.DOC_HEADER_ID AND O.GSTN_ERROR = true "
				+ " AND E.ERROR_TYPE = 'ERR' AND O.BATCH_ID = :batchId";
	}

	@Override
	public List<BatchErrorResponseDto> getAspErrorRecords(String gstin,
			Long value0, Long value1) {
		String queryString = createAspErrorQueryString();

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("gstin", gstin);
		q.setParameter("minId", value0);
		q.setParameter("maxId", value1);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		return list.stream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private String createAspErrorQueryString() {
		return "" + "select O.SUPPLIER_GSTIN ,O.CUST_GSTIN,O.DOC_NUM ,"
				+ "E.ERROR_CODE ,E.ERROR_DESCRIPTION,O.RETURN_PERIOD,"
				+ "O.DOC_TYPE,O.DOC_DATE,O.DOC_KEY "
				+ "FROM  ANX_OUTWARD_DOC_HEADER O "
				+ "INNER JOIN ANX_OUTWARD_DOC_ERROR  E"
				+ " ON O.ID = E.DOC_HEADER_ID AND O.GSTN_ERROR = false AND "
				+ "O.SUPPLIER_GSTIN = :gstin "
				+ "AND O.ID between :minId AND :maxId";
	}

}
