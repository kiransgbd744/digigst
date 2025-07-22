package com.ey.advisory.app.services.search.simplified.docsummary;

import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.master.RateMasterEntity;
import com.ey.advisory.admin.data.repositories.master.RateRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AARRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AATARepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AB2CSRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AUserInputHsnSacRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ARRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ATARepository;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputHsnSacRepository;
import com.ey.advisory.app.data.statecode.dto.GetUnitOfMeasureDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1VerticalRecordDeleteDto;
import com.ey.advisory.common.GsonUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Gstr1PopupRecordDeleteService")
public class Gstr1PopupRecordDeleteService {

	@Autowired
	@Qualifier("Gstr1B2CSRepository")
	Gstr1B2CSRepository repository;

	@Autowired
	@Qualifier("Gstr1ARRepository")
	Gstr1ARRepository gstr1ARRepository;

	@Autowired
	@Qualifier("Gstr1ATARepository")
	Gstr1ATARepository gstr1ATARepository;

	@Autowired
	@Qualifier("Gstr1UserInputHsnSacRepository")
	Gstr1UserInputHsnSacRepository hsnRepository;

	@Autowired
	@Qualifier("RateRepositoryMaster")
	RateRepository rateRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr1AB2CSRepository")
	Gstr1AB2CSRepository repository1a;

	@Autowired
	@Qualifier("Gstr1AARRepository")
	Gstr1AARRepository gstr1ARRepository1a;

	@Autowired
	@Qualifier("Gstr1AATARepository")
	Gstr1AATARepository gstr1ATARepository1a;

	@Autowired
	@Qualifier("Gstr1AUserInputHsnSacRepository")
	Gstr1AUserInputHsnSacRepository hsnRepository1a;

	public int deleteRecord(Gstr1VerticalRecordDeleteDto req) {
		int updateId = 0;
		// List<Long> id = req.getId();
		String returnType = req.getReturnType();
		if (Strings.isNullOrEmpty(returnType)) {
			if ("B2CS".equalsIgnoreCase(req.getDocType())) {
				updateId = repository.UpdateListId(req.getId());
			}
			if ("B2CSA".equalsIgnoreCase(req.getDocType())) {

				updateId = repository.UpdateListId(req.getId());
			}
			if ("HSN".equalsIgnoreCase(req.getDocType())) {

				updateId = hsnRepository.deleteKey(req.getDocKey());
			}

			if ("AT".equalsIgnoreCase(req.getDocType())) {

				updateId = gstr1ARRepository.UpdateId(req.getId());
			}
			if ("ATA".equalsIgnoreCase(req.getDocType())) {

				updateId = gstr1ARRepository.UpdateId(req.getId());
			}
			if ("TXPD".equalsIgnoreCase(req.getDocType())) {

				updateId = gstr1ATARepository.UpdateId(req.getId());
			}
			if ("TXPDA".equalsIgnoreCase(req.getDocType())) {

				updateId = gstr1ATARepository.UpdateId(req.getId());
			}
		} else {
			if ("B2CS".equalsIgnoreCase(req.getDocType())) {
				updateId = repository1a.UpdateListId(req.getId());
			}
			if ("B2CSA".equalsIgnoreCase(req.getDocType())) {

				updateId = repository1a.UpdateListId(req.getId());
			}
			if ("HSN".equalsIgnoreCase(req.getDocType())) {

				updateId = hsnRepository1a.deleteKey(req.getDocKey());
			}

			if ("AT".equalsIgnoreCase(req.getDocType())) {

				updateId = gstr1ARRepository1a.UpdateId(req.getId());
			}
			if ("ATA".equalsIgnoreCase(req.getDocType())) {

				updateId = gstr1ARRepository1a.UpdateId(req.getId());
			}
			if ("TXPD".equalsIgnoreCase(req.getDocType())) {

				updateId = gstr1ATARepository1a.UpdateId(req.getId());
			}
			if ("TXPDA".equalsIgnoreCase(req.getDocType())) {

				updateId = gstr1ATARepository1a.UpdateId(req.getId());
			}
		}

		return updateId;
	}

	/*
	 * public JsonArray fetchStateCode() {
	 * 
	 * Gson gson = GsonUtil.newSAPGsonInstance();
	 * 
	 * String sql = "select STATE_CODE from MASTER_STATE";
	 * 
	 * Query q = entityManager.createNativeQuery(sql);
	 * 
	 * @SuppressWarnings("unchecked") List<String> list = q.getResultList();
	 * 
	 * 
	 * JsonArray array = new JsonArray(); for(String s : list){
	 * 
	 * JsonObject dateResp = new JsonObject(); dateResp.add("value", new
	 * Gson().toJsonTree(s));
	 * 
	 * array.add(dateResp); }
	 * 
	 * // JsonElement stateCodeJson = gson.toJsonTree(list); return array;
	 * 
	 * }
	 */
	/*
	 * public List<Object> fetchUom() {
	 * 
	 * Gson gson = GsonUtil.newSAPGsonInstance();
	 * 
	 * String sql = "select UQC,UQC_DESC from MASTER_UOM";
	 * 
	 * Query q = entityManager.createNativeQuery(sql);
	 * 
	 * @SuppressWarnings("unchecked") List<Object[]> list = q.getResultList();
	 * 
	 * return list.parallelStream().map(o -> convert(o))
	 * .collect(Collectors.toCollection(ArrayList::new)); }
	 */

	/*
	 * private GetUnitOfMeasureDto convert(Object[] arr) { GetUnitOfMeasureDto
	 * getResponse = new GetUnitOfMeasureDto(); getResponse.setUom((String)
	 * arr[0]); getResponse.setUomDesc((String) arr[1]); return getResponse; }
	 */
	public JsonArray fetchRate() {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<RateMasterEntity> findAll = rateRepository.findAll();

		JsonArray array = new JsonArray();
		for (RateMasterEntity s : findAll) {

			JsonObject dateResp = new JsonObject();
			dateResp.add("value", new Gson().toJsonTree(s.getIgst()));

			array.add(dateResp);
		}

		return array;

	}
}
