package com.ey.advisory.admin.fileupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.PermissionsMasterEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.entities.client.UserPermissionsEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.PermissionsMasterRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.admin.data.repositories.client.UserPermissionsRepository;
import com.ey.advisory.app.util.HeaderCheckerUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;

@Service("AppPermissionsFileUploadServiceImpl")
public class AppPermissionsFileUploadServiceImpl
		implements AppPermissionsFileUploadService {

	private static final String[] EXPECTED_HEADERS = { "UserName",
			"Permission Code" };

	@Autowired
	@Qualifier("HeaderCheckerUtil")
	private HeaderCheckerUtil headerCheckerUtil;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("UserPermissionsRepository")
	private UserPermissionsRepository userPermissionsRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userCreationRepository;

	@Autowired
	@Qualifier("PermissionsMasterRepository")
	private PermissionsMasterRepository masterRepository;

	public String appPermissionFileUpload(List<Object[]> appPermissionObjs,
			Object[] header, String groupCode, Long entityId) {
		List<UserPermissionsEntity> userPermissionEntities = new ArrayList<>();
		String msg = null;
		Pair<Boolean, String> pair = headerCheckerUtil
				.validateHeaders(EXPECTED_HEADERS, header);

		// Convert the object array to domain objects.
		if (pair.getValue0()) {
			User user = SecurityContext.getUser();

			if (appPermissionObjs != null && !appPermissionObjs.isEmpty()) {
				for (Object[] appPermissionObj : appPermissionObjs) {
					String userName = appPermissionObj[0] != null
							&& !appPermissionObj[0].toString().trim().isEmpty()
									? String.valueOf(appPermissionObj[0]).trim()
									: null;
					if (userName != null && !userName.trim().isEmpty()) {
						Long userId = userCreationRepository
								.findIdByUserName(userName);

						if (userId != null) {
							String permCodeObj = appPermissionObj[1] != null
									&& !appPermissionObj[1].toString().trim()
											.isEmpty()
													? String.valueOf(
															appPermissionObj[1])
															.trim()
													: null;
							if (permCodeObj != null
									&& permCodeObj.contains(",")) {
								String[] permCodes = permCodeObj.split(",");
								List<String> permCodeList = Arrays
										.asList(permCodes);

								for (String permCode : permCodeList) {
									String perCode = permCode != null
											&& !permCode.trim().isEmpty()
													? permCode.trim() : null;
									if (perCode != null) {
										PermissionsMasterEntity permissionMaster = masterRepository
												.getPermissionCodeDetails(
														perCode);
										if (permissionMaster != null) {
											UserPermissionsEntity userpermissions = userPermissionsRepository
													.getUserPermissions(
															entityId, userId,
															perCode);
											if (userpermissions != null) {
												userpermissions
														.setApplicable(true);
												userPermissionEntities
														.add(userpermissions);
											} else {
												UserPermissionsEntity userPermissionsEntity = new UserPermissionsEntity();
												userPermissionsEntity
														.setPermCode(perCode
																.trim());
												userPermissionsEntity
														.setUserId(userId);
												userPermissionsEntity
														.setEntityId(entityId);
												userPermissionsEntity
														.setCreatedBy(user
																.getUserPrincipalName());
												userPermissionsEntity
														.setCreatedOn(EYDateUtil
																.toUTCDateTimeFromLocal(
																		LocalDateTime
																				.now()));
												userPermissionsEntity
														.setApplicable(true);
												userPermissionEntities.add(
														userPermissionsEntity);
											}
										} else {
											msg = "Invalid User Ids / permission as provided in file";
										}
									}
								}
							} else if (permCodeObj != null
									&& !permCodeObj.trim().isEmpty()) {
								PermissionsMasterEntity permissionMaster = masterRepository
										.getPermissionCodeDetails(permCodeObj);
								if (permissionMaster != null) {
									UserPermissionsEntity userpermissions = userPermissionsRepository
											.getUserPermissions(entityId,
													userId, permCodeObj.trim());
									if (userpermissions != null) {
										userpermissions.setApplicable(true);
										userPermissionEntities
												.add(userpermissions);
									} else {
										UserPermissionsEntity userPermissionsEntity = new UserPermissionsEntity();
										userPermissionsEntity
												.setPermCode(permCodeObj.trim());
										userPermissionsEntity.setUserId(userId);
										userPermissionsEntity
												.setEntityId(entityId);
										userPermissionsEntity.setCreatedBy(
												user.getUserPrincipalName());
										userPermissionsEntity
												.setCreatedOn(EYDateUtil
														.toUTCDateTimeFromLocal(
																LocalDateTime
																		.now()));
										userPermissionsEntity
												.setApplicable(true);
										userPermissionEntities
												.add(userPermissionsEntity);
									}
								} else {
									msg = "Invalid User Ids / permission as provided in file";
								}
							} else {
								msg = "Invalid User Ids / permission as provided in file";
							}
						} else {
							msg = "Please provide valid UserId and Permission";
						}
						if (msg == null && userPermissionEntities != null
								&& !userPermissionEntities.isEmpty()) {
							userPermissionsRepository
									.updatePermissionUser(entityId, userId);
						}
					} else {
						msg = "Please provide valid UserId and Permission";
					}
				}
			} else {
				msg = "Please provide valid UserId and Permission";
			}

		} else

		{
			msg = "Please provide valid UserId and Permission";
		}
		if (msg == null && userPermissionEntities != null
				&& !userPermissionEntities.isEmpty()) {

			userPermissionsRepository.saveAll(userPermissionEntities);
		}
		return msg;
	}
}
