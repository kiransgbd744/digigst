package com.ey.advisory.app.services.manageauthtoken;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.daos.manageauthtoken.AuthTokenDetailDao;
import com.ey.advisory.app.docs.dto.ManageAuthTokenDetDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("authTokenDetailServiceImpl")
public class AuthTokenDetailServiceImpl implements AuthTokenDetailService {

	@Autowired
	@Qualifier("authTokenDetailDaoImpl")
	AuthTokenDetailDao authTokenDaoImp;

	@Override
	public List<ManageAuthTokenDetDto> getAuthDetailsForGstins(
			List<String> gstins) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Calling Dao Layer " + "from AuthTokenDetailServiceImpl "
							+ "Service Class for following gstins %s",
					gstins);
			LOGGER.debug(msg);
		}
		return authTokenDaoImp.findAuthDetailsForGstins(gstins);
	}
}
