package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("saveAnx1DataFetcherImpl")
@Slf4j
public class SaveAnx1DataFetcherImpl implements SaveAnx1DataFetcher {

	private static final String LOG_GROUPCODE_MSG = "groupCode {} is set";
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	/*@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;*/

	@Override
	public List<Object[]> findAnx1InvoiceLevelData(String gstin,
			String retPeriod,
			String groupCode, String docType, List<Long> docIds) {

		/*JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Anx1SaveToGstnReqDto dto = gson.fromJson(requestObject,
				Anx1SaveToGstnReqDto.class);*/
		/*List<List<Object[]>> repEntity = new ArrayList<>();
		try {
			Pair<List<String>, List<String>> pairs = screenExtractor
					.getAnx1CombinationPairs(dto, groupCode);
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
						// This needs to change according to Anx1
						List<Object[]> result = docRepository
								.findAnx1InvLevelData(value0.get(i),
										value1.get(j), docType, docIds);
						if (!result.isEmpty()) {
							repEntity.add(result);
						}
					}
				}
			}
			if (!repEntity.isEmpty()) {
				LOGGER.info("{} {} docs are eligible to do Anx1 SaveToGstn.",
						repEntity.size(), docType);
			}
		} catch (Exception ex) {
			throw new AppException("Error in fetching the Anx1 SaveToGstn", ex);

		} finally {
			TenantContext.clearTenant();
		}
		return repEntity;*/
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		return docRepository.findAnx1InvLevelData(gstin, retPeriod, docType,
				docIds);
	}

	@Override
	public List<Object[]> findAnx1SummaryData(String gstin,
			String retPeriod,
			String groupCode, String docType) {

		/*JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Anx1SaveToGstnReqDto dto = gson.fromJson(requestObject,
				Anx1SaveToGstnReqDto.class);*/
		/*List<List<Object[]>> repEntity = new ArrayList<>();
		try {
			Pair<List<String>, List<String>> pairs = screenExtractor
					.getAnx1CombinationPairs(dto, groupCode);
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
						//Assuming this proc will update the selected rows of both
						//vertical and Horizontal tables BATCH_ID to temp value Zero.
							docRepository.saveAnx1ProcCall(value0.get(i), 
									value1.get(j), docType, groupCode,
									GenUtil.convertTaxPeriodToInt(value1.get(j)));
						List<Object[]> result = docRepository.findAnx1SumLevelData(
								value0.get(i), value1.get(j), docType);
						if (result != null && !result.isEmpty()) {
							repEntity.add(result);
						}
					}
				}
			}
			if (!repEntity.isEmpty()) {
				LOGGER.info("{} {} docs are eligible to do Anx1 SaveToGstn.",
						repEntity.size(), docType);
			}
		} catch (Exception ex) {
			throw new AppException("Error in fetching the Anx1 Gstn save Data", ex);

		} finally {
			TenantContext.clearTenant();
		}

		return repEntity;*/
		
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		//Assuming this proc will update the selected rows of both
		//vertical and Horizontal tables BATCH_ID to temp value Zero.
		docRepository.saveAnx1ProcCall(gstin, retPeriod, docType, groupCode,
				GenUtil.convertTaxPeriodToInt(retPeriod));
		return docRepository.findAnx1SumLevelData(gstin, retPeriod, docType);
	}
	
/*	@Override
	public List<List<Object[]>> findAnx1Data(Anx1SaveToGstnReqDto dto,
			String groupCode, String docType, List<Long> docIds) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Anx1SaveToGstnReqDto dto = gson.fromJson(requestObject,
				Anx1SaveToGstnReqDto.class);
		List<List<Object[]>> repEntity = new ArrayList<>();
		try {
			Pair<List<String>, List<String>> pairs = screenExtractor
					.getAnx1CombinationPairs(dto, groupCode);
			if (pairs != null && pairs.getSize() > 0) {
				List<String> value0 = pairs.getValue0();
				List<String> value1 = pairs.getValue1();
				if (value0 != null && !value0.isEmpty() && 
						value1 != null && !value1.isEmpty()) {
					TenantContext.setTenantId(groupCode);
					LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
					for (int i = 0, j = 0; i < value0.size()
							&& j < value1.size(); i++, j++) {
						// This needs to change according to Anx1
						List<Object[]> result = docRepository
								.findAnx1Data(value0.get(i),
										value1.get(j), docType, docIds);
						if (!result.isEmpty()) {
							repEntity.add(result);
						}
					}
				}
			}
			if (!repEntity.isEmpty()) {
				LOGGER.info("{} {} docs are eligible to do Anx1 SaveToGstn.",
						repEntity.size(), docType);
			}
		} catch (Exception ex) {
			throw new AppException("Error in fetching the Anx1 Gstn save Data", ex);

		} finally {
			TenantContext.clearTenant();
		}
		return repEntity;
}*/
	
	@Override
	public List<Object[]> findAnx1CancelledData(String gstin,
			String retPeriod,
			String groupCode,
			String docType) {
		/*LOGGER.debug("findCancelledData method with groupcode {} and "
				+ "request {}", groupCode, dto);*/
		/*JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Anx1SaveToGstnReqDto dto = gson.fromJson(requestObject,
				Anx1SaveToGstnReqDto.class);*/
/*		LOGGER.debug("request dto is {} ", dto);
		List<List<Object[]>> repEntity = new ArrayList<>();
		try {
			Pair<List<String>, List<String>> pairs = screenExtractor
					.getAnx1CombinationPairs(dto, groupCode);
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
						// This needs to change according to Anx1
						List<Object[]> result = docRepository.
								findAnx1CancelledData(
								value0.get(i), value1.get(j), docType); 
						if (result != null && !result.isEmpty()) {
							repEntity.add(result);
						}
					}
				}
			}
			if (!repEntity.isEmpty()) {
				LOGGER.info("{} {} docs are eligible to do Anx1 SaveToGstn.",
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
		return docRepository.findAnx1CancelledData(gstin, retPeriod, docType);
	}
}
