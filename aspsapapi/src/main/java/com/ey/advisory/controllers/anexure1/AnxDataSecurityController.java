package com.ey.advisory.controllers.anexure1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.docs.dto.AnxDataSecurityDto;
import com.ey.advisory.app.docs.dto.DataSecurityReqDto;
import com.ey.advisory.app.services.datasecurity.AnxDataSecurityService;
import com.ey.advisory.app.services.gen.EntityDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class AnxDataSecurityController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AnxDataSecurityController.class);

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("DefaultAnxDataSecurityService")
	private AnxDataSecurityService dataSecurityService;

	/*@RequestMapping(value = "/getAnxDataSecApplAttr1", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnxDataSecurity(
			@RequestBody String jsonString) {

		JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
		String reqJson = obj.get("req").getAsJsonObject().toString();

		Gson gson = GsonUtil.newSAPGsonInstance();

		DataSecurityReqDto dto = gson.fromJson(reqJson,
				DataSecurityReqDto.class);

		Long reqEntityId = dto.getEntityId();

		User user = SecurityContext.getUser();
		Map<Long, Map<String, List<Pair<Long, String>>>> attributeMap = user
				.getAttributeMap();

		Set<Long> keySet = attributeMap.keySet();
		Map<String, Object> resultMap = new LinkedHashMap<>();
		// List<Object> GSTinList=new ArrayList<>();
		DataSecValueDto valueDto = new DataSecValueDto();
		List<DataSecDto> gstinlist = new ArrayList<>();
		valueDto.setGSTIN(gstinlist);
		List<DataSecDto> profit = new ArrayList<>();
		valueDto.setPC(profit);
		List<DataSecDto> plant = new ArrayList<>();
		valueDto.setPlant(plant);
		List<DataSecDto> division = new ArrayList<>();
		valueDto.setD(division);
		List<DataSecDto> location = new ArrayList<>();
		valueDto.setL(location);
		List<DataSecDto> so = new ArrayList<>();
		valueDto.setSO(so);
		List<DataSecDto> po = new ArrayList<>();
		valueDto.setPO(po);
		List<DataSecDto> ud3 = new ArrayList<>();
		valueDto.setUD3(ud3);
		List<DataSecDto> ud2 = new ArrayList<>();
		valueDto.setUD2(ud2);
		List<DataSecDto> ud1 = new ArrayList<>();
		valueDto.setUD1(ud1);
		List<DataSecDto> ud4 = new ArrayList<>();
		valueDto.setUD4(ud4);
		List<DataSecDto> ud5 = new ArrayList<>();
		valueDto.setUD5(ud5);
		List<DataSecDto> ud6 = new ArrayList<>();
		valueDto.setUD6(ud6);
		List<DataSecDto> dc = new ArrayList<>();
		valueDto.setDC(dc);
		for (Long keyId : keySet) {
			if (keyId.equals(reqEntityId)) {
				Map<String, List<Pair<Long, String>>> map = attributeMap
						.get(keyId);

				Set<String> keySet2 = map.keySet();
				for (String mapKey : keySet2) {
					if (mapKey.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(gstin -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(gstin);

							gstinlist.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.PC)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(pc -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(pc);

							profit.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.PLANT)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(Plant -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(Plant);

							plant.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(D -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(D);

							division.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(L -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(L);

							location.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.SO)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(SO -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(SO);

							so.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.PO)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(porg -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(porg);

							po.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.UD3)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(UD3 -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(UD3);

							ud3.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.UD1)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(UD1 -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(UD1);

							ud1.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.UD2)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(UD2 -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(UD2);

							ud2.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.UD4)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(UD4 -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(UD4);

							ud4.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.UD5)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(UD5 -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(UD5);

							ud5.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.UD6)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(UD6 -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(UD6);

							ud6.add(dataSec);
						});

					}
					if (mapKey.equalsIgnoreCase(OnboardingConstant.DC)) {
						List<Pair<Long, String>> list = map.get(mapKey);
						List<String> resultList = new ArrayList<>();
						resultMap.put(mapKey, resultList);
						for (Pair<Long, String> pair : list) {
							resultList.add(pair.getValue1());
						}
						resultList.forEach(DC -> {
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(DC);

							dc.add(dataSec);
						});

					}

				}
			}
		}

		JsonObject resp = new JsonObject();

		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", gson.toJsonTree(valueDto));
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

	}*/

	@RequestMapping(value = "/ui/getAnxDataSecEntities", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEntities() {
		LOGGER.debug("Entered getEntities method");
		User user = SecurityContext.getUser();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("User Id " + user.getUserPrincipalName());
		}

		Map<Long, List<Quartet<String, String,String, String>>> entityMap = user.getEntityMap();
		Map<Long, Map<String, List<Pair<Long, String>>>> attributeMap = user
				.getAttributeMap();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("entityMap " + entityMap + "attributeMap "
					+ attributeMap);
		}
		List<EntityDto> entityRespList = new ArrayList<>();
		if (attributeMap != null && !attributeMap.isEmpty()) {
			Set<Long> entityIds = attributeMap.keySet();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Data Secured Entity Ids " + entityIds);
			}
			List<EntityInfoEntity> entityList = entityInfoRepository
					.findByEntityIds(entityIds);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("entityList " + entityList);
			}
			if (entityList != null && !entityList.isEmpty()) {
				entityList.forEach(entity -> {
					EntityDto entityDto = new EntityDto();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Data Secured  Entity Id " + entity.getId());
					}
					entityDto.setEntityId(entity.getId());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Data Secured Entity Name "
								+ entity.getEntityName());
					}
					entityDto.setEntityName(entity.getEntityName());
					entityRespList.add(entityDto);
				});

				// Sort the list in Ascending Order
				Collections.sort(entityRespList, new Comparator<EntityDto>() {
					@Override
					public int compare(EntityDto entity1, EntityDto entity2) {
						return entity1.getEntityId()
								.compareTo(entity2.getEntityId());
					}
				});
			}
		}

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", gson.toJsonTree(entityRespList));
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * 
	 * @return
	 */
	/*@RequestMapping(value = "/getAnxDataSecurityAttributes", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnxDataSecurityAttributes() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entered getAnxDataSecurityAttributes method");
		}
		List<AnxDataSecurityDto> respList = dataSecurityService
				.getAnxDataSecurityAttributes();
		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject resp = new JsonObject();

		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", gson.toJsonTree(respList));
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}*/

	@RequestMapping(value = "/ui/getAnxDataSecApplAttr", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnxDataSecurityApplAttrs(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();

			Gson gson = GsonUtil.newSAPGsonInstance();

			DataSecurityReqDto dto = gson.fromJson(reqJson,
					DataSecurityReqDto.class);

			Long reqEntityId = dto.getEntityId();
			JsonObject resp = new JsonObject();
			if (reqEntityId != null) {
				AnxDataSecurityDto dataSecurityDto = dataSecurityService
						.getAnxDataSecurityApplAttributes(reqEntityId);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(dataSecurityDto));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.ERROR_STATUS));
				String errMsg = "Entity Id is mandatory";
				resp.add("resp", gson.toJsonTree(errMsg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while getting "
					+ "Data Security Applicable Attributes";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
