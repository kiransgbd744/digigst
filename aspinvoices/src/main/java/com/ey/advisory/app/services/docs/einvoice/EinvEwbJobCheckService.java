/**
 * 
 *//*
package com.ey.advisory.app.services.docs.einvoice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.entities.client.SourceInfoEntity;
import com.ey.advisory.app.data.repositories.client.SourceInfoRepository;

import lombok.extern.slf4j.Slf4j;
*//**
 * @author Laxmi.Salukuti
 *
 *//*
@Component("EinvEwbJobCheckService")
@Slf4j
public class EinvEwbJobCheckService {

	@Autowired
	@Qualifier("SourceInfoRepository")
	private SourceInfoRepository sourceInfoRepository;
	private static final String DOC_KEY_JOINER = "|";
	public Map<String, Pair<Integer, Integer>> getJobByGstin(
			List<OutwardTransDocument> documents) {

		Map<String, Pair<Integer, Integer>> map = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		List<Long> entityList = new ArrayList<>();
		List<String> plantEntityGstinkeyList = new ArrayList<>();
		List<String> entityGstinkeyList = new ArrayList<>();
		if (documents != null && !documents.isEmpty()) {
			documents.forEach(entity -> {
				String gstin = entity.getSgstin();
				Long entityId  =entity.getEntityId();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" Gstin" + gstin);
				}
			String	plantEntityGstinkey=new StringJoiner(DOC_KEY_JOINER)
					 .add(gstin).add(entity.getEntityId().toString())
				.add(entity.getPlantCode()).toString();
			
			String	entityGstinkey=new StringJoiner(DOC_KEY_JOINER)
					 .add(gstin).add(entity.getEntityId().toString())
				.add(entity.getPlantCode()).toString();
			entityGstinkeyList.add(entityGstinkey);
			plantEntityGstinkeyList.add(plantEntityGstinkey);
				gstinList.add(gstin);
				entityList.add(entityId);
			});

				List<SourceInfoEntity> sourceDetails = findGstinDetails(
						entityList,entityGstinkeyList,plantEntityGstinkeyList);
				if (sourceDetails != null && !sourceDetails.isEmpty()) {
					sourceDetails.forEach(jobDetail -> {
						String gstin = jobDetail.getGstin();
						Integer einvJob = jobDetail.getEInvJob();
						Integer ewbJob = jobDetail.getEwbJob();

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Key-Gstin " + gstinList);
						}
						map.put(gstin,
								new Pair<Integer, Integer>(einvJob, ewbJob));
					});
				}
			
		}
		return map;
	}

	private List<SourceInfoEntity> findGstinDetails(List<Long> entityList,
			List<String> entityGstinkeyList,
			List<String> plantEntityGstinkeyList) {
		
		List<Object[]> sourceInfo = sourceInfoRepository.findewbJobAndEnvJobBygstinEntityPlantKey(plantEntityGstinkeyList);
		
		Map<String, LocalDate> orgDocKeyMap = sourceInfo.stream()
				.collect(Collectors.toMap(obj -> String.valueOf(obj[0]),
						obj -> (LocalDate) obj[1]));
		
		
		return null;
}

}
*/