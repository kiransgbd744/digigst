package com.ey.advisory.app.daos.manageauthtoken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.docs.dto.ManageAuthTokenDetDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;

import lombok.extern.slf4j.Slf4j;

@Repository("authTokenDetailDaoImpl")
@Slf4j
public class AuthTokenDetailDaoImpl implements AuthTokenDetailDao {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	private CommonUtility commonUtility;

	@Override
	public List<ManageAuthTokenDetDto> findAuthDetailsForGstins(
			List<String> gstins) {
		List<ManageAuthTokenDetDto> detailResponse = new ArrayList<ManageAuthTokenDetDto>();
		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Finding Auth Details in Dao Layer" + "on Gstins : %s",
					gstins);
			LOGGER.debug(str);
		}

		List<GSTNDetailEntity> gstnDetail = gstnDetailRepository
				.findByGstinInAndIsDeleteFalse(gstins);

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Finding Auth Details in Dao Layer" + "on gstnDetail : %s",
					gstnDetail);
			LOGGER.debug(str);
		}

		if (gstnDetail == null || gstnDetail.isEmpty()) {
			return detailResponse;
		}
		Pair<Map<String, String>, Map<String, String>> gstnRegMap = commonUtility
				.getGstnRegMap();
		Map<String, String> gstinAuthMap = gstnRegMap.getValue0();

		gstnDetail.forEach(gst -> {

			ManageAuthTokenDetDto obj = new ManageAuthTokenDetDto();

			Long entityId = gst.getEntityId();
			String entityName = entityInfoRepo
					.findEntityNameByEntityId(entityId);
			obj.setEntityName(entityName);
			obj.setGstin(gst.getGstin());
			obj.setEmail(gst.getRegisteredEmail());
			obj.setMobileNo(gst.getRegisteredMobileNo());
			if (!gstinAuthMap.isEmpty()) {
				String gstnAct = gstinAuthMap.get(gst.getGstin());
				 
				 
				/*
				 * String status = authTokenService
				 * .getAuthTokenStatusForGstin(gst.getGstin());
				 */
				if (gstnAct.equals("A")) {
					obj.setStatus(gstnAct);
					obj.setAction(true);
				} else {
					obj.setStatus(gstnAct);
					obj.setAction(false);
				}
			}
			detailResponse.add(obj);

		});
		return detailResponse;
	}

}
