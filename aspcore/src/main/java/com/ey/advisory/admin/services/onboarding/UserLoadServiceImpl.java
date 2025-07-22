package com.ey.advisory.admin.services.onboarding;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityAtConfigEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.DataSecurityRepository;
import com.ey.advisory.admin.data.repositories.client.EntityAtConfiRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;

@Service("UserLoadServiceImpl")
public class UserLoadServiceImpl implements UserLoadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserLoadServiceImpl.class);

	@Autowired
	@Qualifier("dataSecurityRepository")
	private DataSecurityRepository dataSecurityRepository;

	@Autowired
	@Qualifier("entityAtConfiRepository")
	private EntityAtConfiRepository entityAtConfigRepository;
	
	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userCreationRepository;

	@Override
	public User loadUser(String groupCode,String usrPrnplName) {
		LOGGER.info("About to load the User attributes info for User "
				+ usrPrnplName);
		Long userId = userCreationRepository.findIdByUserName(usrPrnplName);
		List<Object[]> dataSecurityList = dataSecurityRepository
				.findDataSecurityWithAttributeValues(userId);

		List<EntityAtConfigEntity> applicableList = entityAtConfigRepository
				.findAllEntityAtConfigApplicableForGroup(groupCode);

		Map<Long, List<Quartet<String, String,String, String>>> applicableAttrMap = applicableList
				.stream()
				.collect(Collectors.groupingBy(
						EntityAtConfigEntity::getEntityId,
						Collectors.mapping(
								obj -> new Quartet<String, String,String, String>(obj.getAtCode(),
										obj.getAtName(),obj.getAtOutward(),obj.getAtInward()),
								Collectors.toList())));

		Map<Long, Map<String, List<Pair<Long, String>>>> attributeValueMap =
				dataSecurityList.stream()
				.collect(Collectors.groupingBy(obj -> (Long) obj[0], Collectors
						.groupingBy(obj -> (String) obj[1], Collectors.mapping(
								obj -> new Pair<Long, String>((Long) obj[2],
										(String) obj[3]),
								Collectors.toList()))));
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("attributeValueMap is {} ", 
					attributeValueMap);
			LOGGER.debug(msg);
		}
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("applicableAttrMap is {} ", 
					applicableAttrMap);
			LOGGER.debug(msg);
		}

		return new User(applicableAttrMap, attributeValueMap);
	}

}
