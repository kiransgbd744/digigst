package com.ey.advisory.bcadmin.common.dao;

import java.util.List;

import com.ey.advisory.bcadmin.common.dto.ERPRequestLogEntitydto;

public interface GetNicPayloadsDAO {

	public List<ERPRequestLogEntitydto> getPayloads(String docNo, String sgstin,
			String req_url, String job_url);

}
