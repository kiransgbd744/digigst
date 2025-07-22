/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ITCReversal180DaysServiceImpl")
public class ITCReversal180DaysServiceImpl
		implements ITCReversal180DaysService {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("ITCReversal180DaysDaoImpl")
	ITCReversal180DaysDao dao;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	Map<String, String> authTokenStatusMap = null;
	Map<String, String> stateNames = null;
	Map<String, String> regTypeMap = null;

	@Override
	public List<ITCReversal180DaysRespDto> getItcReversalData(
			ITCReversal180DaysReqDto reqDto) {

		String msg = String.format(
				"Inside ITCReversal180DaysServiceImpl " + "Req %s ", reqDto);
		LOGGER.debug(msg);

		List<ITCReversal180DaysRespDto> respList = new ArrayList<>();
		List<String> regType = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);

		try {

			Long entityId = null;
			List<String> gstins = reqDto.getGstins();

			try {

				if (gstins == null || gstins.isEmpty()) {
					entityId = reqDto.getEntityId();
					Map<String, List<String>> dataSecurityAttrMap = 
							new HashMap<>();
					Map<String, String> outwardSecurityAttributeMap =
							DataSecurityAttributeUtil
							.getOutwardSecurityAttributeMap();
					dataSecurityAttrMap = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(
									Arrays.asList(entityId),
									outwardSecurityAttributeMap);
					List<String> gstnsList = dataSecurityAttrMap
							.get(OnboardingConstant.GSTIN);

					gstins = gSTNDetailRepository
							.filterGstinBasedByRegType(gstnsList, regType);
				}

				stateNames = entityService.getStateNames(gstins);

				authTokenStatusMap = authTokenService
						.getAuthTokenStatusForGstins(gstins);

				List<GSTNDetailEntity> regTypeList = gSTNDetailRepository
						.findRegTypeByGstinList(gstins);

				regTypeMap = regTypeList.stream().distinct()
						.collect(Collectors.toMap(o -> o.getGstin(),
								o -> o.getRegistrationType()));

			} catch (Exception ex) {
				msg = String.format(
						"Error while fetching Gstins from entityId ", ex);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			List<ITCReversal180DaysRespDto> resp = dao
					.getItcReversalDBResp(gstins);

			List<ITCReversal180DaysRespDto> statusList = gstins.stream()
					.map(o -> setStatus(o)).collect(Collectors.toList());

			respList = statusList.stream().map(o -> setValues(o, resp))
					.collect(Collectors.toList());

			respList.sort(
					Comparator.comparing(ITCReversal180DaysRespDto::getGstin));

		} catch (Exception ex) {
			ex.printStackTrace();
			msg = String
					.format("Error Occured while calling ITCReversal180DaysDaoImpl"
							+ ".getItcReversalDBResp() %s ", ex);
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		return respList;
	}

	private ITCReversal180DaysRespDto setStatus(String gstin) {

		ITCReversal180DaysRespDto dto = new ITCReversal180DaysRespDto();

		dto.setAuthtoken(authTokenStatusMap.get(gstin));
		dto.setState(stateNames.get(gstin));
		dto.setRegType(regTypeMap.get(gstin));
		dto.setGstin(gstin);

		return dto;

	}

	private ITCReversal180DaysRespDto setValues(ITCReversal180DaysRespDto o,
			List<ITCReversal180DaysRespDto> respList) {

		ITCReversal180DaysRespDto dto = new ITCReversal180DaysRespDto();

		dto.setGstin(o.getGstin());
		dto.setRegType(o.getRegType());
		dto.setAuthtoken(o.getAuthtoken());
		dto.setState(o.getState());
		dto.setStatusDesc("Not Initiated");
		dto.setCount(0 + "");

		if (respList != null && !respList.isEmpty()) {
			for (ITCReversal180DaysRespDto resp : respList) {

				if (resp.getGstin() != null) {

					if (resp.getGstin().equalsIgnoreCase(o.getGstin())) {

						dto.setCess(resp.getCess());
						dto.setCgst(resp.getCgst());
						dto.setIgst(resp.getIgst());
						dto.setSgst(resp.getSgst());
						dto.setTotalTax(resp.getTotalTax());
						dto.setCount(resp.getCount());

						dto.setGstin(resp.getGstin());
						if (resp.getStatus() != null) {
							dto.setStatus(removingMilliSec(resp.getStatus()));
						}
						dto.setStatusDesc(resp.getStatusDesc());
					}
				}
			}
		}

		return dto;
	}

	private String removingMilliSec(String status) {

		String[] statusArray = status.split("T");
		String time = statusArray[1].substring(0, 8);

		status = statusArray[0] + " " + time;

		return status;
	}

}
