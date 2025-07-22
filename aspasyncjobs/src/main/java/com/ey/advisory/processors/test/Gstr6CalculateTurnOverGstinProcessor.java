package com.ey.advisory.processors.test;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr6StatusRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CalculateTurnOverRequestDto;
import com.ey.advisory.app.services.daos.gstr6.Gstr6CalculateTurnOverGstnService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Akhilesh
 *
 */

@Slf4j
@Service("Gstr6CalculateTurnOverGstinProcessor")
public class Gstr6CalculateTurnOverGstinProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr6CalculateTurnOverGstnServiceImpl")
	private Gstr6CalculateTurnOverGstnService gstr6CalTurnOverGstnService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr6StatusRepository")
	private Gstr6StatusRepository gstr6StatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		String userName = message.getUserName();
		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr6CalculateTurnOverGstinProcessor execute() method is Called! ");
			LOGGER.debug("userName : {} groupcode : {} and params : {}",
					userName, groupCode, jsonString);
		}
		Gstr6CalculateTurnOverRequestDto criteria = gson.fromJson(jsonString,
				Gstr6CalculateTurnOverRequestDto.class);
		try {

			List<String> isdGstins = criteria.getIsdGstin();
			List<String> activeGstinsList = criteria.getActiveGstinsList();
			
			List<String> taxperoidList = gstr6CalTurnOverGstnService
					.getListOfTaxperoids(criteria);
			List<Pair<String, String>> listOfPairs = gstr6CalTurnOverGstnService
					.getListOfCombinationPairs(activeGstinsList, taxperoidList);

			if (listOfPairs != null && !listOfPairs.isEmpty()) {

				gstr6CalTurnOverGstnService.updateGstnStatus(criteria,
						APIConstants.INPROGRESS);

				gstr6CalTurnOverGstnService.getGstr6CalTurnOverGstnData(
						listOfPairs, groupCode, activeGstinsList, criteria,
						isdGstins);

				String msg = "Turnover Computation for GSTN is Inprogress for the Selected Active GSTINS";
				LOGGER.info(msg);
			} else {
				LOGGER.info(
						"No Data to Compute for Turnover Computation for GSTR6");
				gstr6CalTurnOverGstnService.updateGstnStatus(criteria,
						APIConstants.FAILED);
			}
		} catch (Exception ex) {
			gstr6CalTurnOverGstnService.updateGstnStatus(criteria,
					APIConstants.FAILED);
			String msg = String.format(
					"Exception occured while Computing GSTR6 Turnover for "
							+ "Tax period %s , Entity Id %s , GSTNs %s and ISDGSTN %s .",
					criteria.getTaxPeriod(), criteria.getEntityId(),
					criteria.getGstins(), criteria.getIsdGstin());
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}
}
