/**
 * 
 */
package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.EntityUserMapping;
import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.EntityUserMappingRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.GroupService;

/**
 * @author Sasidhar Reddy
 *
 */
@Service("UserCreationObjArrToEntityConverter")
public class UserCreationObjArrToEntityConverter {

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("groupService")
	private GroupService groupService;

	@Autowired
	@Qualifier("EntityUserMappingRepository")
	private EntityUserMappingRepository entityUserMappingRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userCreationRepository;

	public Pair<List<String>, List<UserCreationEntity>> convert(
			List<Object[]> userObjects, String groupCode, Long entityId)
			throws Exception {
		List<String> errorMsgs = new ArrayList<>();
		List<UserCreationEntity> userCreationEntities = new ArrayList<UserCreationEntity>();
		String attUserName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		final Long groupId = groupInfoDetailsRepository
				.findByGroupId(groupCode);
		for (Object[] userInfo : userObjects) {
			UserCreationEntity creationEntity = new UserCreationEntity();
			String trim = String.valueOf(userInfo[0]);
			String userName = trim.trim();
			String firstNameTrim = String.valueOf(userInfo[1]);
			String firstName = firstNameTrim.trim();
			String lastNameTrim = String.valueOf(userInfo[2]);
			String lastName = lastNameTrim.trim();
			String emailTrim = String.valueOf(userInfo[3]);
			String email = emailTrim.trim();
			// changing mobile number from Double to Integer--->
			BigDecimal mobileDecimalFormat = BigDecimal.ZERO;
			if (userInfo[4] != null) {
				String mobileDecimalFormatStr = (String.valueOf(userInfo[4]))
						.trim();
				mobileDecimalFormat = new BigDecimal(mobileDecimalFormatStr);
			}

			String mobile = String.valueOf(mobileDecimalFormat.longValue());
			String userRole = String.valueOf(userInfo[5]);
			if (userName == null || userName.trim().isEmpty()
					|| userName.equals("null")) {
				errorMsgs.add(userName + " userName is mandatory."
						+ "and should not be empty");
			}
			if (firstName == null || firstName.trim().isEmpty()
					|| firstName.equals("null")) {
				errorMsgs.add(firstName + " firstName is mandatory."
						+ "and should not be empty");
			}
			if (lastName == null || lastName.trim().isEmpty()
					|| lastName.equals("null")) {
				errorMsgs.add(lastName + " lastName is mandatory"
						+ "and should not be empty");
			}
			if (email == null || email.trim().isEmpty()
					|| email.equals("null")) {
				errorMsgs.add(email + " email is mandatory."
						+ "and should not be empty");
			}
			if (mobile == null || mobile.trim().isEmpty()
					|| mobile.equals("null")) {
				errorMsgs.add(mobile + " Mobilenumber is mandatory "
						+ "and should not be empty");
			}
			if (userName != null && firstName != null && lastName != null
					&& email != null && mobile != null && userRole != null) {
				List<Long> userNamecount = userCreationRepository
						.findUserIdByUserName(userName, groupCode);
				if (userNamecount != null && userNamecount.size() > 0) {

					List<EntityUserMapping> byUserAndEntityId = entityUserMappingRepository
							.getEntityIds(userNamecount.get(0));
					if (byUserAndEntityId != null
							&& byUserAndEntityId.size() > 0) {
						List<Long> entityIds = byUserAndEntityId.stream()
								.map(mapping -> mapping.getEntityId())
								.collect(Collectors.toList());
						if (entityIds.contains(entityId)) {
							EntityInfoEntity entityInfo = entityInfoDetailsRepository
									.findEntityByEntityId(entityId);
							errorMsgs.add(
									userName + " - is already exists for this "
											+ "entityName - "
											+ entityInfo.getEntityName());
						}
					}
				}
				creationEntity.setGroupId(groupId);
				creationEntity.setGroupCode(groupCode);
				creationEntity.setUserName(userName);
				creationEntity.setFirstName(firstName);
				creationEntity.setLastName(lastName);
				creationEntity.setEmail(email);
				creationEntity.setMobile(mobile);
				creationEntity.setUserRole(userRole);
				creationEntity.setIsFlag(false);
				creationEntity.setCreatedBy(attUserName);
				creationEntity.setCreatedOn(
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
				creationEntity.setModifiedBy(attUserName);
				creationEntity.setModifiedOn(
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
				userCreationEntities.add(creationEntity);
			}
		}
		return new Pair<List<String>, List<UserCreationEntity>>(errorMsgs,
				userCreationEntities);
	}

}