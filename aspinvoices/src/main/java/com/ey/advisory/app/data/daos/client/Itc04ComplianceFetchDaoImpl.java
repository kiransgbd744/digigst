package com.ey.advisory.app.data.daos.client;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedDataRecordsRespDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Itc04ComplianceFetchDaoImpl")
public class Itc04ComplianceFetchDaoImpl implements Itc04ComplianceDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public Gstr2aProcessedDataRecordsRespDto FetchRec(
			Gstr2aProcessedDataRecordsReqDto gstr2aProcessedDataRecordsReqDto) {

		return null;
	}

}
