package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.anx1.Anx1SaveToGstnReqDto;
import com.ey.advisory.app.docs.dto.ret.RetSaveToGstnReqDto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("screenDeciderAndExtractorImpl")
@Slf4j
public class ScreenDeciderAndExtractorImpl
		implements ScreenDeciderAndExtractor {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	
	@Autowired
	@Qualifier("DocRepositoryGstr1A")
	private DocRepositoryGstr1A docRepositorygstr1A;

	@Override
	public List<Pair<String, String>> getGstr1CombinationPairs(
			Gstr1SaveToGstnReqDto dto, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getCombinationPairs method dto {} and groupCode {}",
					dto, groupCode);
		}
		List<Pair<String, String>> listOfPairs = new ArrayList<>();

		List<String> gstins = dto.getSgstins();
		String retPeriod = dto.getReturnPeriod();
		if (gstins != null && !gstins.isEmpty() && retPeriod != null) { // SaveToGstn Screen
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTIN and ReturnPeriod combination is Received from user.");
				LOGGER.debug("Gstr1 SaveToGstn Screen");
			}
			// return new Pair<>(value0, value1);
			for (int i = 0; i < gstins.size(); i++) {
				listOfPairs.add(new Pair<>(gstins.get(i), retPeriod));
			}
			return listOfPairs;
		} else { // Data status screen
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTIN and ReturnPeriod combination is not Received from user.");
				LOGGER.debug("Gstr1 DataStatus Screen");
			}
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			return docRepository.getGstr1SgstinRetPerds(dto);
		}

	}

	@Override
	public List<Pair<String, String>> getAnx1CombinationPairs(
			Anx1SaveToGstnReqDto dto, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getTestAnx1CombinationPairs dto {} and groupCode {}",
					dto, groupCode);
		}
		List<Pair<String, String>> listOfPairs = new ArrayList<>();

		List<String> gstins = dto.getGstins();
		String retPeriod = dto.getReturnPeriod();
		// Anx1 Processed Records Screen
		if (gstins != null && !gstins.isEmpty() && retPeriod != null
				&& !retPeriod.isEmpty()) {
			// simple logic to add the return period as many times as
			// gstin present to a list
			// List<String> value2 = new ArrayList<>();
			gstins.forEach(
					gstin -> listOfPairs.add(new Pair<>(gstin, retPeriod)));
			// return new Pair<>(value0, value2);
		} else {
			// In future if SaveToGstn Option comes from different screen
			// with some other save criteria then we can handle it by this
			// else section
			// return new Pair<>(null, null) ;
		}
		return listOfPairs;
	}

	@Override
	public List<Pair<String, String>> getRetCombinationPairs(
			RetSaveToGstnReqDto dto, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getTestRetCombinationPairs dto {} and groupCode {}",
					dto, groupCode);
		}
		List<Pair<String, String>> listOfPairs = new ArrayList<>();

		List<String> gstins = dto.getGstins();
		String retPeriod = dto.getReturnPeriod();
		// Anx1 Processed Records Screen
		if (gstins != null && !gstins.isEmpty() && retPeriod != null
				&& !retPeriod.isEmpty()) {
			// simple logic to add the return period as many times as
			// gstin present to a list
			// List<String> value2 = new ArrayList<>();
			gstins.forEach(
					gstin -> listOfPairs.add(new Pair<>(gstin, retPeriod)));
			// return new Pair<>(value0, value2);
		} else {
			// In future if SaveToGstn Option comes from different screen
			// with some other save criteria then we can handle it by this
			// else section
			// return new Pair<>(null, null) ;
		}
		return listOfPairs;
	}
	
	@Override
	public List<Pair<String, String>> getGstr7CombinationPairs(
			Gstr1SaveToGstnReqDto dto, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getCombinationPairs method dto {} and groupCode {}",
					dto, groupCode);
		}
		List<Pair<String, String>> listOfPairs = new ArrayList<>();

		List<String> gstins = dto.getSgstins();
		String retPeriod = dto.getReturnPeriod();
		Long entityid=dto.getEntityId();
		
		if (gstins != null && !gstins.isEmpty() && retPeriod != null) { // SaveToGstn Screen
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTIN and ReturnPeriod combination is Received from user.");
				LOGGER.debug("Gstr7 SaveToGstn Screen");
			}
			// return new Pair<>(value0, value1);
			for (int i = 0; i < gstins.size(); i++) {
				listOfPairs.add(new Pair<>(gstins.get(i), retPeriod));
			}
			return listOfPairs;
		} else { // Data status screen
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTIN and ReturnPeriod combination is not Received from user.");
				LOGGER.debug("Gstr7 DataStatus Screen");
			}
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			return docRepository.getGstr7SgstinRetPerds(dto);
		}

	}
	
	@Override
	public List<Pair<String, String>> getItc04CombinationPairs(
			Gstr1SaveToGstnReqDto dto, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getCombinationPairs method dto {} and groupCode {}",
					dto, groupCode);
		}
		List<Pair<String, String>> listOfPairs = new ArrayList<>();

		List<String> gstins = dto.getSgstins();
		String retPeriod = dto.getReturnPeriod();
		if (gstins != null && !gstins.isEmpty() && retPeriod != null) { // SaveToGstn Screen
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTIN and ReturnPeriod combination is Received from user.");
				LOGGER.debug("Itc04 SaveToGstn Screen");
			}
			// return new Pair<>(value0, value1);
			for (int i = 0; i < gstins.size(); i++) {
				listOfPairs.add(new Pair<>(gstins.get(i), retPeriod));
			}
			return listOfPairs;
		} else { // Data status screen
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTIN and ReturnPeriod combination is not Received from user.");
				LOGGER.debug("Itc04 DataStatus Screen");
			}
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			return docRepository.getItc04SgstinRetPerds(dto);
		}

	}
	
	@Override
	public List<Pair<String, String>> getGstr1ACombinationPairs(
			Gstr1SaveToGstnReqDto dto, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getCombinationPairs gstr1A method dto {} and groupCode {}",
					dto, groupCode);
		}
		List<Pair<String, String>> listOfPairs = new ArrayList<>();

		List<String> gstins = dto.getSgstins();
		String retPeriod = dto.getReturnPeriod();
		if (gstins != null && !gstins.isEmpty() && retPeriod != null) { // SaveToGstn Screen
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTIN and ReturnPeriod combination is Received from user.");
				LOGGER.debug("Gstr1A SaveToGstn Screen");
			}
			// return new Pair<>(value0, value1);
			for (int i = 0; i < gstins.size(); i++) {
				listOfPairs.add(new Pair<>(gstins.get(i), retPeriod));
			}
			return listOfPairs;
		} else { // Data status screen
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTIN and ReturnPeriod combination is not Received from user.");
				LOGGER.debug("Gstr1A DataStatus Screen");
			}
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			
			return docRepositorygstr1A.getGstr1ASgstinRetPerds(dto);
		}

	}
}
