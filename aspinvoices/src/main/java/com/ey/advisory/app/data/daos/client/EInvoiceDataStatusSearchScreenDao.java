package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Anx1NewDataStatusEntity;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;

public interface EInvoiceDataStatusSearchScreenDao {

	List<Anx1NewDataStatusEntity> dataAnx1NewStatusSection(String sectionType,
			DataStatusSearchReqDto req,String buildQuery,String dataType,
			Object dataRecvFrom, Object dataRecvTo);
}
