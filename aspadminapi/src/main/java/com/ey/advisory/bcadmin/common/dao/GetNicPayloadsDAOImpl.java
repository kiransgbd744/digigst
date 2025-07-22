package com.ey.advisory.bcadmin.common.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.bcadmin.common.dto.ERPRequestLogEntitydto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("GetNicPayloadsDAOImpl")
public class GetNicPayloadsDAOImpl implements GetNicPayloadsDAO {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<ERPRequestLogEntitydto> getPayloads(String docNo, String sgstin,
			String req_url, String job_url) {
		String queryStr = createQueryString();
		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter(1, "%" + sgstin + "%" + docNo + "%");
		q.setParameter(2, "%" + docNo + "%" + sgstin + "%");
		q.setParameter(3, "%" + req_url + "%");
		q.setParameter(4, "%" + job_url + "%");
		List<Object[]> resultList = q.getResultList();
		return resultList.stream().map(o -> convert(o))
				.collect(Collectors.toList());
	}

	private ERPRequestLogEntitydto convert(Object[] o) {
		ERPRequestLogEntitydto erpRequestLogEntitydto = new ERPRequestLogEntitydto();
		erpRequestLogEntitydto.setReqPayload(String.valueOf(o[1]));
		erpRequestLogEntitydto.setNicReqPayload(String.valueOf(o[2]));
		return erpRequestLogEntitydto;
	}

	private String createQueryString() {
		String query = "SELECT  top 1 ID,to_char(REQ_BODY),to_char(NIC_REQ_PAYLOAD) FROM ERP_REQUEST_LOG where  "
				+ " (NIC_REQ_Payload like ? or NIC_REQ_Payload like ?) and (REQ_URL like ? or REQ_URL like ?) ORDER BY ID DESC;";
		return query;
	}
}
