/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

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
@Service("SaveRetDataFetcherImpl")
@Slf4j
public class SaveRetDataFetcherImpl implements SaveRetDataFetcher {

	private static final String LOG_GROUPCODE_MSG = "groupCode {} is set";
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

/*	@Override
	public List<Object[]> findRetInvoiceLevelData(String gstin,
			String retPeriod, String groupCode, String docType,
			List<Long> docIds) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		RetSaveToGstnReqDto dto = gson.fromJson(requestObject,
				RetSaveToGstnReqDto.class);
		List<List<Object[]>> repEntity = new ArrayList<>();
		try {
			Pair<List<String>, List<String>> pairs = screenExtractor
					.getRetCombinationPairs(dto, groupCode);
			if (pairs != null && pairs.getValue0() != null
					&& !pairs.getValue0().isEmpty() && pairs.getValue1() != null
					&& !pairs.getValue1().isEmpty()) {
				List<String> value0 = pairs.getValue0();
				List<String> value1 = pairs.getValue1();
				if (!value0.isEmpty() && !value1.isEmpty()) {
					TenantContext.setTenantId(groupCode);
					LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
					for (int i = 0, j = 0; i < value0.size()
							&& j < value1.size(); i++, j++) {
						// This needs to change according to Ret
						List<Object[]> result = docRepository
								.findRetInvLevelData(value0.get(i),
										value1.get(j), docType, docIds);
						if (!result.isEmpty()) {
							repEntity.add(result);
						}
					}
				}
			}
			if (!repEntity.isEmpty()) {
				LOGGER.info("{} {} docs are eligible to do Ret SaveToGstn.",
						repEntity.size(), docType);
			}
		} catch (Exception ex) {
			throw new AppException("Error in fetching the Ret SaveToGstn", ex);

		} finally {
			TenantContext.clearTenant();
		}
		return repEntity;
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		return docRepository.findRetInvLevelData(gstin, retPeriod, docType,
				docIds);
	}*/

	@Override
	public List<Object[]> findRetSummaryData(String gstin,
			String retPeriod, String groupCode, String docType) {

		/*JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		RetSaveToGstnReqDto dto = gson.fromJson(requestObject,
				RetSaveToGstnReqDto.class);
		List<List<Object[]>> repEntity = new ArrayList<>();
		try {
			Pair<List<String>, List<String>> pairs = screenExtractor
					.getRetCombinationPairs(dto, groupCode);
			if (pairs != null && pairs.getValue0() != null
					&& !pairs.getValue0().isEmpty() && pairs.getValue1() != null
					&& !pairs.getValue1().isEmpty()) {
				List<String> value0 = pairs.getValue0();
				List<String> value1 = pairs.getValue1();
				if (!value0.isEmpty() && !value1.isEmpty()) {
					TenantContext.setTenantId(groupCode);
					LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
					for (int i = 0, j = 0; i < value0.size()
							&& j < value1.size(); i++, j++) {
							docRepository.saveRetProcCall(value0.get(i), 
									value1.get(j), docType);
						List<Object[]> result = docRepository.findRetSumLevelData(
								value0.get(i), value1.get(j), docType);
						if (result != null && !result.isEmpty()) {
							repEntity.add(result);
						}
					}
				}
			}
			if (!repEntity.isEmpty()) {
				LOGGER.info("{} {} docs are eligible to do Ret SaveToGstn.",
						repEntity.size(), docType);
			}
		} catch (Exception ex) {
			throw new AppException("Error in fetching the Ret Gstn save Data", ex);

		} finally {
			TenantContext.clearTenant();
		}

		return repEntity;*/
		
	TenantContext.setTenantId(groupCode);
	LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
	//Assuming this proc will update the selected rows of both
	//vertical and Horizontal tables BATCH_ID to temp value Zero.
	docRepository.saveRetProcCall(gstin, retPeriod, docType, groupCode,
			GenUtil.convertTaxPeriodToInt(retPeriod));
	return docRepository.findRetSumLevelData(gstin, retPeriod, docType);
	
	
	}
	
	
	@Override
	public List<Object[]> findRetCancelledData(String gstin,
			String retPeriod,
			String groupCode, String docType) {
		/*LOGGER.debug("findCancelledData method with groupcode {} and "
				+ "request {}", groupCode, jsonString);
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		RetSaveToGstnReqDto dto = gson.fromJson(requestObject,
				RetSaveToGstnReqDto.class);
		LOGGER.debug("request dto is {} ", dto);
		List<List<Object[]>> repEntity = new ArrayList<>();
		try {
			Pair<List<String>, List<String>> pairs = screenExtractor
					.getRetCombinationPairs(dto, groupCode);
			if (pairs != null && pairs.getValue0() != null
					&& !pairs.getValue0().isEmpty() && pairs.getValue1() != null
					&& !pairs.getValue1().isEmpty()) {
				List<String> value0 = pairs.getValue0();
				List<String> value1 = pairs.getValue1();
				if (value0 != null && !value0.isEmpty() && 
						value1 != null && !value1.isEmpty()) {
					TenantContext.setTenantId(groupCode);
					LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
					for (int i = 0, j = 0; i < value0.size()
							&& j < value1.size(); i++, j++) {
						// This needs to change according to Ret
						List<Object[]> result = docRepository.
								findRetCancelledData(
								value0.get(i), value1.get(j), docType); 
						if (result != null && !result.isEmpty()) {
							repEntity.add(result);
						}
					}
				}
			}
			if (!repEntity.isEmpty()) {
				LOGGER.info("{} {} docs are eligible to do Ret SaveToGstn.",
						repEntity.size(), docType);
			}
		} catch (Exception ex) {
			throw new AppException("Error in fetching the gstn save Data", ex);

		} finally {
			TenantContext.clearTenant();
		}
		return repEntity;
		*/
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		return docRepository.findRetCancelledData(gstin, retPeriod, docType);
	}
}
