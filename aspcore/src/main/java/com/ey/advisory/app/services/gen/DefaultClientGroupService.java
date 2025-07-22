package com.ey.advisory.app.services.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.GroupInfoEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoRepository;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.gstr2.userdetails.GstinDto;
/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("DefaultClientGroupService")
public class DefaultClientGroupService implements ClientGroupService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ClientGroupService.class);

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepository;

	@Autowired
	@Qualifier("groupInfoRepository")
	private GroupInfoRepository groupInfoRepository;

	@Override
	public List<GroupInfoEntity> getAllGroups() {
		List<GroupInfoEntity> groups = groupInfoRepository.getAllGroups();

		return groups;
	}

	/**
	 * getGstinsForEntity - Gets the GSTINs based on given Entity
	 * 
	 * @param groupCode
	 * @return
	 */
	@Override
	public List<GstinDto> getGstinsForEntity(List<Long> entityIds,
			String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Entity Id List " + entityIds + " for Group Code "
					+ groupCode);
		}
		List<GSTNDetailEntity> gstins = new ArrayList<>();
		if (!entityIds.isEmpty()) {
			gstins = gstinInfoRepository.findByEntityIds(entityIds);
		}
		List<GstinDto> gstinList = new ArrayList<>();
		gstins.forEach(gstin -> {
			GstinDto gstinDto = new GstinDto();
			gstinDto.setEntityId(gstin.getEntityId());
			gstinDto.setGstin(gstin.getGstin());
			gstinList.add(gstinDto);
		});
		// Sort the list in Ascending Order
		Collections.sort(gstinList, new Comparator<GstinDto>() {
			@Override
			public int compare(GstinDto gstin1, GstinDto gstin2) {
				return gstin1.getGstin().compareTo(gstin2.getGstin());
			}
		});
		return gstinList;
	}

	@Override
	public List<EntityDto> getEntitiesForGroup(String groupCode) {
		List<EntityInfoEntity> entities = entityInfoRepository
				.findEntityInfoDetails(groupCode);

		List<EntityDto> entityList = new ArrayList<>();

		entities.forEach(entity -> {
			EntityDto entityDto = new EntityDto();
			entityDto.setEntityId(entity.getId());
			Long entityId = entity.getId();
			List<String> gstinList = gstinInfoRepository
					.findgstinByEntityId(entityId);
			List<GstinDto> gst = new ArrayList<>();
			GstinDto gst1 = new GstinDto();
			for (String gstins : gstinList) {
				gst1.setGstin(gstins);
				gst.add(gst1);
			}
			entityDto.setEntityName(entity.getEntityName());
			entityDto.setGstin(gst);
			entityList.add(entityDto);
		});
		return entityList;
	}

	@Override
	public List<Long> findEntityDetailsForGroupCode() {
		List<Long> entityIdList = new ArrayList<>();
		String groupCode = TenantContext.getTenantId();
		List<EntityInfoEntity> entityListForGrpCode = entityInfoRepository
				.findEntityInfoDetails(groupCode);
		if (entityListForGrpCode != null && !entityListForGrpCode.isEmpty()) {
			entityListForGrpCode.forEach(entity -> {
				Long id = entity.getId();
				String entityName = entity.getEntityName();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" Entity Id " + id + " entityName "
							+ entityName + " for Group Code " + groupCode);
				}
				entityIdList.add(id);
			});
		}
		return entityIdList;
	}

	@Override
	public Map<String, Long> getGstinAndEntityMapForGroupCode(
			List<Long> entityIds) {
		Map<String, Long> map = new HashMap<>();
		String groupCode = TenantContext.getTenantId();
		if (entityIds != null && !entityIds.isEmpty()) {
			List<GSTNDetailEntity> gstinDetails = findGstinDetailsByEntityIds(
					entityIds);
			if (gstinDetails != null && !gstinDetails.isEmpty()) {
				gstinDetails.forEach(gstinDetail -> {
					Long entityId = gstinDetail.getEntityId();
					String gstin = gstinDetail.getGstin();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Key-Gstin " + gstin + " Value-Entity Id "
								+ entityId + " for Group Code " + groupCode);
					}
					map.put(gstin, entityId);
				});
			}
		}
		return map;
	}
	
	private List<GSTNDetailEntity> findGstinDetailsByEntityIds(
			List<Long> entityIds) {
		List<GSTNDetailEntity> gstinDetails = gstinInfoRepository
				.findByEntityIds(entityIds);
		return gstinDetails;
	}

	@Override
	public Map<Long, String> getEntityAndReturnPeriodMapForGroupCode(
			List<Long> entityIdList) {
		Map<Long, String> map = new HashMap<>();
		map.put(7L, "012019");
		map.put(8L, "022019");
		map.put(9L, "032019");
		map.put(10L, "042019");
		map.put(11L, "052019");
		map.put(12L, "062019");
		map.put(13L, "052019");
		return map;
	}

	
	public List<Long> getEntities(List<Long> entityIds) {
		User user = SecurityContext.getUser();
		Set<Long> entityIdsSet = user.getEntityMap().keySet();
        if(entityIdsSet != null){
       	 entityIds.addAll(entityIdsSet);
        }
		return entityIds;
		
	}

}
