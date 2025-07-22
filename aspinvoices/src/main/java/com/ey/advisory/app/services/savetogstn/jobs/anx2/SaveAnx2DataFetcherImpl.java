/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("SaveAnx2DataFetcherImpl")
public class SaveAnx2DataFetcherImpl implements SaveAnx2DataFetcher {

	private static final String LOG_GROUPCODE_MSG = "groupCode {} is set";

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	
	@Override
	public List<Object[]> findCancelledData(String gstin, String retPeriod,
			String groupCode, String docType) {

		/*JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr1SaveToGstnReqDto dto = gson.fromJson(requestObject,
				Gstr1SaveToGstnReqDto.class);
		List<List<Object[]>> repEntity = null;
		try {
			Pair<List<String>, List<String>> pairs = screenExtractor
					.getGstr1CombinationPairs(dto, groupCode);
			if (pairs != null && pairs.getValue0() != null
					&& !pairs.getValue0().isEmpty() && pairs.getValue1() != null
					&& !pairs.getValue1().isEmpty()) {
				repEntity = new ArrayList<>();
				List<String> value0 = pairs.getValue0();
				List<String> value1 = pairs.getValue1();
				if (value0 != null && !value0.isEmpty() && value1 != null
						&& !value1.isEmpty()) {
					TenantContext.setTenantId(groupCode);
					LOGGER.info("groupCode {} is set", groupCode);
					for (int i = 0, j = 0; i < value0.size()
							&& j < value1.size(); i++, j++) {
						List<Object[]> result = docRepository
								.findAnx2CancelledData(value0.get(i),
										value1.get(j), docType);
						if (result != null && !result.isEmpty()) {
							repEntity.add(result);
						}
					}
				}
			}
			if (repEntity != null) {
				LOGGER.info("{} {} docs are eligible to do Save To Gstn.",
						repEntity.size(), docType);
			}
		} catch (Exception ex) {
			throw new AppException("Error in fetching the gstn save Data", ex);

		} finally {
			TenantContext.clearTenant();
		}
		return repEntity;*/
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		return docRepository.findAnx2CancelledData(gstin, retPeriod, docType);
	}
	
	
	
	@Override
	public List<Object[]> findInvoiceLevelData(String gstin, String retPeriod,
			String groupCode, String docType, List<Long> docIds) {

		/*JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
//		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
//		SaveToGstnReqDto dto = gson.fromJson(reqObject, SaveToGstnReqDto.class);
		Gstr1SaveToGstnReqDto dto = gson.fromJson(requestObject, Gstr1SaveToGstnReqDto.class);
		List<List<Object[]>> repEntity = null;
		try {
			*//**
			 * This method is used to handle Gstr1 SaveToGstn Operation 
			 * from different screens(different input criteria), using with the 
			 * screen given criteria we can fetch the reurnPeriod and gstin 
			 * because SaveToGstn operation is meant at return period and gstin 
			 * level.
			**//*
			Pair<List<String>, List<String>> pairs = screenExtractor.
					getGstr1CombinationPairs(dto, groupCode);
			
			if (pairs != null && pairs.getValue0() != null
					&& !pairs.getValue0().isEmpty() && pairs.getValue1() != null
					&& !pairs.getValue1().isEmpty()) {
				repEntity = new ArrayList<>();
				List<String> value0 = pairs.getValue0();
				List<String> value1 = pairs.getValue1();
				if (value0 != null && !value0.isEmpty() && value1 != null
						&& !value1.isEmpty()) {
					TenantContext.setTenantId(groupCode);
					LOGGER.info("groupCode {} is set", groupCode);
					for (int i = 0, j = 0; i < value0.size()
							&& j < value1.size(); i++, j++) {
						List<Object[]> result = docRepository
								.findAnx2InvLevelData(value0.get(i),
										value1.get(j), docType, docIds);
						if (result != null && !result.isEmpty()) {
							repEntity.add(result);
						}
					}
				}
			}	
			if (repEntity != null) {
				LOGGER.info("{} {} docs are eligible to do Save To Gstn.",
						repEntity.size(), docType);
			}
		} catch (Exception ex) {
			throw new AppException("Error in fetching the gstn save Data", ex);

		} finally {
			TenantContext.clearTenant();
		}
		return repEntity;*/
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		// Assuming this procedure will insert new rows into tables
		docRepository.saveAnx2ProcCall(gstin, docType,
				GenUtil.convertTaxPeriodToInt(retPeriod));
		return docRepository.findAnx2InvLevelData(gstin, retPeriod, docType, docIds);
	}


}
