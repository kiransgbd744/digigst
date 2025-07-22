package com.ey.advisory.app.services.daos.listgstinforrecon;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Anx2GetProcessedResponseDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2ListGSTINForReconReq;
import com.ey.advisory.app.docs.dto.anx2.Anx2ListGSTINForReconResp;
import com.ey.advisory.app.services.daos.get2a.AnxGet2aprocessedDao;

@Service("Anx2ListGstinsForReconServiceImpl")
public class Anx2ListGstinsForReconServiceImpl
		implements Anx2ListGstinsForReconService {
	

	@Autowired
	@Qualifier("Anx2ListGstinsForReconDaoImpl")
	Anx2ListGstinsForReconDao anx2ListGstinsForReconDao;


	@Override
	public List<Object> getAnx2ListGstinsForRecon(
			Anx2ListGSTINForReconReq dto) {
		
		List<Object> recResponse =
				anx2ListGstinsForReconDao.getAnx2ListGstinsForRecondao(dto);
				
				return recResponse;
	}
}
