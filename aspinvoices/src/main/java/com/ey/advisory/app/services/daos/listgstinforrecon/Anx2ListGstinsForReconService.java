package com.ey.advisory.app.services.daos.listgstinforrecon;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx2.Anx2ListGSTINForReconReq;

public interface Anx2ListGstinsForReconService {
	
	List<Object> getAnx2ListGstinsForRecon(
			Anx2ListGSTINForReconReq dto);
}
