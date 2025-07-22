package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Anx1DataStatusEntity;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;


public interface Anx1DataStatusDao {
	
	/*List<Anx1DataStatusEntity> dataAnx1StatusSection(
			String sectionType,String buildQuery, List<String> sgstins,
			List<String> profitCenter,List<String> location,List<String> plant,
			List<String> salesOrg,List<String> distrChnl,List<String> userDef1,
			List<String> userDef2,List<String> userDef3,List<String> userDef4,
			List<String> userDef5,List<String> userDef6,String dataType,
			Object dataRecvFrom, Object dataRecvTo);
*/
	List<Anx1DataStatusEntity> dataAnx1StatusSection(String sectionType,
			DataStatusSearchReqDto req,String buildQuery,String dataType,
			Object dataRecvFrom, Object dataRecvTo);


}
