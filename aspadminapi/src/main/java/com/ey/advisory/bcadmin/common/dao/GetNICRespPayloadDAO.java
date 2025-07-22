package com.ey.advisory.bcadmin.common.dao;

import java.util.List;

import com.ey.advisory.bcadmin.common.dto.ERPRequestLogEntitydto;

public interface GetNICRespPayloadDAO {

	public List<ERPRequestLogEntitydto> getRespPayloads(String docNo,
			String sgstin, String req_url);

}
