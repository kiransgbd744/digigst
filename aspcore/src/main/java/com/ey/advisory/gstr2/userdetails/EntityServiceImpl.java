package com.ey.advisory.gstr2.userdetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.master.StateCodeInfoEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;

@Component("EntityServiceImpl")
public class EntityServiceImpl
		implements EntityService {

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepository;
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;
	
	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	StatecodeRepository stateCodeRepo;

	@Override
	public List<EntityInfoEntity> getAllEntities(String groupCode) {

		List<EntityInfoEntity> result = entityInfoRepository
				.findEntitiesByGroupCode(groupCode);
		List<EntityInfoEntity> response = new ArrayList<EntityInfoEntity>();
		for (EntityInfoEntity res : result) {
			EntityInfoEntity temp = new EntityInfoEntity();
			temp.setId(res.getId());
			temp.setEntityName(res.getEntityName());
			response.add(temp);
		}

		return response;
	}


	@Override
	public List<String> getGSTINsForEntity(Long entityId) {

		return gSTNDetailRepository.filterGstinBasedOnRegTypeforACD(entityId);
	}


	@Override
	public Map<String, String> getStateNames(List<String> gstins) {
		
		List<StateCodeInfoEntity> states = stateCodeRepo.findAll();
		
		Map<String, String> stateCodeToNameMap =  
				states.stream().collect(Collectors.toMap(
						o -> o.getStateCode(), o -> o.getStateName()));
		Map<String, String> retMap = new HashMap<>();
		gstins.stream().forEach(gstin -> {
			String key = gstin;
			String value = stateCodeToNameMap.get(gstin.substring(0, 2));
			retMap.put(key,  value);
		});
		
		return retMap;
	}
	
	@Override
	public Map<String, String> getRegType(List<String> gstins) {
		
		List<GSTNDetailEntity> entityList = gSTNDetailRepository.findAll();
		
		Map<String, String> RegTypeGstinMap =  
				entityList.stream().collect(Collectors.toMap(
						o -> o.getGstin(), o -> o.getRegistrationType()));
		
		
		return RegTypeGstinMap;
	}
	
}
 