package com.ey.advisory.app.services.validation.sales;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.services.onboarding.UserLoadService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Slf4j
@Component("DataSecurityApplicabilityChecker")
public class DataSecurityApplicabilityChecker {

	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;
	@Autowired
	@Qualifier("UserLoadServiceImpl")
	UserLoadService UserLoadServiceImpl;
	public boolean isAttrValid(String gstin, String attrType, String attrVal) {
		
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("DataSecurityApplicabilityChecker isAttrValid begin");
		}
		gstinInfoRepository = StaticContextHolder
				.getBean("GSTNDetailRepository", GSTNDetailRepository.class);

		List<GSTNDetailEntity> gstinEntity = 
				  gstinInfoRepository.findByGstin(gstin);
		if (gstinEntity == null || gstinEntity.isEmpty()) return false;
		
		Long entityId = gstinEntity.get(0).getEntityId();
		if(entityId==null) return false;
		User user = SecurityContext.getUser();
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("user begin");
		}
		if(user==null) return true;
		List<Quartet<String, String,String, String>> applicableAttrs = user
                		.getApplicableAttrs(entityId);		
		
		if (applicableAttrs==null || applicableAttrs.isEmpty() 
				|| applicableAttrs.size() <= 0) return true;
		// Filter for all the values in the above list that matches the
		// attribute name. If the return list has at least one value, then
		// return true. Otherwise, false.
		boolean isAttrApplicable = !applicableAttrs.stream().filter(
				p -> attrType.equals(p.getValue0()))
				.collect(Collectors.toList()).isEmpty();
		
		// If the attribute is not applicable, then return true;
		if (!isAttrApplicable) return true;
		
		// Get the actual attribute values fort the given attrType, for the
		// entity.
		List<Pair<Long, String>> attrValuesForAttrCode = user
				.getAttrValuesForAttrCode(entityId, attrType);
		if (attrValuesForAttrCode == null || attrValuesForAttrCode.isEmpty() 
				||  attrValuesForAttrCode.size() <= 0) return true;
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("DataSecurityApplicabilityChecker isAttrValid end");
		}
		return !attrValuesForAttrCode.stream().filter(
				p -> attrVal.equals(p.getValue1()))
				.collect(Collectors.toList()).isEmpty();
		
	}
	
	public boolean isAttrValid(String attrType, String attrVal, Long entityId,
			String userName) {
		
		if (userName == null || userName.isEmpty())
			return true;
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("DataSecurityApplicabilityChecker isAttrValid begin");
		}
		if (attrVal == null || attrVal.isEmpty())
			return true;

		if (entityId == null)
			return true;
		User user = SecurityContext.getUser();

		if (user == null) {
			user = UserLoadServiceImpl.loadUser(TenantContext.getTenantId(),
					userName);
			SecurityContext.setUser(user);
		}
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("user data:{}", user);
		}
		if (user == null)
			return true;
		List<Quartet<String, String,String, String>> applicableAttrs = user
				.getApplicableAttrs(entityId);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("applicableAttrs {}", applicableAttrs);
			}
		if (applicableAttrs == null || applicableAttrs.isEmpty())
			return true;
		// Filter for all the values in the above list that matches the
		// attribute name. If the return list has at least one value, then
		// return true. Otherwise, false.
		boolean isAttrApplicable = !applicableAttrs.stream()
				.filter(p -> attrType.equals(p.getValue0()))
				.collect(Collectors.toList()).isEmpty();

		// If the attribute is not applicable, then return true;
		if (!isAttrApplicable){

			if (LOGGER.isDebugEnabled() && attrType.equals(OnboardingConstant.GSTIN)) {
				LOGGER.debug("applicability false");
				}
			return true;
		}
		// Get the actual attribute values fort the given attrType, for the
		// entity.
		List<Pair<Long, String>> attrValuesForAttrCode = user
				.getAttrValuesForAttrCode(entityId, attrType);
		
		if (attrValuesForAttrCode == null || attrValuesForAttrCode.isEmpty()){
			if(attrType.equals(OnboardingConstant.GSTIN)){
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" attrType is null for GSTIN attrType");
					}
				return false;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" attrvalue for attrcode is coming null");
				}
			return true;
		}
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("DataSecurityApplicabilityChecker isAttrValid end");
		LOGGER.debug("attrValuesForAttrCode: {}", attrValuesForAttrCode);
		}
		return !attrValuesForAttrCode.stream()
				.filter(p -> attrVal.equals(p.getValue1()))
				.collect(Collectors.toList()).isEmpty();

	}
	
public boolean isAttrValid(String attrType, String attrVal,Long entityId) {
		
		
		
		if(attrVal==null || attrVal.isEmpty()) return true;
		
		if(entityId==null) return true;
		User user = SecurityContext.getUser();
	
		if(user==null) return true;
		List<Quartet<String, String,String, String>> applicableAttrs = user
                		.getApplicableAttrs(entityId);		
		
		if (applicableAttrs==null || applicableAttrs.isEmpty() 
				|| applicableAttrs.size() <= 0) return true;
		// Filter for all the values in the above list that matches the
		// attribute name. If the return list has at least one value, then
		// return true. Otherwise, false.
		boolean isAttrApplicable = !applicableAttrs.stream().filter(
				p -> attrType.equals(p.getValue0()))
				.collect(Collectors.toList()).isEmpty();
		
		// If the attribute is not applicable, then return true;
		if (!isAttrApplicable) return true;
		
		// Get the actual attribute values fort the given attrType, for the
		// entity.
		List<Pair<Long, String>> attrValuesForAttrCode = user
				.getAttrValuesForAttrCode(entityId, attrType);
		if (attrValuesForAttrCode == null || attrValuesForAttrCode.isEmpty() 
				||  attrValuesForAttrCode.size() <= 0) return true;
		
		return !attrValuesForAttrCode.stream().filter(
				p -> attrVal.equals(p.getValue1()))
				.collect(Collectors.toList()).isEmpty();
		
	}
	
	
	public boolean isAttrValid(Map<Long, List<Pair<String, String>>> entityAtValMap,
			                              String attrType, String attrVal,
			                              Long entityId){
		if(entityId!=null){
		List<String> pcList=new ArrayList<>();
		List<String> plantList=new ArrayList<>();
		List<String> divisionList=new ArrayList<>();
		List<String> soList=new ArrayList<>();
		List<String> poList=new ArrayList<>();
		List<String> dcList=new ArrayList<>();
		List<String> locationList=new ArrayList<>();
		List<String> ud1List=new ArrayList<>();
		List<String> ud2List=new ArrayList<>();
		List<String> ud3List=new ArrayList<>();
		List<String> ud4List=new ArrayList<>();
		List<String> ud5List=new ArrayList<>();
		List<String> ud6List=new ArrayList<>();

			List<Pair<String, String>> orgAttrlist = entityAtValMap
					.get(entityId);
			if (orgAttrlist != null && !orgAttrlist.isEmpty()) {
				if (attrVal != null && !attrVal.isEmpty()) {
					switch (attrType) {
					case OnboardingConstant.PC:

						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.PC
									.equalsIgnoreCase(attrCode)) {
								pcList.add(orgAttr.getValue1());
							}
						}

						if (pcList != null && !pcList.isEmpty()) {
							if (!pcList.contains(attrVal)) {
								return false;
							}
						}

						break;

					case OnboardingConstant.PLANT:

						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.PLANT
									.equalsIgnoreCase(attrCode)) {
								plantList.add(orgAttr.getValue1());

							}
						}

						if (plantList != null && !plantList.isEmpty()) {
							if (!plantList.contains(attrVal)) {
								return false;
							}
						}

						break;
					case OnboardingConstant.DIVISION:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.DIVISION
									.equalsIgnoreCase(attrCode)) {
								divisionList.add(orgAttr.getValue1());

							}
						}

						if (divisionList != null && !divisionList.isEmpty()) {
							if (!divisionList.contains(attrVal)) {
								return false;
							}

						}
						break;
					case OnboardingConstant.SO:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.SO
									.equalsIgnoreCase(attrCode)) {
								soList.add(orgAttr.getValue1());

							}
						}

						if (soList != null && !soList.isEmpty()) {
							if (!soList.contains(attrVal)) {
								return false;
							}
						}

						break;
					case OnboardingConstant.PO:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.PO
									.equalsIgnoreCase(attrCode)) {
								poList.add(orgAttr.getValue1());

							}
						}

						if (poList != null && !poList.isEmpty()) {
							if (!poList.contains(attrVal)) {
								return false;
							}

						}
						break;
					case OnboardingConstant.DC:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.DC
									.equalsIgnoreCase(attrCode)) {
								dcList.add(orgAttr.getValue1());

							}
						}

						if (dcList != null && !dcList.isEmpty()) {
							if (!dcList.contains(attrVal)) {
								return false;
							}

						}
						break;
					case OnboardingConstant.LOCATION:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.LOCATION
									.equalsIgnoreCase(attrCode)) {
								locationList.add(orgAttr.getValue1());

							}
						}

						if (locationList != null && !locationList.isEmpty()) {
							if (!locationList.contains(attrVal)) {
								return false;
							}

						}
						break;
					case OnboardingConstant.UD1:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.UD1
									.equalsIgnoreCase(attrCode)) {
								ud1List.add(orgAttr.getValue1());

							}
						}

						if (ud1List != null && !ud1List.isEmpty()) {
							if (!ud1List.contains(attrVal)) {
								return false;
							}
						}

						break;
					case OnboardingConstant.UD2:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.UD2
									.equalsIgnoreCase(attrCode)) {
								ud2List.add(orgAttr.getValue1());

							}
						}

						if (ud2List != null && !ud2List.isEmpty()) {
							if (!ud2List.contains(attrVal)) {
								return false;
							}

						}
						break;
					case OnboardingConstant.UD3:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.UD3
									.equalsIgnoreCase(attrCode)) {
								ud3List.add(orgAttr.getValue1());

							}
						}

						if (ud3List != null && !ud3List.isEmpty()) {
							if (!ud3List.contains(attrVal)) {
								return false;
							}

						}
						break;
					case OnboardingConstant.UD4:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.UD4
									.equalsIgnoreCase(attrCode)) {
								ud4List.add(orgAttr.getValue1());

							}
						}

						if (ud4List != null && !ud4List.isEmpty()) {
							if (!plantList.contains(attrVal)) {
								return false;
							}

						}
						break;
					case OnboardingConstant.UD5:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.UD5
									.equalsIgnoreCase(attrCode)) {
								ud5List.add(orgAttr.getValue1());

							}
						}

						if (ud5List != null && !ud5List.isEmpty()) {
							if (!ud5List.contains(attrVal)) {
								return false;
							}
						}

						break;
					case OnboardingConstant.UD6:
						for (Pair<String, String> orgAttr : orgAttrlist) {
							String attrCode = orgAttr.getValue0();
							if (OnboardingConstant.UD6
									.equalsIgnoreCase(attrCode)) {
								ud6List.add(orgAttr.getValue1());

							}
						}

						if (ud6List != null && !ud6List.isEmpty()) {
							if (!ud6List.contains(attrVal)) {
								return false;
							}
						}

						break;
					}
				}
			}

		}
		
return true;
		
	}
}
